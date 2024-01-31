package com.murzify.meetum.feature.auth.components

import com.arkivanov.decompose.router.stack.ChildStack
import kotlinx.coroutines.flow.StateFlow

interface AuthComponent {

    val childStack: StateFlow<ChildStack<*, Child>>

    sealed interface Child {
        class Register(val component: RegisterComponent): Child

        class SignIn(val component: SignInComponent): Child

        class CheckEmail(val component: CheckEmailComponent): Child
    }

}