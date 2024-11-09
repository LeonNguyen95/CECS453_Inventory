package com.example.inventory.ui.item

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inventory.InventoryTopAppBar
import com.example.inventory.R
import com.example.inventory.ui.AppViewModelProvider
import com.example.inventory.ui.navigation.NavigationDestination
import com.example.inventory.ui.theme.InventoryTheme

object ItemOrderSummaryDestination : NavigationDestination {
    override val route = "item_order_summary"
    override val titleRes = R.string.order_summary
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemOrderSummaryScreen(
    navigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ItemDetailsViewModel =viewModel(factory = AppViewModelProvider.Factory)
){
    val uiState = viewModel.uiState.collectAsState()

    val quantityToOrder = uiState.value.itemDetails.toItem().soldQuantity
    val price = uiState.value.itemDetails.toItem().price
    val totalPrice = price * quantityToOrder

    Scaffold(
        topBar = {
            InventoryTopAppBar(
                title = stringResource(ItemOrderSummaryDestination.titleRes),
                canNavigateBack = false
            ) },
        modifier = modifier
    ) {
        innerPadding ->
        ItemOrderSummaryBody(
            itemDetailsUiState = uiState.value,
            quantityToOrder = quantityToOrder.toString(),
            totalPrice = totalPrice.toString(),
            onHomeClick = navigateToHome,
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding()
                )
                .verticalScroll(rememberScrollState())
        )
    }
}

@Composable
fun ItemOrderSummaryBody(
    itemDetailsUiState: ItemDetailsUiState,
    quantityToOrder: String,
    totalPrice: String,
    onHomeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        // Item details
        ItemDetails(
            item = itemDetailsUiState.itemDetails.toItem(),
            modifier = Modifier.fillMaxWidth()
        )
        // Order details
        OrderDetails(
            quantityToOrder = quantityToOrder,
            totalPrice = totalPrice
        )
        // home button
        Button(
            onClick = onHomeClick,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small
        ) {
            Text(stringResource(R.string.home))
        }
    }
}

@Composable
fun OrderDetails(
    quantityToOrder: String,
    totalPrice: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.padding_medium)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
        ){
            Row {
                Text(
                    stringResource(R.string.quantity_ordered),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(quantityToOrder)
            }
            Row {
                Text(
                    stringResource(R.string.total_price),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(totalPrice)
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun ItemOrderSummaryScreenPreview() {
    InventoryTheme {
        OrderDetails("5", "500.0")
    }
}