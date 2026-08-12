package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.data.local.AquaDatabase
import com.example.data.models.UserRole
import com.example.data.repository.AquaRepository
import com.example.ui.components.AquaTopBar
import com.example.ui.screens.auth.AuthRoleSelectionScreen
import com.example.ui.screens.customer.*
import com.example.ui.screens.seller.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.CustomerViewModel
import com.example.ui.viewmodel.SellerViewModel

class MainActivity : ComponentActivity() {

    private lateinit var database: AquaDatabase
    private lateinit var repository: AquaRepository
    private lateinit var authViewModel: AuthViewModel
    private lateinit var customerViewModel: CustomerViewModel
    private lateinit var sellerViewModel: SellerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        database = AquaDatabase.getDatabase(this, lifecycleScope)
        repository = AquaRepository(
            userDao = database.userDao(),
            addressDao = database.addressDao(),
            sellerDao = database.sellerDao(),
            productDao = database.productDao(),
            orderDao = database.orderDao()
        )

        authViewModel = AuthViewModel(repository)
        customerViewModel = CustomerViewModel(repository)
        sellerViewModel = SellerViewModel(repository)

        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }

            AquaConnectTheme(darkTheme = isDarkTheme) {
                AquaAppContainer(
                    authViewModel = authViewModel,
                    customerViewModel = customerViewModel,
                    sellerViewModel = sellerViewModel,
                    isDarkTheme = isDarkTheme,
                    onToggleDarkTheme = { isDarkTheme = !isDarkTheme }
                )
            }
        }
    }
}

