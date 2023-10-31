package com.murzify.meetum.feature.services.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.murzify.meetum.core.ui.TextField
import com.murzify.meetum.core.ui.Toolbar
import com.murzify.meetum.feature.services.R
import com.murzify.meetum.feature.services.components.AddServiceComponent
import kotlinx.coroutines.launch
import java.util.Currency

@Composable
internal fun AddServiceUi(
    component: AddServiceComponent
) {
    val name by component.name.collectAsState()
    val isNameError by component.isNameError.collectAsState()
    val price by component.price.collectAsState()
    val isPriceError by component.isPriceError.collectAsState()
    val currency by component.currency.collectAsState()
    val showAlert by component.showAlert.collectAsState()
    val showDeleteButton by component.showDeleteButton.collectAsState()

    Toolbar(
        title = {
            Text(
                text = stringResource(id = R.string.new_service)
            )
        },
        onBackClicked = component::onBackClick,
        fab = {
            FabBar(
                showDeleteButton = showDeleteButton,
                onDeleteClick = component::onDeleteClick,
                onDeleteCanceled = component::onDeleteCanceled,
                showAlert = showAlert,
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
            TextField(
                value = name,
                onValueChange = component::onNameChanged,
                label = { Text(text = stringResource(id = R.string.service_name)) },
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .width(200.dp),
                isError = isNameError,
            )

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {

                TextField(
                    value = price,
                    onValueChange = component::onPriceChanged,
                    label = { Text(text = stringResource(R.string.price)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    isError = isPriceError
                )

                CurrencyField(
                    currency,
                    onCurrencyChanged = component::onCurrencyChanged
                )
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
                modifier = Modifier.padding(16.dp),
                onClick = onDeleteClick,
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.error
            ) {
                Icon(
                    painter = painterResource(id = com.murzify.ui.R.drawable.round_delete_outline_24),
                    contentDescription = stringResource(id = R.string.delete_service)
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
                            text = stringResource(id = R.string.you_have_records),
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(horizontalArrangement = Arrangement.SpaceBetween) {
                            TextButton(
                                onClick = onDeleteCanceled,
                            ) {
                                Text(stringResource(id = R.string.cancel_delete))
                            }
                            TextButton(
                                onClick = onDeleteConfirmed,
                            ) {
                                Text(stringResource(id = R.string.confirm_delete))
                            }
                        }

                    }
                }
            }

        }

        FloatingActionButton(
            modifier = Modifier.padding(16.dp),
            onClick = onSaveClick
        ) {
            Text(
                text = stringResource(id = R.string.save),
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
    var options by remember {
        mutableStateOf(currencies)
    }
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember {
        mutableStateOf(default.currencyCode)
    }
    var selectedCurrency by remember {
        mutableStateOf(default)
    }
    val coroutineScope = rememberCoroutineScope()


    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.width(150.dp)
    ) {

        TextField(
            modifier = Modifier.menuAnchor(),
            value = selectedOptionText,
            onValueChange = {
                selectedOptionText = it
                coroutineScope.launch {
                    options = currencies.filter { currency ->
                        currency.currencyCode.startsWith(it.uppercase())
                    }
                }
                expanded = true
            },
            label = { Text(stringResource(id = R.string.currency)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            maxLines = 1,
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                if (selectedOptionText.isEmpty())  {
                    onCurrencyChanged(null)
                }
            },
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption.currencyCode) },
                    onClick = {
                        selectedOptionText = selectionOption.currencyCode
                        selectedCurrency = selectionOption
                        expanded = false
                        onCurrencyChanged(selectionOption)
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}