package com.murzify.meetum.feature.auth.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.murzify.meetum.core.ui.resources.Res
import com.murzify.meetum.core.ui.resources.reset_url_email
import com.murzify.meetum.core.ui.resources.sign_in
import com.murzify.meetum.feature.auth.components.ResetPasswordComponent
import org.jetbrains.compose.resources.stringResource

@Composable
fun ResetPasswordUi(component: ResetPasswordComponent) {


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        EmailAnimation()

        Text(
            text = stringResource(Res.string.reset_url_email),
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )

        Button(
            onClick = component::onSignInClick
        ) {
            Text(stringResource(Res.string.sign_in))
        }
    }
}
