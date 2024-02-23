package com.murzify.meetum.core.data.repository

import com.benasher44.uuid.Uuid
import com.murzify.meetum.core.data.model.FirebaseService
import com.murzify.meetum.core.data.userEvents
import com.murzify.meetum.core.database.Services
import com.murzify.meetum.core.database.dao.ServiceDao
import com.murzify.meetum.core.database.model.toEntity
import com.murzify.meetum.core.domain.model.Service
import com.murzify.meetum.core.domain.repository.ServiceRepository
import com.murzify.meetum.meetumDispatchers
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.database.ChildEvent
import dev.gitlive.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Currency


class ServiceRepositoryImpl(
    private val serviceDao: ServiceDao
) : ServiceRepository {

    private val scope = CoroutineScope(meetumDispatchers.io)
    private val auth = Firebase.auth

    init {
        val job = Job()
        val syncScope = CoroutineScope(meetumDispatchers.io + job)
        scope.launch {
            auth.authStateChanged.collect { user ->
                job.children.forEach { it.cancelAndJoin() }
                user?.uid?.let { uid ->
                    syncScope.launch {
                        syncWithFirebase(uid)
                    }
                }
            }
        }
    }

    override suspend fun getAllServices() = serviceDao.getAll().map { serviceList ->
        serviceList.map {
            Service(
                it.name,
                it.price,
                Currency.getInstance(it.currency),
                Uuid.fromString(it.service_id)
            )
        }
    }

    override suspend fun addService(service: Service) {
        val uid = auth.currentUser?.uid!!
        val bookingRef =
            Firebase.database.reference("users/$uid/services/${service.id}")
        bookingRef.setValue(
            FirebaseService(
                service.name,
                service.price,
                service.currency.currencyCode
            )
        )
        serviceDao.add(service.toEntity())
    }

    override suspend fun deleteService(service: Service) {
        val uid = auth.currentUser?.uid!!
        Firebase.database.reference("users/$uid/services/${service.id}")
            .removeValue()
        serviceDao.delete(service.toEntity())
    }

    override suspend fun editService(service: Service) {
        val uid = auth.currentUser?.uid!!
        val bookingRef =
            Firebase.database.reference("users/$uid/services/${service.id}")
        bookingRef.setValue(
            FirebaseService(
                service.name,
                service.price,
                service.currency.currencyCode
            )
        )
        serviceDao.edit(service = service.toEntity())
    }

    private suspend fun syncWithFirebase(userId: String) {
        userEvents<FirebaseService>(userId, "services") { key, value, type ->
            val servicesEntity = Services(
                key!!,
                value.name,
                value.price,
                value.currency
            )
            when (type) {
                ChildEvent.Type.ADDED -> serviceDao.add(servicesEntity)
                ChildEvent.Type.CHANGED -> serviceDao.edit(servicesEntity)
                ChildEvent.Type.MOVED -> {}
                ChildEvent.Type.REMOVED -> serviceDao.delete(servicesEntity)
            }
        }
    }

}