package com.murzify.meetum.feature.auth.components

import com.arkivanov.decompose.ComponentContext
import com.murzify.meetum.core.common.ComponentFactory
import com.murzify.meetum.core.common.componentCoroutineScope
import com.murzify.meetum.core.domain.repository.FirebaseRepository
import com.murzify.meetum.meetumDispatchers
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.get

fun ComponentFactory.createCheckEmailComponent(
    componentContext: ComponentContext,
    navigateToCalendar: () -> Unit,
    navigateToRegister: () -> Unit
) = RealCheckEmailComponent(
    componentContext,
    get(),
    navigateToCalendar,
    navigateToRegister
)

class RealCheckEmailComponent(
    componentContext: ComponentContext,
    firebaseRepo: FirebaseRepository,
    private val navigateToCalendar: () -> Unit,
    private val navigateToRegister: () -> Unit
) : ComponentContext by componentContext, CheckEmailComponent {

    private val scope = componentCoroutineScope()
    private val auth = Firebase.auth

    init {
        scope.launch {
            val idToken = auth.currentUser?.getIdToken(true)
            if (idToken != null) {
                firebaseRepo.sendEmailVerification(idToken)
            } else {
                navigateToRegister()
                return@launch
            }
            while (!firebaseRepo.getUserData(idToken).emailVerified) {
                delay(1000)
            }
            withContext(meetumDispatchers.main) {
                navigateToCalendar()
            }
        }
    }
}