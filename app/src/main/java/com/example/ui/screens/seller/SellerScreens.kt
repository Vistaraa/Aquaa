package com.example.ui.screens.seller

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.Converters
import com.example.data.models.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.SellerViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SellerDashboardScreen(
    sellerViewModel: SellerViewModel,
    onNavigateToPipeline: () -> Unit,
    modifier: Modifier = Modifier
) {
    val earningsSummary by sellerViewModel.earningsSummary.collectAsState()
    val sellerProfile by sellerViewModel.sellerProfile.collectAsState()
    val orders by sellerViewModel.filteredSellerOrders.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Header Banner
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = sellerProfile?.brandName ?: "Metro Hydro Infrastructure Hub",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            color = AquaTealContainer,
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(1.dp, AquaTeal.copy(alpha = 0.2f))
                        ) {
                            Text(
                                text = "STATION #S1",
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = AquaTeal,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                    Text(
                        text = "Real-time utility grid telemetry, active tanker dispatches, and reservoir stock.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { showAddDialog = true },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AquaOchre)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Water SKU", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    OutlinedButton(
                        onClick = onNavigateToPipeline,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        val activeCount = orders.filter { it.status != OrderStatus.DELIVERED.displayName && it.status != OrderStatus.CANCELLED.displayName }.size
                        Text("Pipeline Orders ($activeCount)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }

        // Metrics Grid Row
        item {
            if (isLandscape) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Today's Revenue",
                        value = "$${String.format(Locale.US, "%,d", earningsSummary?.todayEarnings?.toInt() ?: 1420)}",
                        subtext = "↗ +14.2%",
                        bottomLabel = "Target: $2,000 / day",
                        badgeColor = AquaTeal,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Active Pipeline Orders",
                        value = "${orders.filter { it.status != OrderStatus.DELIVERED.displayName && it.status != OrderStatus.CANCELLED.displayName }.size}",
                        subtext = "1 Out for Delivery",
                        bottomLabel = "Avg fulfillment: 22 mins",
                        badgeColor = AquaTeal,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Main Reservoir Capacity",
                        value = "84%",
                        subtext = "15,000 L",
                        bottomLabel = "Pressure: 42.5 PSI",
                        badgeColor = AquaTeal,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Low Stock Alerts",
                        value = "0",
                        subtext = "STOCK OK",
                        bottomLabel = "0 items below 30% fill",
                        badgeColor = AquaTeal,
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        MetricCard(
                            title = "Today's Revenue",
                            value = "$1,420",
                            subtext = "↗ +14.2%",
                            bottomLabel = "Target: $2,000 / day",
                            badgeColor = AquaTeal,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "Active Pipeline Orders",
                            value = "2",
                            subtext = "1 Out for Delivery",
                            bottomLabel = "Avg fulfillment: 22 mins",
                            badgeColor = AquaTeal,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        MetricCard(
                            title = "Main Reservoir Capacity",
                            value = "84%",
                            subtext = "15,000 L",
                            bottomLabel = "Pressure: 42.5 PSI",
                            badgeColor = AquaTeal,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "Low Stock Alerts",
                            value = "0",
                            subtext = "STOCK OK",
                            bottomLabel = "0 items below 30% fill",
                            badgeColor = AquaTeal,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Today's Revenue Fill Dial & System Meter Telemetry
        item {
            if (isLandscape) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    CircularTelemetryDial(
                        label = "Today's Revenue Fill Dial",
                        value = "$${String.format(Locale.US, "%,d", earningsSummary?.todayEarnings?.toInt() ?: 1420)}",
                        percentage = 0.71f,
                        badgeText = "71% of goal",
                        targetText = "Target Cap: $2,000",
                        modifier = Modifier.weight(0.4f)
                    )

                    SystemMeterTelemetryCard(
                        modifier = Modifier.weight(0.6f)
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    CircularTelemetryDial(
                        label = "Today's Revenue Fill Dial",
                        value = "$1,420",
                        percentage = 0.71f,
                        badgeText = "71% of goal",
                        targetText = "Target Cap: $2,000",
                        modifier = Modifier.fillMaxWidth()
                    )

                    SystemMeterTelemetryCard(
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Recent Dispatches Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline), shape = RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "RECENT DISPATCHES (${orders.take(2).size})",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "View Pipeline →",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            color = AquaOchre,
                            modifier = Modifier.clickable { onNavigateToPipeline() }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        orders.take(2).forEach { order ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)), shape = RoundedCornerShape(8.dp))
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = order.orderNumber,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = order.customerName,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    StatusBadge(status = order.status)
                                    Text(
                                        text = "$${String.format(Locale.US, "%.2f", order.totalAmount)}",
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        ProductEditDialog(
            product = null,
            onDismiss = { showAddDialog = false },
            onSave = { brand, capacity, weight, price, inStock, est, desc ->
                sellerViewModel.saveProduct(
                    id = null,
                    brand = brand,
                    capacityLiters = capacity,
                    weightKg = weight,
                    price = price,
                    inStock = inStock,
                    deliveryTimeEst = est,
                    description = desc
                )
                showAddDialog = false
            },
            onDelete = {}
        )
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtext: String,
    bottomLabel: String,
    badgeColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline), shape = RoundedCornerShape(10.dp)),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title.uppercase(),
                fontSize = 8.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = value,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Surface(
                    color = AquaTealContainer,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = subtext,
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = bottomLabel,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerOrdersScreen(
    sellerViewModel: SellerViewModel,
    modifier: Modifier = Modifier
) {
    val filteredOrders by sellerViewModel.filteredSellerOrders.collectAsState()
    val searchQuery by sellerViewModel.orderSearchQuery.collectAsState()
    val selectedStatus by sellerViewModel.selectedStatusFilter.collectAsState()

    val statuses = remember {
        listOf(null) + OrderStatus.entries.filter { it != OrderStatus.CANCELLED }
    }

    var selectedOrderForDetail by remember { mutableStateOf<OrderEntity?>(null) }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    // Set selected order to first item by default if screen is wide
    LaunchedEffect(filteredOrders) {
        if (isLandscape && selectedOrderForDetail == null && filteredOrders.isNotEmpty()) {
            selectedOrderForDetail = filteredOrders.first()
        }
    }

    Row(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Left Column: Search Filters & List
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(if (isLandscape) 0.4f else 1f)
                .border(BorderStroke(if (isLandscape) 1.dp else 0.dp, MaterialTheme.colorScheme.outline))
        ) {
            // Header Description
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Pipeline Order Dispatch",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Track stage transitions, assign tanker drivers, and update status seals.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { sellerViewModel.orderSearchQuery.value = it },
                    placeholder = { Text("Search orders...", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Scrollable filters tabs
                ScrollableTabRow(
                    selectedTabIndex = statuses.indexOf(selectedStatus).coerceAtLeast(0),
                    containerColor = Color.Transparent,
                    edgePadding = 0.dp,
                    divider = {}
                ) {
                    statuses.forEach { status ->
                        val isSelected = selectedStatus == status
                        Tab(
                            selected = isSelected,
                            onClick = { sellerViewModel.selectedStatusFilter.value = status },
                            text = {
                                Text(
                                    text = status?.displayName ?: "all",
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) AquaOchre else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        )
                    }
                }
            }

            HorizontalDivider()

            // Orders list view
            if (filteredOrders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No Orders Found", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredOrders, key = { it.id }) { order ->
                        val isSelected = selectedOrderForDetail?.id == order.id
                        val borderColor = if (isSelected && isLandscape) AquaOchre else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        val borderWidth = if (isSelected && isLandscape) 2.dp else 1.dp

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedOrderForDetail = order
                                }
                                .border(BorderStroke(borderWidth, borderColor), shape = RoundedCornerShape(8.dp)),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = order.orderNumber,
                                            fontSize = 12.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = order.customerName,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    StatusBadge(status = order.status)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("1 items", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(
                                        text = "$${String.format(Locale.US, "%.2f", order.totalAmount)}",
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Right Column: Detail Inspector Card
        if (isLandscape) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.6f)
                    .padding(16.dp)
            ) {
                if (selectedOrderForDetail != null) {
                    OrderInspectorCard(
                        order = selectedOrderForDetail!!,
                        onUpdateStatus = { nextStatus ->
                            sellerViewModel.updateOrderStatus(selectedOrderForDetail!!.id, nextStatus)
                        }
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Select an order to inspect details", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    }
                }
            }
        }
    }

    // Modal dialog for compact/portrait detail inspection
    if (!isLandscape && selectedOrderForDetail != null) {
        AlertDialog(
            onDismissRequest = { selectedOrderForDetail = null },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { selectedOrderForDetail = null }) {
                    Text("Close")
                }
            },
            text = {
                Box(modifier = Modifier.fillMaxWidth().height(450.dp)) {
                    OrderInspectorCard(
                        order = selectedOrderForDetail!!,
                        onUpdateStatus = { nextStatus ->
                            sellerViewModel.updateOrderStatus(selectedOrderForDetail!!.id, nextStatus)
                            selectedOrderForDetail = null
                        }
                    )
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderInspectorCard(
    order: OrderEntity,
    onUpdateStatus: (OrderStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = remember(order.itemsJson) { Converters.jsonToCartItems(order.itemsJson) }
    val currentStatus = OrderStatus.fromString(order.status)
    val nextStatus = when (currentStatus) {
        OrderStatus.PENDING -> OrderStatus.PROCESSING
        OrderStatus.PROCESSING -> OrderStatus.PACKED
        OrderStatus.PACKED -> OrderStatus.PICKED_UP
        OrderStatus.PICKED_UP -> OrderStatus.OUT_FOR_DELIVERY
        OrderStatus.OUT_FOR_DELIVERY -> OrderStatus.DELIVERED
        else -> null
    }

    Card(
        modifier = modifier
            .fillMaxSize()
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline), shape = RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Order ${order.orderNumber}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Customer: ${order.customerName} (${order.customerPhone})",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    StatusBadge(status = order.status)
                    if (nextStatus != null) {
                        Button(
                            onClick = { onUpdateStatus(nextStatus) },
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AquaOchre),
                            contentPadding = PaddingValues(horizontal = 10.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Advance Pipeline →", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            HorizontalDivider()

            // Manual Override Dropdown Selector
            Column {
                Text(
                    text = "MANUAL STATUS OVERRIDE:",
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                var dropdownExpanded by remember { mutableStateOf(false) }
                Surface(
                    onClick = { dropdownExpanded = true },
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth().height(42.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = order.status, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }

                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        OrderStatus.entries.forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status.displayName, fontSize = 12.sp) },
                                onClick = {
                                    onUpdateStatus(status)
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Items Catalog in order
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "JUG ITEMS IN ORDER",
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)), shape = RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        items.forEach { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(text = item.productTitle, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "${item.quantity}x • ${item.capacityLiters.toInt()} Liters", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text(
                                    text = "$${String.format(Locale.US, "%.2f", item.total)}",
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // Bottom split telemetry rows
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Address Column
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "DISPATCH ADDRESS",
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = order.deliveryAddressText,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Readings Column
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "SEAL & METER READING",
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "SEAL-2026-${order.orderNumber}-A",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "PSI: 42.5 • TDS: 45 PPM",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun SellerInventoryScreen(
    sellerViewModel: SellerViewModel,
    modifier: Modifier = Modifier
) {
    val products by sellerViewModel.sellerProducts.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }
    var selectedProductToEdit by remember { mutableStateOf<ProductEntity?>(null) }

    val configuration = LocalConfiguration.current
    val gridCells = if (configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) 2 else 1

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Inventory & Gauge Control",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Manage product SKUs, stock fill percentages, and pricing.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = {
                    selectedProductToEdit = null
                    showEditDialog = true
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AquaOchre)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add New Water SKU", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (products.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No products in inventory", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(gridCells),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(products, key = { it.id }) { product ->
                    // Wrapping product card with edit trigger
                    Box {
                        WaterJugCard(
                            product = product,
                            cartQuantity = 0,
                            onAdd = {
                                selectedProductToEdit = product
                                showEditDialog = true
                            },
                            onDecrement = {}
                        )
                    }
                }
            }
        }
    }

    if (showEditDialog) {
        ProductEditDialog(
            product = selectedProductToEdit,
            onDismiss = { showEditDialog = false },
            onSave = { brand, capacity, weight, price, inStock, est, desc ->
                sellerViewModel.saveProduct(
                    id = selectedProductToEdit?.id,
                    brand = brand,
                    capacityLiters = capacity,
                    weightKg = weight,
                    price = price,
                    inStock = inStock,
                    deliveryTimeEst = est,
                    description = desc
                )
                showEditDialog = false
            },
            onDelete = { id ->
                sellerViewModel.deleteProduct(id)
                showEditDialog = false
            }
        )
    }
}

@Composable
fun ProductEditDialog(
    product: ProductEntity?,
    onDismiss: () -> Unit,
    onSave: (String, Float, Float, Double, Boolean, String, String) -> Unit,
    onDelete: (String) -> Unit
) {
    var brand by remember { mutableStateOf(product?.brand ?: "") }
    var waterType by remember { mutableStateOf("RO + UV Mineral") }
    var capacity by remember { mutableStateOf(product?.capacityLiters?.toString() ?: "20") }
    var price by remember { mutableStateOf(product?.price?.toString() ?: "4.50") }
    var deposit by remember { mutableStateOf("10.00") }
    var stockUnits by remember { mutableStateOf("400") }
    var tds by remember { mutableStateOf("45") }
    var phVal by remember { mutableStateOf("7.4") }
    var desc by remember { mutableStateOf(product?.description ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {},
        text = {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "NEW WATER JUG SKU",
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(16.dp))
                        }
                    }

                    HorizontalDivider()

                    // Brand/Title
                    OutlinedTextField(
                        value = brand,
                        onValueChange = { brand = it },
                        label = { Text("Product Name:", fontSize = 11.sp) },
                        placeholder = { Text("e.g. 20L Premium RO Jug", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Drops
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Dropdown selection for water type
                        var dropExpanded by remember { mutableStateOf(false) }
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = waterType,
                                onValueChange = {},
                                label = { Text("Water Type:", fontSize = 11.sp) },
                                readOnly = true,
                                trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.clickable { dropExpanded = true }) },
                                modifier = Modifier.fillMaxWidth()
                            )
                            DropdownMenu(expanded = dropExpanded, onDismissRequest = { dropExpanded = false }) {
                                listOf("RO + UV Mineral", "Spring", "Alkaline", "Deionized").forEach { opt ->
                                    DropdownMenuItem(
                                        text = { Text(opt, fontSize = 12.sp) },
                                        onClick = {
                                            waterType = opt
                                            dropExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = capacity,
                            onValueChange = { capacity = it },
                            label = { Text("Volume Capacity:", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    // Three columns (Price, Deposit, Stock)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = price,
                            onValueChange = { price = it },
                            label = { Text("Price ($):", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = deposit,
                            onValueChange = { deposit = it },
                            label = { Text("Jug Deposit ($):", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = stockUnits,
                            onValueChange = { stockUnits = it },
                            label = { Text("Stock Units:", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    // Two columns (TDS, pH)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = tds,
                            onValueChange = { tds = it },
                            label = { Text("TDS (PPM):", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = phVal,
                            onValueChange = { phVal = it },
                            label = { Text("pH Level:", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    // Description text area
                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Description:", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth().height(80.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Dialog Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (product != null) {
                            TextButton(
                                onClick = { onDelete(product.id) },
                                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text("Delete SKU", fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        OutlinedButton(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("Cancel", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                onSave(
                                    brand,
                                    capacity.toFloatOrNull() ?: 20f,
                                    capacity.toFloatOrNull() ?: 20f, // Use weight as capacity
                                    price.toDoubleOrNull() ?: 4.50,
                                    true,
                                    "25-35 mins",
                                    desc
                                )
                            },
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AquaOchre)
                        ) {
                            Text("Save SKU", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun SellerRevenueDialsScreen(
    sellerViewModel: SellerViewModel,
    modifier: Modifier = Modifier
) {
    val earningsSummary by sellerViewModel.earningsSummary.collectAsState()

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Earnings Dials & Financial Analytics",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Revenue targets, fill-dials, and daily order cashflow.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Three Dials
        item {
            if (isLandscape) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CircularTelemetryDial(
                        label = "Today's Revenue",
                        value = "$${String.format(Locale.US, "%,d", earningsSummary?.todayEarnings?.toInt() ?: 1420)}",
                        percentage = 0.71f,
                        badgeText = "71% of goal",
                        targetText = "Target Cap: $2,000",
                        modifier = Modifier.weight(1f)
                    )
                    CircularTelemetryDial(
                        label = "Weekly Revenue",
                        value = "$${String.format(Locale.US, "%,d", earningsSummary?.weeklyEarnings?.toInt() ?: 8640)}",
                        percentage = 0.72f,
                        badgeText = "72% of goal",
                        targetText = "Target Cap: $12,000",
                        modifier = Modifier.weight(1f)
                    )
                    CircularTelemetryDial(
                        label = "Monthly Revenue",
                        value = "$${String.format(Locale.US, "%,d", earningsSummary?.monthlyEarnings?.toInt() ?: 32400)}",
                        percentage = 0.72f,
                        badgeText = "72% of goal",
                        targetText = "Target Cap: $45,000",
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    CircularTelemetryDial(
                        label = "Today's Revenue",
                        value = "$1,420",
                        percentage = 0.71f,
                        badgeText = "71% of goal",
                        targetText = "Target Cap: $2,000",
                        modifier = Modifier.fillMaxWidth()
                    )
                    CircularTelemetryDial(
                        label = "Weekly Revenue",
                        value = "$8,640",
                        percentage = 0.72f,
                        badgeText = "72% of goal",
                        targetText = "Target Cap: $12,000",
                        modifier = Modifier.fillMaxWidth()
                    )
                    CircularTelemetryDial(
                        label = "Monthly Revenue",
                        value = "$32,400",
                        percentage = 0.72f,
                        badgeText = "72% of goal",
                        targetText = "Target Cap: $45,000",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Weekly trend bar chart
        item {
            EarningsChart(
                weeklyTrend = earningsSummary?.weeklyTrend ?: listOf(
                    "Mon" to 1100.0, "Tue" to 1420.0, "Wed" to 1200.0, "Thu" to 1850.0, "Fri" to 1600.0, "Sat" to 2100.0, "Sun" to 1400.0
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun SellerProfileScreen(
    sellerViewModel: SellerViewModel,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sellerProfile by sellerViewModel.sellerProfile.collectAsState()

    var brandName by remember(sellerProfile) { mutableStateOf(sellerProfile?.brandName ?: "Metro Hydro Infrastructure Hub") }
    var brandLabel by remember(sellerProfile) { mutableStateOf(sellerProfile?.ownerName ?: "Metro Aqua") }
    var tagline by remember(sellerProfile) { mutableStateOf(sellerProfile?.description ?: "High-volume high-purity RO & mineral water distribution") }
    var phone by remember(sellerProfile) { mutableStateOf(sellerProfile?.phone ?: "+1 (555) 392-8810") }
    var hours by remember(sellerProfile) { mutableStateOf(sellerProfile?.businessHours ?: "06:00 AM - 10:00 PM") }
    var address by remember(sellerProfile) { mutableStateOf(sellerProfile?.locationAddress ?: "Tanker Station #4, Sector 12 Industrial Utility Grid") }

    var isSavedToast by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title block
        Column {
            Text(
                text = "Station & Business Profile Management",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Public directory listings, purity certifications, and station contacts.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline), shape = RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "STATION IDENTITY",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Surface(
                        color = AquaOchre,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "VERIFIED INFRASTRUCTURE",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                HorizontalDivider()

                // Form
                if (isLandscape) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = brandName,
                            onValueChange = { brandName = it },
                            label = { Text("Business Name:", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = brandLabel,
                            onValueChange = { brandLabel = it },
                            label = { Text("Brand Label:", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                } else {
                    OutlinedTextField(
                        value = brandName,
                        onValueChange = { brandName = it },
                        label = { Text("Business Name:", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = brandLabel,
                        onValueChange = { brandLabel = it },
                        label = { Text("Brand Label:", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = tagline,
                    onValueChange = { tagline = it },
                    label = { Text("Tagline:", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (isLandscape) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Phone Contact:", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = hours,
                            onValueChange = { hours = it },
                            label = { Text("Operating Hours:", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                } else {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Contact:", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = hours,
                        onValueChange = { hours = it },
                        label = { Text("Operating Hours:", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Station Physical Address:", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            sellerViewModel.updateProfile(
                                brandName = brandName,
                                ownerName = brandLabel,
                                phone = phone,
                                locationAddress = address,
                                businessHours = hours,
                                description = tagline
                            )
                            isSavedToast = true
                        },
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AquaOchre)
                    ) {
                        Text("Save Profile Updates", fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    if (isSavedToast) {
                        Text(
                            text = "Profile updated successfully!",
                            color = StatusDelivered,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
        ) {
            Icon(Icons.Default.Logout, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sign Out", fontWeight = FontWeight.Bold)
        }
    }
}
