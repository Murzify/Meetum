package com.murzify.meetum.core.data.repository

import com.murzify.meetum.core.domain.model.ErrorEntity
import com.murzify.meetum.core.domain.model.FirebaseUser
import com.murzify.meetum.core.domain.repository.FirebaseRepository
import com.murzify.meetum.core.network.FirebaseAuth
import com.murzify.meetum.core.network.model.FirebaseError
import com.murzify.meetum.core.network.model.LookupResponse
import com.murzify.meetum.core.network.model.PasswordSignUpResult
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode

class FirebaseRepositoryImpl(private val auth: FirebaseAuth) : FirebaseRepository {
    override suspend fun createUser(email: String, password: String): String {
        val resp = auth.createUser(email, password)
        resp.throwIfNotOk()
        val signInResult = resp.body<PasswordSignUpResult>()
        return signInResult.idToken
    }

    override suspend fun sendEmailVerification(idToken: String) {
        auth.sendEmailVerification(idToken).throwIfNotOk()
    }

    override suspend fun getUserData(idToken: String): FirebaseUser {
        val resp = auth.lookup(idToken)
        resp.throwIfNotOk()
        return resp.body<LookupResponse>().users.first()
    }

    override suspend fun resetPassword(email: String) {
        auth.resetPassword(email).throwIfNotOk()
    }

    private suspend fun HttpResponse.throwIfNotOk() {
        if (status != HttpStatusCode.OK) {
            throw ErrorEntity[body<FirebaseError>().error.message]
        }
    }
}