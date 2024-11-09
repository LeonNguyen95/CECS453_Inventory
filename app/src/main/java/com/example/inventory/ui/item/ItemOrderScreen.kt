package com.example.inventory.ui.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inventory.InventoryTopAppBar
import com.example.inventory.R
import com.example.inventory.ui.AppViewModelProvider
import com.example.inventory.ui.home.ErrorMessage
import com.example.inventory.ui.navigation.NavigationDestination
import com.example.inventory.ui.theme.InventoryTheme

object ItemOrderDestination : NavigationDestination {
    override val route = "item_order"
    override val titleRes = R.string.item_order_title
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemOrderScreen(
    navigateToItemOrderSummary: (Int) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ItemDetailsViewModel = viewModel(
        key = "sharedViewModelKey",
        factory = AppViewModelProvider.Factory)
) {
    val uiState = viewModel.uiState.collectAsState()

    var quantityToOrder by remember { mutableStateOf("0") }
    var invalidQuantityToOrder by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            InventoryTopAppBar(
                title = stringResource(ItemOrderDestination.titleRes),
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        },
        modifier = modifier
    ) { innerPadding ->
        ItemOrderBody(
            itemDetailsUiState = uiState.value,
            quantityToOrder = quantityToOrder,
            onQuantityToOrderChange = { newQuantityToOrder ->
                quantityToOrder = newQuantityToOrder
            },
            onPlaceOrderClick = {
                val itemId = uiState.value.itemDetails.id
                if (quantityToOrder.toInt() > uiState.value.itemDetails.quantity.toInt()) {
                    invalidQuantityToOrder = true
                } else {
                    viewModel.reduceQuantityInStockByOrderedQuantity(quantityToOrder.toInt())
                    navigateToItemOrderSummary(itemId)
                }
            },
            onCancel = navigateBack,
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding()
                )
                .verticalScroll(rememberScrollState())
        )
        if (invalidQuantityToOrder) {
            ErrorMessage(
                message = "Input Quantity was greater than quantity in stock",
                onDismissRequest = { invalidQuantityToOrder = false}
            )
        }
    }
}

@Composable
fun ItemOrderBody(
    itemDetailsUiState: ItemDetailsUiState,
    quantityToOrder: String,
    onQuantityToOrderChange: (String) -> Unit,
    onPlaceOrderClick: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id =  R.dimen.padding_medium))
    ) {
        ItemDetails(
            item = itemDetailsUiState.itemDetails.toItem(),
            modifier = Modifier.fillMaxWidth()
        )
        // Quantity to Order input field
        QuantityToOrder(
            quantityToOrder = quantityToOrder,
            onQuantityToOrderChange = onQuantityToOrderChange
        )
        // Place Order Button
        Button(
            onClick = onPlaceOrderClick,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small,
            // enabled only when quantityToOrder is a number > 0 and an item not out of stock
            enabled = quantityToOrder.toIntOrNull() ?: 0 > 0 && !itemDetailsUiState.outOfStock
        ) {
            Text(stringResource(R.string.place_order))
        }
        // Cancel Button
        Button(
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small
        ) {
            Text(stringResource(R.string.cancel))
        }
    }
}

@Composable
fun QuantityToOrder(
    quantityToOrder: String,
    onQuantityToOrderChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var hasFocus by remember { mutableStateOf(false) }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(id = R.dimen.padding_medium)
                )
        ) {
            Text(stringResource(R.string.quantity_to_order))
            Spacer(modifier = Modifier.weight(1f))
            OutlinedTextField(
                value = quantityToOrder,
                onValueChange = onQuantityToOrderChange,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .width(60.dp)
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused && !hasFocus) {
                            onQuantityToOrderChange("")
                            hasFocus = true
                        }
                        if (!focusState.isFocused) {
                            hasFocus = false
                        }
                    }
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun ItemOrderScreenPreview() {
    InventoryTheme {
        QuantityToOrder(
            quantityToOrder = "0",
            onQuantityToOrderChange = {},
        )
    }
}