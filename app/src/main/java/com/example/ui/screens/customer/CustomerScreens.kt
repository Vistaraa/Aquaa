package com.example.ui.screens.customer

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.ui.viewmodel.CustomerViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDiscoverScreen(
    customerViewModel: CustomerViewModel,
    onSellerClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val sellers by customerViewModel.sellers.collectAsState()
    val searchQuery by customerViewModel.searchQuery.collectAsState()
    val verifiedFilter by customerViewModel.isVerifiedOnlyFilter.collectAsState()

    var selectedWaterType by remember { mutableStateOf("all") }
    var selectedRadius by remember { mutableIntStateOf(5) }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    // Dynamic Filter Logic based on selected Water Type
    val filteredSellers = remember(sellers, selectedWaterType, selectedRadius) {
        sellers.filter { seller ->
            val matchesRadius = when (selectedRadius) {
                1 -> seller.locationAddress.contains("0.8") || seller.locationAddress.contains("1.0")
                3 -> !seller.locationAddress.contains("3.5")
                else -> true
            }
            val matchesWaterType = when (selectedWaterType) {
                "RO + UV" -> seller.brandName.contains("AquaPure", ignoreCase = true) || seller.description.contains("RO", ignoreCase = true)
                "Alkaline" -> seller.brandName.contains("BlueWave", ignoreCase = true) || seller.description.contains("Alkaline", ignoreCase = true)
                "Spring" -> seller.brandName.contains("Oasis", ignoreCase = true) || seller.description.contains("Spring", ignoreCase = true)
                "Deionized" -> seller.description.contains("Deionized", ignoreCase = true) || seller.description.contains("Oxygenated", ignoreCase = true)
                else -> true
            }
            matchesRadius && matchesWaterType
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Search bar & Water Type Filters row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { customerViewModel.searchQuery.value = it },
                    placeholder = { Text("Search nearby water suppliers, brands, purity grades...", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )

                // Water Type Filter Pills next to search
                val waterTypes = listOf("all", "RO + UV", "Alkaline", "Spring", "Deionized")
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    waterTypes.forEach { opt ->
                        val isSelected = selectedWaterType == opt
                        Surface(
                            onClick = { selectedWaterType = opt },
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSelected) AquaOchre else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, if (isSelected) AquaOchre else MaterialTheme.colorScheme.outline),
                            modifier = Modifier.height(38.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(horizontal = 10.dp)
                            ) {
                                Text(
                                    text = opt,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // Radius Slider Selector Bar
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline), shape = RoundedCornerShape(8.dp)),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Radius Filter: $selectedRadius km",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(1, 3, 5, 10).forEach { r ->
                            val isSelected = selectedRadius == r
                            Surface(
                                onClick = { selectedRadius = r },
                                shape = RoundedCornerShape(4.dp),
                                color = if (isSelected) AquaTeal else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.height(24.dp)
                            ) {
                                Text(
                                    text = "≤ ${r}km",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Hyperlocal dispatch active status banner & Live Telemetry
        item {
            if (isLandscape) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .weight(0.5f)
                            .height(115.dp)
                            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline), shape = RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF4A5F5E)) // Dark slate-teal banner background
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp).fillMaxHeight(),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "HYPERLOCAL DISPATCH STATUS",
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = AquaOchre
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "4 Water Tankers Active in Sector 12 Grid",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Average delivery dispatch time is currently 25 minutes. Pressures nominal across all neighborhood station hubs.",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }

                    SystemMeterTelemetryCard(
                        modifier = Modifier.weight(0.5f)
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline), shape = RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF4A5F5E))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "HYPERLOCAL DISPATCH STATUS",
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                color = AquaOchre
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "4 Water Tankers Active in Sector 12 Grid",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Average delivery dispatch time is currently 25 minutes. Pressures nominal across all neighborhood station hubs.",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }

                    SystemMeterTelemetryCard(modifier = Modifier.fillMaxWidth())
                }
            }
        }

        // Suppliers section header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "NEARBY SUPPLIERS (${filteredSellers.size})",
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "SORT: DISTANCE & STOCK",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Grid/List of Supplier Cards
        if (filteredSellers.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                    Text("No supplier match filters", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                }
            }
        } else {
            items(filteredSellers, key = { it.id }) { seller ->
                SellerCard(
                    seller = seller,
                    onClick = {
                        customerViewModel.selectSeller(seller.id)
                        onSellerClick(seller.id)
                    },
                    modifier = Modifier.testTag("seller_card_${seller.id}")
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerDetailScreen(
    customerViewModel: CustomerViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val seller by customerViewModel.selectedSeller.collectAsState()
    val products by customerViewModel.selectedSellerProducts.collectAsState()
    val cartItems by customerViewModel.cartItems.collectAsState()
    val cartTotal by customerViewModel.cartTotal.collectAsState()
    val cartCount by customerViewModel.cartItemCount.collectAsState()

    var showCheckoutSheet by remember { mutableStateOf(false) }

    val cartQuantityMap = remember(cartItems) {
        cartItems.associate { it.productId to it.quantity }
    }

    val configuration = LocalConfiguration.current
    val gridCells = if (configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) 2 else 1

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Back Link
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onBack() }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Back to Nearby Dealers",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Cover Banner Detail
            if (seller != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline), shape = RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column {
                        // Blurred filtration backdrop placeholder
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        )

                        // Main Header overlay details
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("METRO", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }

                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = seller!!.brandName,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Black,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Surface(
                                            color = AquaTealContainer,
                                            shape = RoundedCornerShape(4.dp),
                                            border = BorderStroke(1.dp, AquaTeal.copy(alpha = 0.2f))
                                        ) {
                                            Text(
                                                text = "ISO 22000 GRADE A+",
                                                fontSize = 7.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = AquaTeal,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }

                                        Surface(
                                            color = AquaOchreContainer,
                                            shape = RoundedCornerShape(4.dp),
                                            border = BorderStroke(1.dp, AquaOchre.copy(alpha = 0.2f))
                                        ) {
                                            Text(
                                                text = "VERIFIED DEALER",
                                                fontSize = 7.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = AquaOchre,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = seller!!.locationAddress,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Live Stock Card
                            Row(
                                modifier = Modifier
                                    .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline), shape = RoundedCornerShape(8.dp))
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Box(
                                        modifier = Modifier.size(36.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        val progressFraction = 0.84f // Mock live tank fill
                                        val outlineColor = MaterialTheme.colorScheme.outline
                                        Canvas(modifier = Modifier.fillMaxSize()) {
                                            drawCircle(
                                                color = outlineColor.copy(alpha = 0.3f),
                                                style = Stroke(width = 3.5f)
                                            )
                                            drawArc(
                                                color = AquaTeal,
                                                startAngle = -90f,
                                                sweepAngle = progressFraction * 360f,
                                                useCenter = false,
                                                style = Stroke(width = 3.5f, cap = StrokeCap.Round)
                                            )
                                        }
                                        Text(
                                            text = "84%",
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Text(
                                        text = "LIVE STOCK",
                                        fontSize = 6.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                VerticalDivider(modifier = Modifier.height(34.dp))

                                Column {
                                    Text(text = "Pressure: 42.5 PSI", fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                    Text(text = "Operating: ${seller!!.businessHours}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(text = "Phone: ${seller!!.phone}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Catalog Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PRODUCT CATALOG & JUG STOCK",
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${products.size} SKUS AVAILABLE",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Products Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(gridCells),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = if (cartCount > 0) 90.dp else 24.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(products, key = { it.id }) { product ->
                    val qty = cartQuantityMap[product.id] ?: 0
                    WaterJugCard(
                        product = product,
                        cartQuantity = qty,
                        onAdd = { customerViewModel.addToCart(product) },
                        onDecrement = { customerViewModel.decrementInCart(product.id) }
                    )
                }
            }
        }

        // Floating Bottom Cart Bar
        if (cartCount > 0) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .navigationBarsPadding(),
                shape = RoundedCornerShape(12.dp),
                color = AquaTeal,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "$cartCount ITEMS IN CART",
                            fontSize = 8.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "$${String.format(Locale.US, "%.2f", cartTotal)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }

                    Button(
                        onClick = { showCheckoutSheet = true },
                        colors = ButtonDefaults.buttonColors(containerColor = AquaOchre),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.testTag("checkout_button")
                    ) {
                        Text(
                            text = "Checkout Order",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }

    if (showCheckoutSheet) {
        CheckoutSheet(
            customerViewModel = customerViewModel,
            onDismiss = { showCheckoutSheet = false },
            onOrderPlaced = {
                showCheckoutSheet = false
                onBack()
            }
        )
    }
}

@Composable
fun CustomerOrdersScreen(
    customerViewModel: CustomerViewModel,
    modifier: Modifier = Modifier
) {
    val orders by customerViewModel.customerOrders.collectAsState()
    var selectedOrderForDetail by remember { mutableStateOf<OrderEntity?>(null) }
    val dateFormatter = remember { SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()) }

    if (selectedOrderForDetail != null) {
        CustomerOrderDetailScreen(
            order = selectedOrderForDetail!!,
            customerViewModel = customerViewModel,
            onBack = { selectedOrderForDetail = null }
        )
    } else {
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
                Text(
                    text = "My Orders & Delivery History",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${orders.size} TOTAL ORDERS",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (orders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No orders placed yet",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(orders, key = { it.id }) { order ->
                        val items = remember(order.itemsJson) { Converters.jsonToCartItems(order.itemsJson) }
                        val itemsText = items.joinToString { "${it.quantity}x ${it.productTitle}" }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedOrderForDetail = order }
                                .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline), shape = RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = order.orderNumber,
                                            fontSize = 15.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        StatusBadge(status = order.status)
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Supplier: ${order.sellerBrandName}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = itemsText,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "$${String.format(Locale.US, "%.2f", order.totalAmount)}",
                                        fontSize = 16.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = dateFormatter.format(Date(order.createdAtMillis)),
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomerOrderDetailScreen(
    order: OrderEntity,
    customerViewModel: CustomerViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val items = remember(order.itemsJson) { Converters.jsonToCartItems(order.itemsJson) }
    val currentStatus = OrderStatus.fromString(order.status)

    val scope = rememberCoroutineScope()
    val dateFormatter = remember { SimpleDateFormat("MM/dd/yyyy 'at' h:mm:ss a", Locale.getDefault()) }

    val nextStatus = when (currentStatus) {
        OrderStatus.PENDING -> OrderStatus.PROCESSING
        OrderStatus.PROCESSING -> OrderStatus.PACKED
        OrderStatus.PACKED -> OrderStatus.PICKED_UP
        OrderStatus.PICKED_UP -> OrderStatus.OUT_FOR_DELIVERY
        OrderStatus.OUT_FOR_DELIVERY -> OrderStatus.DELIVERED
        else -> OrderStatus.PENDING
    }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    // Calculate percentage and ETA based on status
    val (progressPercent, etaText, stageText) = when (currentStatus) {
        OrderStatus.PENDING -> Triple(0.16f, "Est. Arrival: 45 mins", "1/6")
        OrderStatus.PROCESSING -> Triple(0.33f, "Est. Arrival: 40 mins", "2/6")
        OrderStatus.PACKED -> Triple(0.50f, "Est. Arrival: 35 mins", "3/6")
        OrderStatus.PICKED_UP -> Triple(0.67f, "Est. Arrival: 25 mins", "4/6")
        OrderStatus.OUT_FOR_DELIVERY -> Triple(0.83f, "Est. Arrival: 10 mins", "5/6")
        OrderStatus.DELIVERED -> Triple(1.00f, "Delivered", "6/6")
        OrderStatus.CANCELLED -> Triple(0.00f, "Cancelled", "0/6")
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Back Link & Simulate Button Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.clickable { onBack() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.ArrowBack, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Back to All Orders", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        customerViewModel.updateOrderStatus(order.id, nextStatus)
                    },
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AquaSurfaceWhite),
                    border = BorderStroke(1.dp, AquaOchre),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(Icons.Default.ElectricBike, null, tint = AquaOchre, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Simulate Dispatch Progress", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AquaOchre)
                }
            }
        }

        // Header Title
        item {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Order ${order.orderNumber}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    StatusBadge(status = order.status)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Placed on ${dateFormatter.format(Date(order.createdAtMillis))}",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Two Columns Split Layout
        item {
            if (isLandscape) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    // Left Column
                    Column(
                        modifier = Modifier.weight(0.45f),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        OrderDetailProgressCard(
                            progressPercent = progressPercent,
                            etaText = etaText,
                            stageText = stageText,
                            currentStatus = currentStatus
                        )

                        OrderDetailTimelineCard(currentStatus = currentStatus)
                    }

                    // Right Column
                    Column(
                        modifier = Modifier.weight(0.55f),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        OrderDetailDispatchTeamCard(order = order)

                        OrderDetailWaterJugsCard(items = items, order = order)

                        SystemMeterTelemetryCard(
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    OrderDetailProgressCard(
                        progressPercent = progressPercent,
                        etaText = etaText,
                        stageText = stageText,
                        currentStatus = currentStatus
                    )

                    OrderDetailDispatchTeamCard(order = order)

                    OrderDetailTimelineCard(currentStatus = currentStatus)

                    OrderDetailWaterJugsCard(items = items, order = order)

                    SystemMeterTelemetryCard(modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
fun OrderDetailProgressCard(
    progressPercent: Float,
    etaText: String,
    stageText: String,
    currentStatus: OrderStatus,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline), shape = RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(130.dp),
                contentAlignment = Alignment.Center
            ) {
                val outlineColor = MaterialTheme.colorScheme.outline
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = outlineColor.copy(alpha = 0.3f),
                        style = Stroke(width = 12f)
                    )
                    drawArc(
                        color = AquaOchre,
                        startAngle = -90f,
                        sweepAngle = progressPercent * 360f,
                        useCenter = false,
                        style = Stroke(width = 12f, cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${(progressPercent * 100).toInt()}%",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = currentStatus.displayName.uppercase(),
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = AquaOchre
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = etaText,
                            fontSize = 8.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Text("HYDRO PRESSURE", fontSize = 8.sp, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("42.5 PSI", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                VerticalDivider(modifier = Modifier.height(28.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Text("PIPELINE STAGE", fontSize = 8.sp, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(stageText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun OrderDetailTimelineCard(
    currentStatus: OrderStatus,
    modifier: Modifier = Modifier
) {
    val steps = listOf(
        OrderStatus.PENDING,
        OrderStatus.PROCESSING,
        OrderStatus.PACKED,
        OrderStatus.PICKED_UP,
        OrderStatus.OUT_FOR_DELIVERY,
        OrderStatus.DELIVERED
    )

    Card(
        modifier = modifier
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline), shape = RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "DISPATCH TIMELINE PIPELINE",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            steps.forEachIndexed { index, step ->
                val isCompleted = step.stepOrder < currentStatus.stepOrder && currentStatus != OrderStatus.CANCELLED
                val isCurrent = step == currentStatus
                val activeBorderColor = if (isCurrent) AquaOchre else Color.Transparent
                val activeBgColor = if (isCurrent) AquaOchreContainer.copy(alpha = 0.2f) else Color.Transparent

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(BorderStroke(1.dp, activeBorderColor), shape = RoundedCornerShape(6.dp)),
                    color = activeBgColor,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 6.dp, horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Dot/Check icon indicator
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isCurrent -> AquaOchre
                                        isCompleted -> AquaTeal
                                        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCompleted) {
                                Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(11.dp))
                            } else {
                                Text(
                                    text = "${step.stepOrder}",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCurrent) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = step.displayName,
                            fontSize = 12.sp,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                            color = if (isCurrent) AquaOchre else if (isCompleted) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (isCurrent) {
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "[ACTIVE]",
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = AquaOchre
                            )
                        }
                    }
                }

                if (index < steps.size - 1) {
                    Box(
                        modifier = Modifier
                            .padding(start = 18.dp)
                            .width(1.5.dp)
                            .height(10.dp)
                            .background(
                                if (isCompleted) AquaTeal else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun OrderDetailDispatchTeamCard(
    order: OrderEntity,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline), shape = RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.LocalShipping, null, tint = AquaTeal, modifier = Modifier.size(16.dp))
                    Text(
                        text = "TANKER DISPATCH TEAM",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    color = AquaTealContainer,
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, AquaTeal.copy(alpha = 0.2f))
                ) {
                    Text(
                        text = "LIVE VERIFIED",
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = AquaTeal,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("DRIVER", fontSize = 8.sp, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Viktor Vance", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("DRIVER CONTACT", fontSize = 8.sp, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("+1 (555) 902-3344", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("VEHICLE", fontSize = 8.sp, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Flatbed #AQ-770", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("PRESSURE SEAL", fontSize = 8.sp, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("SEAL-2026-9729", fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = AquaTeal)
                }
            }
        }
    }
}

@Composable
fun OrderDetailWaterJugsCard(
    items: List<CartItem>,
    order: OrderEntity,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline), shape = RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "ORDERED WATER JUGS",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)), shape = RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier.size(32.dp).clip(RoundedCornerShape(4.dp)).background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.WaterDrop, null, tint = AquaTeal, modifier = Modifier.size(16.dp))
                            }
                            Column {
                                Text(item.productTitle, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("${item.quantity}x • ${item.capacityLiters.toInt()} Liters (5.2 Gallons)", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
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

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(8.dp))

            val subtotal = items.sumOf { it.total }
            val dispatchFee = 1.50
            val grandTotal = subtotal + dispatchFee

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Subtotal:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("$${String.format(Locale.US, "%.2f", subtotal)}", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Deposit:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("$0.00", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Dispatch Fee:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("$${String.format(Locale.US, "%.2f", dispatchFee)}", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            }

            Spacer(modifier = Modifier.height(6.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(6.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Total Paid:", fontSize = 12.sp, fontWeight = FontWeight.Black)
                Text("$${String.format(Locale.US, "%.2f", grandTotal)}", fontSize = 14.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Black, color = AquaOchre)
            }
        }
    }
}

@Composable
fun CustomerProfileScreen(
    customerViewModel: CustomerViewModel,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by customerViewModel.currentUser.collectAsState()
    val savedAddresses by customerViewModel.savedAddresses.collectAsState()

    var showAddAddressDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Profile Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline), shape = RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentUser?.name?.take(1)?.uppercase() ?: "U",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Column {
                    Text(
                        text = currentUser?.name ?: "Customer",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = currentUser?.email ?: "alex@example.com",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = currentUser?.phone ?: "+1 555-0198",
                        fontSize = 12.sp,
                        color = AquaTeal,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Saved Delivery Addresses Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Saved Delivery Addresses",
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            TextButton(onClick = { showAddAddressDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add New", fontWeight = FontWeight.Bold)
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(savedAddresses, key = { it.id }) { address ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline), shape = RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (address.isDefault) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = when (address.label) {
                                    "Work" -> Icons.Default.Work
                                    "Home" -> Icons.Default.Home
                                    else -> Icons.Default.Place
                                },
                                contentDescription = null,
                                tint = AquaTeal
                            )

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = address.label,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    if (address.isDefault) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = AquaTeal,
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "DEFAULT",
                                                fontSize = 8.sp,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = address.addressText,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Sign Out Button
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

    if (showAddAddressDialog) {
        AddAddressDialog(
            customerViewModel = customerViewModel,
            onDismiss = { showAddAddressDialog = false }
        )
    }
}

@Composable
fun AddressSelectionDialog(
    customerViewModel: CustomerViewModel,
    onDismiss: () -> Unit
) {
    val addresses by customerViewModel.savedAddresses.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Delivery Address", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                addresses.forEach { addr ->
                    Surface(
                        onClick = {
                            customerViewModel.selectedAddressText.value = addr.addressText
                            onDismiss()
                        },
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = addr.label, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = addr.addressText, fontSize = 12.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun AddAddressDialog(
    customerViewModel: CustomerViewModel,
    onDismiss: () -> Unit
) {
    var label by remember { mutableStateOf("Home") }
    var addressText by remember { mutableStateOf("") }
    var landmark by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Delivery Address", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = label, onValueChange = { label = it }, label = { Text("Label") }, singleLine = true)
                OutlinedTextField(value = addressText, onValueChange = { addressText = it }, label = { Text("Address Text") }, singleLine = true)
                OutlinedTextField(value = landmark, onValueChange = { landmark = it }, label = { Text("Landmark") }, singleLine = true)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (addressText.isNotBlank()) {
                        customerViewModel.addAddress(label, addressText, landmark, true)
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AquaOchre)
            ) {
                Text("Save Address", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun CheckoutSheet(
    customerViewModel: CustomerViewModel,
    onDismiss: () -> Unit,
    onOrderPlaced: () -> Unit
) {
    val cartItems by customerViewModel.cartItems.collectAsState()
    val cartTotal by customerViewModel.cartTotal.collectAsState()
    val selectedAddress by customerViewModel.selectedAddressText.collectAsState()

    var emptyJugExchangeCount by remember { mutableIntStateOf(0) }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    val subtotal = cartTotal
    val maxEmpties = cartItems.sumOf { it.quantity }

    // Deposit fee calculations: $10.00 per jug, waived for each returned empty
    val originalDepositFee = maxEmpties * 10.0
    val savedDeposit = emptyJugExchangeCount * 10.0
    val actualDepositFee = originalDepositFee - savedDeposit

    val dispatchFee = 1.50
    val grandTotal = subtotal + actualDepositFee + dispatchFee - savedDeposit

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {},
        text = {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.width(if (isLandscape) 700.dp else 400.dp).height(500.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Order Summary & Dispatch Confirmation",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "HUB: METRO HYDRO INFRASTRUCTURE HUB",
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Left Column: Items & Delivery
                        Column(
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "SELECTED JUGS (${cartItems.size})",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            // Cart list
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                                    .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)), shape = RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                            ) {
                                LazyColumn(contentPadding = PaddingValues(8.dp)) {
                                    items(cartItems) { item ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Box(
                                                    modifier = Modifier.size(36.dp).clip(RoundedCornerShape(4.dp)).background(MaterialTheme.colorScheme.surface),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(Icons.Default.WaterDrop, null, tint = AquaTeal, modifier = Modifier.size(16.dp))
                                                }
                                                Column {
                                                    Text(item.productTitle, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    Text("${item.quantity}x • ${item.capacityLiters.toInt()} Liters ($${item.unitPrice}/jug)", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                }
                                            }

                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("- ${item.quantity} +", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                Text(
                                                    text = "$${String.format(Locale.US, "%.2f", item.total)}",
                                                    fontSize = 11.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Empty Jug Exchange Options Card
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline), shape = RoundedCornerShape(8.dp)),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Empty Jug Exchange",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Returning empty jugs waives deposit fee ($10/jug)",
                                            fontSize = 9.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    // Dropdown empties selection
                                    var emptiesExpanded by remember { mutableStateOf(false) }
                                    Box {
                                        Surface(
                                            onClick = { emptiesExpanded = true },
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                            shape = RoundedCornerShape(4.dp),
                                            modifier = Modifier.height(30.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("Empties: $emptyJugExchangeCount jugs", fontSize = 10.sp)
                                                Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(14.dp))
                                            }
                                        }
                                        DropdownMenu(expanded = emptiesExpanded, onDismissRequest = { emptiesExpanded = false }) {
                                            (0..maxEmpties).forEach { n ->
                                                DropdownMenuItem(
                                                    text = { Text("$n jugs", fontSize = 11.sp) },
                                                    onClick = {
                                                        emptyJugExchangeCount = n
                                                        emptiesExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Delivery Address selections
                            Column {
                                Text(
                                    text = "DELIVERY DESTINATION & DISPATCH SLOT",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Surface(
                                        modifier = Modifier.fillMaxWidth().border(BorderStroke(1.2.dp, AquaOchre), shape = RoundedCornerShape(6.dp)),
                                        shape = RoundedCornerShape(6.dp),
                                        color = AquaOchreContainer.copy(alpha = 0.2f)
                                    ) {
                                        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                            RadioButton(selected = true, onClick = {})
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Column {
                                                Text("Home Residence DEFAULT", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                Text(selectedAddress, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Right Column: Payment & Checkout Telemetry
                        Column(
                            modifier = Modifier.width(if (isLandscape) 240.dp else 160.dp).fillMaxHeight(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline), shape = RoundedCornerShape(8.dp)),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = "PAYMENT & PRICE TELEMETRY",
                                        fontSize = 8.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    HorizontalDivider()

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Jugs Subtotal:", fontSize = 10.sp)
                                        Text("$${String.format(Locale.US, "%.2f", subtotal)}", fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                    }

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Jug Deposit Fee:", fontSize = 10.sp)
                                        Text("$${String.format(Locale.US, "%.2f", actualDepositFee)}", fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                    }

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Tanker Dispatch:", fontSize = 10.sp)
                                        Text("$${String.format(Locale.US, "%.2f", dispatchFee)}", fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                    }

                                    if (emptyJugExchangeCount > 0) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(AquaTealContainer, shape = RoundedCornerShape(4.dp))
                                                .padding(horizontal = 4.dp, vertical = 2.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("Deposit Savings:", fontSize = 9.sp, color = AquaTeal, fontWeight = FontWeight.Bold)
                                            Text("-$${String.format(Locale.US, "%.2f", savedDeposit)}", fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = AquaTeal, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    HorizontalDivider()

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("GRAND TOTAL:", fontSize = 11.sp, fontWeight = FontWeight.Black)
                                        Text("$${String.format(Locale.US, "%.2f", grandTotal.coerceAtLeast(0.0))}", fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Black, color = AquaOchre)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            Button(
                                onClick = {
                                    customerViewModel.placeOrder {
                                        onOrderPlaced()
                                    }
                                },
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AquaOchre),
                                modifier = Modifier.fillMaxWidth().height(42.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp), tint = Color.White)
                                    Text("Confirm Order", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }

                            OutlinedButton(
                                onClick = onDismiss,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.fillMaxWidth().height(36.dp)
                            ) {
                                Text("Cancel", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            }
        }
    )
}
