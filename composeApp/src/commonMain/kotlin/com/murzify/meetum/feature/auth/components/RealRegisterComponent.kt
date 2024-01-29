package com.murzify.meetum.feature.auth.components

import com.arkivanov.decompose.ComponentContext
import com.murzify.meetum.core.common.ComponentFactory
import com.murzify.meetum.core.common.componentCoroutineScope
import com.murzify.meetum.core.domain.model.ErrorEntity
import com.murzify.meetum.core.domain.repository.FirebaseRepository
import com.murzify.meetum.feature.auth.components.RegisterComponent.Error
import com.murzify.meetum.feature.auth.components.RegisterComponent.Model
import com.murzify.meetum.meetumDispatchers
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.get

fun ComponentFactory.createRegisterComponent(
    componentContext: ComponentContext,
    navigateToSignIn: () -> Unit,
    navigateToCalendar: () -> Unit,
): RegisterComponent = RealRegisterComponent(
    componentContext,
    navigateToSignIn,
    navigateToCalendar,
    get()
)

class RealRegisterComponent(
    componentContext: ComponentContext,
    private val navigateToSignIn: () -> Unit,
    private val navigateToCalendar: () -> Unit,
    private val firebaseRepo: FirebaseRepository
) : ComponentContext by componentContext, RegisterComponent {
    override val model: MutableStateFlow<Model> = MutableStateFlow(
        Model(
            email = "misha.murzin.mm@gmail.com",
            password = "qwerty123",
            confirmPassword = "qwerty123",
            error = null,
            emailConfirmation = false,
            loading = false
        )
    )

    private val scope = componentCoroutineScope()
    private val auth = Firebase.auth

    init {
        model.update { it.copy(emailConfirmation = auth.currentUser?.isEmailVerified == false) }
    }

    override fun onEmailChange(email: String) {
        model.update { it.copy(email = email) }
    }

    override fun onPasswordChange(password: String) {
        model.update { it.copy(password = password) }
    }

    override fun onConfirmPasswordChange(confirmPassword: String) {
        model.update { it.copy(confirmPassword = confirmPassword) }
    }

    override fun onRegisterClick() {
        scope.launch(meetumDispatchers.io) {
            model.value.apply {
                if (password != confirmPassword) {
                    setError(Error.DIFFERENT_PASSWORDS)
                    return@launch
                }
                if (password.length < 6) {
                    setError(Error.SHORT_PASSWORD)
                    return@launch
                }
                val regex = Regex("([a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.[a-zA-Z0-9_-]+)")
                if (!regex.matches(email)) {
                    setError(Error.INCORRECT_EMAIL)
                    return@launch
                }
            }
            try {
                model.update { it.copy(loading = true) }
                val email = model.value.email
                val password = model.value.password
                firebaseRepo.createUser(email, password)
                auth.signInWithEmailAndPassword(email, password)

                val idToken = auth.currentUser?.getIdToken(true)
                if (idToken != null) {
                    firebaseRepo.sendEmailVerification(idToken)
                } else return@launch
                model.update { it.copy(loading = false) }
                model.update { it.copy(emailConfirmation = true) }
                
                launch {
                    while (!firebaseRepo.getUserData(idToken).emailVerified) {
                        delay(1000)
                    }
                    withContext(meetumDispatchers.main) {
                        navigateToCalendar()
                    }
                }
            } catch (e: ErrorEntity.EmailExists) {
                model.update { it.copy(loading = false) }
                setError(Error.EMAIL_EXISTS)
            }

        }
    }

    override fun onSignInClick() {
        navigateToSignIn()
    }

    private fun setError(error: Error) {
        model.update { it.copy(error = error) }
    }

}