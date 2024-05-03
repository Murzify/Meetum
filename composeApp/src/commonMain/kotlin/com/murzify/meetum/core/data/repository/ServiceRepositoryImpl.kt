package com.murzify.meetum.core.data.repository

import com.benasher44.uuid.Uuid
import com.murzify.meetum.core.data.FirebaseSync
import com.murzify.meetum.core.data.model.FirebaseService
import com.murzify.meetum.core.data.toFirebase
import com.murzify.meetum.core.data.userEvents
import com.murzify.meetum.core.database.Services
import com.murzify.meetum.core.database.dao.ServiceDao
import com.murzify.meetum.core.database.model.toEntity
import com.murzify.meetum.core.domain.model.Service
import com.murzify.meetum.core.domain.repository.ServiceRepository
import com.murzify.meetum.meetumDispatchers
import dev.gitlive.firebase.database.ChildEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Currency


class ServiceRepositoryImpl(
    private val serviceDao: ServiceDao
) : ServiceRepository, FirebaseSync() {

    private val scope = CoroutineScope(meetumDispatchers.io)

    init {
        val job = Job()
        val syncScope = CoroutineScope(meetumDispatchers.io + job)
        scope.launch {
            auth.getUid { uid ->
                job.children.forEach { it.cancelAndJoin() }
                syncScope.launch {
                    syncWithFirebase(uid)
                }
            }
        }

        scope.launch {
            serviceDao.unsyncedServices.sync { servicesEntity, uid ->
                val bookingRef = db
                    .reference("users/$uid/services/${servicesEntity.service_id}")
                bookingRef.setValue(
                    servicesEntity.toFirebase()
                )
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
        serviceDao.add(service.toEntity(false))
    }

    override suspend fun deleteService(service: Service) {
        serviceDao.markForDeletion(service.id.toString())
    }

    override suspend fun editService(service: Service) {
        serviceDao.edit(service = service.toEntity(false))
    }

    private suspend fun syncWithFirebase(userId: String) {
        userEvents<FirebaseService>(userId, "services") { key, value, type ->
            val servicesEntity = Services(
                key!!,
                value.name,
                value.price,
                value.currency,
                deleted = value.deleted,
                synced = true
            )
            when (type) {
                ChildEvent.Type.ADDED -> serviceDao.addOrReplace(servicesEntity)
                ChildEvent.Type.CHANGED -> serviceDao.edit(servicesEntity)
                ChildEvent.Type.MOVED -> {}
                ChildEvent.Type.REMOVED -> {}
            }
        }
    }

}