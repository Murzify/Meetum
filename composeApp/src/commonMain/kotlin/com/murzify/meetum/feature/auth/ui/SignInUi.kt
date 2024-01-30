package com.murzify.meetum.feature.auth.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
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
import com.murzify.meetum.MR
import com.murzify.meetum.core.ui.LoadingButton
import com.murzify.meetum.core.ui.TextField
import com.murzify.meetum.feature.auth.components.SignInComponent
import com.murzify.meetum.feature.auth.components.SignInComponent.Error
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun SignInUi(component: SignInComponent) {
    val model by component.model.collectAsState()

    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.systemBarsPadding().imePadding()
        ) {
            TextField(
                modifier = Modifier.width(250.dp),
                onValueChange = component::onEmailChange,
                value = model.email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                label = {
                    Text(stringResource(MR.strings.email))
                }
            )
            TextField(
                modifier = Modifier.width(250.dp),
                onValueChange = component::onPasswordChange,
                value = model.password,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                label = {
                    Text(stringResource(MR.strings.password))
                }
            )

            Text(
                stringResource(MR.strings.forgot_password),
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable(
                        onClick = component::onForgotPasswordClick
                    )
            )

            val errorText = when (model.error) {
                Error.INVALID_CREDENTIALS -> stringResource(MR.strings.invalid_credentials)
                Error.MISSING_EMAIL -> stringResource(MR.strings.enter_email)
                Error.INVALID_EMAIL -> stringResource(MR.strings.incorrect_email)
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
                onClick = component::onSignInClick,
                loading = model.loading,
                modifier = Modifier.size(width = 150.dp, height = 50.dp)
            ) {
                Text(stringResource(MR.strings.sign_in))
            }

            TextButton(
                onClick = component::onRegisterClick,
                modifier = Modifier.size(width = 150.dp, height = 50.dp)
            ) {
                Text(stringResource(MR.strings.sign_up))
            }
        }
    }

}