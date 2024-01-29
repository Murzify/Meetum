package com.murzify.meetum.core.domain.repository

import com.murzify.meetum.core.domain.model.FirebaseUser

interface FirebaseRepository {

    suspend fun createUser(email: String, password: String): String

    suspend fun sendEmailVerification(idToken: String)

    suspend fun getUserData(idToken: String): FirebaseUser
}