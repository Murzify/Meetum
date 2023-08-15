package com.murzify.meetum.feature.services

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.murzify.meetum.core.domain.model.Service
import kotlinx.coroutines.launch
import java.util.Currency
import java.util.Locale
import java.util.UUID

@Composable
internal fun AddServiceRoute(
    viewModel: ServicesViewModel = hiltViewModel(),
    isEditing: Boolean,
    navigateToBack: () -> Unit
) {
    val editingService by viewModel.selectedService.collectAsState()
    Log.d("addService", editingService.toString())
    AddServiceScreen(
        isEditing,
        editingService,
        navigateToBack,
        save = {
            if (isEditing) {
                Log.d("edit", it.toString())
                viewModel.editService(it)
            } else {
                viewModel.addService(it)
            }
        },
        delete = {
            editingService?.let { service -> viewModel.deleteService(service) }
        }
    )
}

@Preview
@Composable
internal fun AddServiceScreen(
    isEditing: Boolean = false,
    editingService: Service? = null,
    navigateToBack: () -> Unit = {},
    save: (Service) -> Unit = {},
    delete: () -> Unit = {}
) {
    val nameDefault = if (isEditing) {
        editingService?.name ?: ""
    } else ""
    var name by rememberSaveable {
        mutableStateOf(nameDefault)
    }
    val priceDefault = if (isEditing) {
        editingService?.price.toString()
    } else ""
    var priceAmount by rememberSaveable {
        mutableStateOf(priceDefault)
    }
    val currencyDefault = if (isEditing) {
        editingService?.currency
    } else null
    var currency by rememberSaveable {
        mutableStateOf(currencyDefault)
    }

    var nameError by rememberSaveable {
        mutableStateOf(false)
    }
    var priceError by rememberSaveable {
        mutableStateOf(false)
    }
    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {

        IconButton(modifier = Modifier.statusBarsPadding(), onClick = { navigateToBack() }) {
            Icon(
                painter = painterResource(id = com.murzify.ui.R.drawable.round_arrow_back_24),
                contentDescription = stringResource(id = R.string.back_button)
            )
        }

        OutlinedTextField(
            value = name,
            onValueChange = {
                nameError = false
                name = it
                            },
            label = { Text(text = stringResource(id = R.string.service_name)) },
            modifier = Modifier.padding(vertical = 16.dp),
            isError = nameError,
        )

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {

            OutlinedTextField(
                value = priceAmount,
                onValueChange = {
                    priceError = false
                    val text = it.replace(",", ".")
                    priceAmount = text
                },
                label = { Text(text = stringResource(R.string.price)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                isError = priceError
            )

            CurrencyField(isEditing, currencyDefault) { c ->
                currency = c
            }
        }
        
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(){
            if (isEditing) {
                FloatingActionButton(
                    modifier = Modifier.padding(16.dp),
                    onClick = {
                        delete()
                        navigateToBack()
                    },
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.error
                ) {
                    Icon(
                        painter = painterResource(id = com.murzify.ui.R.drawable.round_delete_outline_24),
                        contentDescription = stringResource(id = R.string.delete_service)
                    )
                }
            }

            FloatingActionButton(
                modifier = Modifier.padding(16.dp),
                onClick = {
                    nameError = name.isEmpty()
                    try {
                        priceAmount.toDouble()
                    } catch (_: Throwable) {
                        Log.d("price", "error")
                        priceError = true
                    }
                    val c = if (currency == null) {
                        Currency.getInstance(Locale.getDefault())
                    } else {
                        currency
                    }

                    if (!nameError && !priceError) {
                        save(
                            Service(
                                name,
                                priceAmount.toDouble(),
                                c!!,
                                if (isEditing && editingService != null) editingService.id
                                else UUID.randomUUID()
                            )
                        )
                        navigateToBack()
                    }
                }
            ) {
                Text(
                    text = stringResource(id = R.string.save),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CurrencyField(
    isEditing: Boolean = false,
    default: Currency? = null,
    onCurrencyChanged: (currency: Currency?) -> Unit
) {
    val defaultCurrency = if (isEditing) {
        default ?: Currency.getInstance(Locale.getDefault())
    } else Currency.getInstance(Locale.getDefault())
    val currencies = Currency.getAvailableCurrencies().toSortedSet(
        compareBy {
            it.currencyCode
        }
    )
    val options = remember {
        currencies
    }
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember {
        mutableStateOf(defaultCurrency.currencyCode)
    }
    var selectedCurrency by remember {
        mutableStateOf(defaultCurrency)
    }
    val coroutineScope = rememberCoroutineScope()


    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.width(150.dp)
    ) {

        OutlinedTextField(
            modifier = Modifier.menuAnchor(),
            value = selectedOptionText,
            onValueChange = {
                val prevText = selectedOptionText
                selectedOptionText = it
                coroutineScope.launch {
                    if (prevText.length > selectedOptionText.length) {
                        options.addAll(currencies.minus(options).filter { currency ->
                            currency.currencyCode.startsWith(it.uppercase())
                        })
                    } else {
                        options.removeIf { currency ->
                            !currency.currencyCode.startsWith(it.uppercase())
                        }
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