@Composable
fun AquaAppContainer(
    authViewModel: AuthViewModel,
    customerViewModel: CustomerViewModel,
    sellerViewModel: SellerViewModel,
    isDarkTheme: Boolean,
    onToggleDarkTheme: () -> Unit
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val currentRole by authViewModel.currentRole.collectAsState()
    val cartCount by customerViewModel.cartItemCount.collectAsState()

    var customerNavTab by remember { mutableIntStateOf(0) } // 0=Discover, 1=Orders, 2=Profile
    var sellerNavTab by remember { mutableIntStateOf(0) }   // 0=Overview, 1=Inventory, 2=Pipeline, 3=Revenue, 4=Business Hub

    var activeCustomerScreen by remember { mutableStateOf("HOME") } // "HOME" or "SELLER_DETAIL"

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    if (currentUser == null) {
        AuthRoleSelectionScreen(
            authViewModel = authViewModel,
            onLoginSuccess = {
                customerNavTab = 0
                sellerNavTab = 0
                activeCustomerScreen = "HOME"
            }
        )
    } else {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                AquaTopBar(
                    currentRole = currentRole,
                    userName = currentUser?.name ?: "User",
                    onRoleSwitch = { role ->
                        authViewModel.selectRole(role)
                    },
                    onProfileClick = {
                        if (currentRole == UserRole.CUSTOMER) {
                            customerNavTab = 2
                            activeCustomerScreen = "HOME"
                        } else {
                            sellerNavTab = 4
                        }
                    },
                    cartCount = cartCount,
                    onCartClick = {
                        if (currentRole == UserRole.CUSTOMER) {
                            activeCustomerScreen = "SELLER_DETAIL"
                        }
                    },
                    isDarkTheme = isDarkTheme,
                    onToggleDarkTheme = onToggleDarkTheme
                )
            },
            bottomBar = {
                // Show bottom bar for portrait screens, or always for customer mode
                if (currentRole == UserRole.CUSTOMER && activeCustomerScreen == "HOME") {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .navigationBarsPadding()
                            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline))
                            .testTag("customer_bottom_nav")
                    ) {
                        NavigationBarItem(
                            selected = customerNavTab == 0,
                            onClick = { customerNavTab = 0 },
                            icon = { Icon(if (customerNavTab == 0) Icons.Filled.Explore else Icons.Outlined.Explore, contentDescription = "Discover") },
                            label = { Text("Discover", fontWeight = FontWeight.Bold) }
                        )

                        NavigationBarItem(
                            selected = customerNavTab == 1,
                            onClick = { customerNavTab = 1 },
                            icon = { Icon(if (customerNavTab == 1) Icons.Filled.ReceiptLong else Icons.Outlined.ReceiptLong, contentDescription = "Orders") },
                            label = { Text("My Orders", fontWeight = FontWeight.Bold) }
                        )

                        NavigationBarItem(
                            selected = customerNavTab == 2,
                            onClick = { customerNavTab = 2 },
                            icon = { Icon(if (customerNavTab == 2) Icons.Filled.Person else Icons.Outlined.Person, contentDescription = "Profile") },
                            label = { Text("Profile", fontWeight = FontWeight.Bold) }
                        )
                    }
                } else if (currentRole == UserRole.SELLER && !isLandscape) {
                    // Mobile portrait bottom navigation bar with 5 items
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .navigationBarsPadding()
                            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline))
                            .testTag("seller_bottom_nav")
                    ) {
                        NavigationBarItem(
                            selected = sellerNavTab == 0,
                            onClick = { sellerNavTab = 0 },
                            icon = { Icon(Icons.Default.GridView, contentDescription = "Overview") },
                            label = { Text("Overview", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                        )

                        NavigationBarItem(
                            selected = sellerNavTab == 1,
                            onClick = { sellerNavTab = 1 },
                            icon = { Icon(Icons.Default.Speed, contentDescription = "Inventory") },
                            label = { Text("Inventory", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                        )

                        NavigationBarItem(
                            selected = sellerNavTab == 2,
                            onClick = { sellerNavTab = 2 },
                            icon = { Icon(Icons.Default.Inbox, contentDescription = "Pipeline") },
                            label = { Text("Orders", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                        )

                        NavigationBarItem(
                            selected = sellerNavTab == 3,
                            onClick = { sellerNavTab = 3 },
                            icon = { Icon(Icons.Default.ShowChart, contentDescription = "Dials") },
                            label = { Text("Dials", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                        )

                        NavigationBarItem(
                            selected = sellerNavTab == 4,
                            onClick = { sellerNavTab = 4 },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Business") },
                            label = { Text("Business", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                        )
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Wide Screen Left Navigation Sidebar (Seller only)
                if (currentRole == UserRole.SELLER && isLandscape) {
                    Column(
                        modifier = Modifier
                            .width(240.dp)
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.surface)
                            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "SELLER OPERATIONS HUB",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Metro Hydro Hub #4",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Sidebar Tabs
                        val menuItems = listOf(
                            Triple("Overview", Icons.Default.GridView, 0),
                            Triple("Inventory & Gauges", Icons.Default.Speed, 1),
                            Triple("Pipeline Orders", Icons.Default.Inbox, 2),
                            Triple("Revenue Dials", Icons.Default.ShowChart, 3),
                            Triple("Business Hub", Icons.Default.Home, 4)
                        )

                        menuItems.forEach { (label, icon, tabIndex) ->
                            val isSelected = sellerNavTab == tabIndex
                            val itemBg = if (isSelected) AquaOchre else Color.Transparent
                            val itemText = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface

                            Surface(
                                onClick = { sellerNavTab = tabIndex },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(42.dp),
                                shape = RoundedCornerShape(8.dp),
                                color = itemBg,
                                border = if (isSelected) null else BorderStroke(0.dp, Color.Transparent)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                        contentDescription = label,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = label,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = itemText
                                    )
                                }
                            }
                        }
                    }
                }

                // Main Content Screen Area
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    if (currentRole == UserRole.CUSTOMER) {
                        when {
                            activeCustomerScreen == "SELLER_DETAIL" -> {
                                SellerDetailScreen(
                                    customerViewModel = customerViewModel,
                                    onBack = { activeCustomerScreen = "HOME" }
                                )
                            }
                            customerNavTab == 0 -> {
                                CustomerDiscoverScreen(
                                    customerViewModel = customerViewModel,
                                    onSellerClick = { sellerId ->
                                        activeCustomerScreen = "SELLER_DETAIL"
                                    }
                                )
                            }
                            customerNavTab == 1 -> {
                                CustomerOrdersScreen(
                                    customerViewModel = customerViewModel
                                )
                            }
                            customerNavTab == 2 -> {
                                CustomerProfileScreen(
                                    customerViewModel = customerViewModel,
                                    onLogout = { authViewModel.logout() }
                                )
                            }
                        }
                    } else {
                        when (sellerNavTab) {
                            0 -> SellerDashboardScreen(
                                sellerViewModel = sellerViewModel,
                                onNavigateToPipeline = { sellerNavTab = 2 }
                            )
                            1 -> SellerInventoryScreen(sellerViewModel = sellerViewModel)
                            2 -> SellerOrdersScreen(sellerViewModel = sellerViewModel)
                            3 -> SellerRevenueDialsScreen(sellerViewModel = sellerViewModel)
                            4 -> SellerProfileScreen(
                                sellerViewModel = sellerViewModel,
                                onLogout = { authViewModel.logout() }
                            )
                        }
                    }
                }
            }
        }
    }
}
