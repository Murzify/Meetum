package com.murzify.meetum.feature.calendar.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.murzify.meetum.MR
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.compose.stringResource
import dev.icerock.moko.resources.desc.Plural
import dev.icerock.moko.resources.desc.StringDesc
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
actual fun ImportContactButton(
    modifier: Modifier,
    onClick: (name: String, phone: String) -> Unit,
) {
    StringDesc.Plural(MR.plurals.day, 18).localized()
    var name = ""
    var phone = ""

    val importContact = remember { mutableStateOf(false) }
    val contentResolver = LocalContext.current.contentResolver
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickContact()
    ) {
        it?.let { uri ->
            val contactFields = arrayOf(
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts._ID,
            )
            val cursor = contentResolver.query(
                uri,
                contactFields,
                null,
                null,
                null
            ) ?: return@let

            cursor.use { cur ->
                if (cur.count == 0) return@let
                cur.moveToFirst()
                name = cur.getString(0)
                val contactId = cur.getString(1)
                val phonesFields = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val phones = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, phonesFields,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                    null, null
                ) ?: return@let
                phones.use { ph ->
                    ph.moveToFirst()
                    phone = ph.getString(0)
                }
            }
            onClick(name, phone)
        }
    }

    if (importContact.value) {
        RequestContactsPermission() {
            LaunchedEffect(key1 = launcher) {
                launcher.launch(null)
                importContact.value = false
            }
        }
    }

    IconButton(
        modifier = modifier,
        onClick = {
            importContact.value = true
        },
    ) {
        Icon(painter = painterResource("drawable/round_import_contacts_24.xml"),
            contentDescription = stringResource(MR.strings.import_contact)
        )
    }
}

@Composable
private fun RequestContactsPermission(onGranted: @Composable () -> Unit) {
    var isGranted = remember { mutableStateOf(false) }
    if (isGranted.value) {
        onGranted()
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            isGranted.value = true
        }
    }
    val activity = LocalContext.current.findActivity()
    if (!hasContactsPermission(activity)) {
        SideEffect {
            launcher.launch(Manifest.permission.READ_CONTACTS)
        }
    }
}

private fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("no activity")
}

private fun hasContactsPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) ==
            PackageManager.PERMISSION_GRANTED
}