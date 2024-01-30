package com.murzify.meetum.feature.auth.components

import com.arkivanov.decompose.ComponentContext
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.murzify.meetum.core.common.ComponentFactory
import com.murzify.meetum.core.common.componentCoroutineScope
import com.murzify.meetum.core.domain.model.ErrorEntity
import com.murzify.meetum.core.domain.repository.FirebaseRepository
import com.murzify.meetum.feature.auth.components.SignInComponent.Error
import com.murzify.meetum.feature.auth.components.SignInComponent.Model
import com.murzify.meetum.meetumDispatchers
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuthInvalidCredentialsException
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.get

fun ComponentFactory.createSignInComponent(
    componentContext: ComponentContext,
    navigateToRegister: () -> Unit,
    navigateToCalendar: () -> Unit
): SignInComponent = RealSignInComponent(
    componentContext,
    get(),
    navigateToRegister,
    navigateToCalendar
)

class RealSignInComponent(
    componentContext: ComponentContext,
    private val firebaseRepo: FirebaseRepository,
    private val navigateToRegister: () -> Unit,
    private val navigateToCalendar: () -> Unit
): ComponentContext by componentContext, SignInComponent {

    override val model = MutableStateFlow(Model(
        "",
        "",
        false,
        null
    ))

    private val scope = componentCoroutineScope()
    private val auth = Firebase.auth

    override fun onEmailChange(email: String) {
        model.update { it.copy(email = email) }
    }

    override fun onPasswordChange(password: String) {
        model.update { it.copy(password = password) }
    }

    override fun onSignInClick() {
        model.update { it.copy(loading = true) }
        model.value.let {
            scope.launch {
                try {
                    auth.signInWithEmailAndPassword(it.email, it.password)
                    withContext(meetumDispatchers.main) {
                        navigateToCalendar()
                    }
                } catch (_: FirebaseAuthInvalidCredentialsException) {
                    model.update { it.copy(error = Error.INVALID_CREDENTIALS) }
                } catch (_: FirebaseAuthInvalidUserException) {
                    model.update { it.copy(error = Error.INVALID_CREDENTIALS) }
                }
                model.update { it.copy(loading = false) }
            }
        }
    }

    override fun onRegisterClick() {
        navigateToRegister()
    }

    override fun onForgotPasswordClick() {
        scope.launch {
            try {
                firebaseRepo.resetPassword(model.value.email)
            } catch (e: ErrorEntity.MissingEmail) {
                model.update { it.copy(error = Error.MISSING_EMAIL) }
            } catch (e: ErrorEntity.InvalidEmail) {
                model.update { it.copy(error = Error.INVALID_EMAIL) }
            }
        }
    }
}