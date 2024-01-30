package com.murzify.meetum.feature.auth.ui

import KottieAnimation
import KottieCompositionSpec
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import animateKottieCompositionAsState
import com.murzify.meetum.MR
import com.murzify.meetum.core.ui.LoadingButton
import com.murzify.meetum.core.ui.TextField
import com.murzify.meetum.feature.auth.components.RegisterComponent
import com.murzify.meetum.feature.auth.components.RegisterComponent.Model
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.resource
import rememberKottieComposition

@Composable
fun RegisterUi(component: RegisterComponent) {
    val model by component.model.collectAsState()

    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {

        if (model.emailConfirmation) {
            EmailConfirmation()
        } else {
            Register(
                model,
                component::onEmailChange,
                component::onPasswordChange,
                component::onConfirmPasswordChange,
                component::onRegisterClick,
                component::onSignInClick
            )
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun Register(
    model: Model,
    onEmailChange: (email: String) -> Unit,
    onPasswordChange: (password: String) -> Unit,
    onConfirmPasswordChange: (confirmPassword: String) -> Unit,
    onRegisterClick: () -> Unit,
    onSignInClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.systemBarsPadding().imePadding()
    ) {
        MeetumBanner()

        TextField(
            modifier = Modifier.width(250.dp),
            onValueChange = onEmailChange,
            value = model.email,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            label = {
                Text(stringResource(MR.strings.email))
            }
        )
        TextField(
            modifier = Modifier.width(250.dp),
            onValueChange = onPasswordChange,
            value = model.password,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            label = {
                Text(stringResource(MR.strings.password))
            }
        )
        TextField(
            modifier = Modifier.width(250.dp),
            onValueChange = onConfirmPasswordChange,
            value = model.confirmPassword,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            label = {
                Text(stringResource(MR.strings.confirm_password))
            }
        )

        val errorText = when (model.error) {
            RegisterComponent.Error.DIFFERENT_PASSWORDS -> stringResource(MR.strings.different_passwords_error)
            RegisterComponent.Error.SHORT_PASSWORD -> stringResource(MR.strings.short_password_error)
            RegisterComponent.Error.INCORRECT_EMAIL -> stringResource(MR.strings.incorrect_email)
            RegisterComponent.Error.EMAIL_EXISTS -> stringResource(MR.strings.user_exists_error)
            null -> ""
        }

        AnimatedVisibility(
            visible = errorText.isNotEmpty()
        ) {
            Text(
                text = errorText,
                color = MaterialTheme.colorScheme.error
            )
        }

        LoadingButton(
            onClick = onRegisterClick,
            loading = model.loading,
            modifier = Modifier.size(width = 150.dp, height = 50.dp)
        ) {
            Text(stringResource(MR.strings.sign_up))
        }

        TextButton(
            onClick = onSignInClick,
            modifier = Modifier.size(width = 150.dp, height = 50.dp)
        ) {
            Text(stringResource(MR.strings.sign_in))
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun EmailConfirmation() {
    val composition = rememberKottieComposition(
        spec = KottieCompositionSpec.File(resource("lottie/email_verif.json"))
    )
    val animationState by animateKottieCompositionAsState(
        composition = composition,
        speed = 1f,
        iterations = 1
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        KottieAnimation(
            composition = composition,
            progress = { animationState.progress },
            modifier = Modifier.size(200.dp)
        )

        Text(
            text = stringResource(MR.strings.check_email),
            fontSize = 24.sp,
            modifier = Modifier.padding(top = 16.dp)
        )
    }


}