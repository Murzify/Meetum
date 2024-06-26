package com.murzify.meetum.feature.services.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.murzify.meetum.MR
import com.murzify.meetum.core.ui.TextField
import com.murzify.meetum.core.ui.Toolbar
import com.murzify.meetum.core.ui.moveFocusDown
import com.murzify.meetum.feature.services.components.AddServiceComponent
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import java.util.Currency

@Composable
internal fun AddServiceUi(
    component: AddServiceComponent
) {
    val model by component.model.collectAsState()

    Toolbar(
        title = {
            Text(
                text = if (model.service != null) {
                    stringResource(MR.strings.service)
                } else stringResource(MR.strings.new_service)
            )
        },
        onBackClicked = component::onBackClick,
        fab = {
            FabBar(
                showDeleteButton = model.showDeleteButton,
                onDeleteClick = component::onDeleteClick,
                onDeleteCanceled = component::onDeleteCanceled,
                showAlert = model.showAlert,
                onDeleteConfirmed = component::onDeleteConfirmed,
                onSaveClick = component::onSaveClick
            )
        },
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(16.dp)
        ) {
            val focusManager = LocalFocusManager.current
            TextField(
                value = model.name,
                onValueChange = component::onNameChanged,
                label = { Text(text = stringResource(MR.strings.service_name)) },
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .width(200.dp)
                    .moveFocusDown(focusManager),
                isError = model.isNameError,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            Row(
            ) {

                TextField(
                    value = model.price,
                    onValueChange = component::onPriceChanged,
                    label = { Text(text = stringResource(MR.strings.price)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus(true)
                        }
                    ),
                    modifier = Modifier
                        .padding(end = 8.dp),
                    isError = model.isPriceError
                )

                CurrencyField(
                    model.currency,
                    onCurrencyChanged = component::onCurrencyChanged
                )
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
@Composable
private fun FabBar(
    showDeleteButton: Boolean,
    onDeleteClick: () -> Unit,
    onDeleteCanceled: () -> Unit,
    showAlert: Boolean,
    onDeleteConfirmed: () -> Unit,
    onSaveClick: () -> Unit
) {
    Row(){
        if (showDeleteButton) {
            FloatingActionButton(
                modifier = Modifier.padding(horizontal = 16.dp),
                onClick = onDeleteClick,
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.error
            ) {
                Icon(
                    painter = painterResource("drawable/round_delete_outline_24.xml"),
                    contentDescription = stringResource(MR.strings.delete_service)
                )
            }
        }

        if (showAlert) {
            AlertDialog(onDismissRequest = onDeleteCanceled) {
                Surface(
                    modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight(),
                    shape = MaterialTheme.shapes.large,
                    tonalElevation = AlertDialogDefaults.TonalElevation
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(MR.strings.you_have_records),
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(horizontalArrangement = Arrangement.SpaceBetween) {
                            TextButton(
                                onClick = onDeleteCanceled,
                            ) {
                                Text(stringResource(MR.strings.cancel_delete))
                            }
                            TextButton(
                                onClick = onDeleteConfirmed,
                            ) {
                                Text(stringResource(MR.strings.confirm_delete))
                            }
                        }

                    }
                }
            }

        }

        FloatingActionButton(
            modifier = Modifier.padding(horizontal = 16.dp),
            onClick = onSaveClick
        ) {
            Text(
                text = stringResource(MR.strings.save),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CurrencyField(
    default: Currency,
    onCurrencyChanged: (currency: Currency?) -> Unit
) {
    val currencies = Currency.getAvailableCurrencies().sortedBy { it.currencyCode }

    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember {
        mutableStateOf(default.currencyCode)
    }
    var selectedCurrency by remember {
        mutableStateOf(default)
    }
    val options = currencies.filter { currency ->
        currency.currencyCode.startsWith(selectedOptionText, ignoreCase = true)
    }
    var selected by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.width(150.dp)
    ) {

        TextField(
            modifier = Modifier.menuAnchor()
                .onFocusChanged {
                    if (!selected) {
                        runCatching {
                            onCurrencyChanged(
                                Currency.getInstance(selectedOptionText.uppercase())
                            )
                        }
                    }
                },
            value = selectedOptionText,
            onValueChange = {
                selectedOptionText = it
                selected = false
            },
            label = { Text(stringResource(MR.strings.currency)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            maxLines = 1
        )
        if (options.isNotEmpty()) {
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                },
            ) {
                options.take(2).forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption.currencyCode) },
                        onClick = {
                            selectedOptionText = selectionOption.currencyCode
                            selectedCurrency = selectionOption
                            expanded = false
                            selected = true
                            onCurrencyChanged(selectionOption)
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }
        }

    }
}