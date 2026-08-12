package com.example.data.repository

import com.example.data.local.*
import com.example.data.models.*
import kotlinx.coroutines.flow.*
import java.util.Calendar

class AquaRepository(
    private val userDao: UserDao,
    private val addressDao: AddressDao,
    private val sellerDao: SellerDao,
    private val productDao: ProductDao,
    private val orderDao: OrderDao
) {
    // Current Active User & Role State
    private val _currentUser = MutableStateFlow<UserEntity?>(
        UserEntity(
            id = "usr_customer_1",
            name = "Alex Rivera",
            email = "alex@example.com",
            phone = "+1 555-0198",
            role = UserRole.CUSTOMER.name
        )
    )
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _currentRole = MutableStateFlow(UserRole.CUSTOMER)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    // Login / Logout / Switch Active User
    suspend fun loginAsCustomer(email: String = "alex@example.com") {
        val user = userDao.getUserByEmail(email) ?: userDao.getUserById("usr_customer_1")
        if (user != null) {
            _currentUser.value = user
            _currentRole.value = UserRole.CUSTOMER
        } else {
            val newUser = UserEntity(
                id = "usr_cust_${System.currentTimeMillis()}",
                name = email.substringBefore("@").replaceFirstChar { it.uppercase() },
                email = email,
                phone = "+1 555-0100",
                role = UserRole.CUSTOMER.name
            )
            userDao.insertUser(newUser)
            _currentUser.value = newUser
            _currentRole.value = UserRole.CUSTOMER
        }
    }

    suspend fun loginAsSeller(sellerId: String = "seller_1") {
        val seller = sellerDao.getSellerById(sellerId)
        val sellerUser = userDao.getUserById("usr_seller_1") ?: UserEntity(
            id = "usr_seller_1",
            name = seller?.ownerName ?: "David Miller",
            email = "david@aquapure.com",
            phone = seller?.phone ?: "+1 555-0245",
            role = UserRole.SELLER.name,
            sellerId = sellerId
        )
        _currentUser.value = sellerUser
        _currentRole.value = UserRole.SELLER
    }

    fun switchRole(role: UserRole) {
        _currentRole.value = role
        val user = _currentUser.value ?: return
        if (role == UserRole.SELLER && user.sellerId == null) {
            _currentUser.value = user.copy(role = UserRole.SELLER.name, sellerId = "seller_1")
        } else {
            _currentUser.value = user.copy(role = role.name)
        }
    }

    fun logout() {
        _currentUser.value = null
    }

    // Customer Features
    fun getAddressesForUser(userId: String): Flow<List<AddressEntity>> =
        addressDao.getAddressesForUser(userId)

    suspend fun addAddress(address: AddressEntity) {
        if (address.isDefault) {
            addressDao.clearDefaultAddresses(address.userId)
        }
        addressDao.insertAddress(address)
    }

    suspend fun setDefaultAddress(userId: String, addressId: String) {
        addressDao.setDefaultAddress(userId, addressId)
    }

    suspend fun deleteAddress(addressId: String) {
        addressDao.deleteAddress(addressId)
    }

    // Sellers & Products
    val allSellers: Flow<List<SellerEntity>> = sellerDao.getAllSellers()

    suspend fun getSellerById(id: String): SellerEntity? = sellerDao.getSellerById(id)

    fun observeSellerById(id: String): Flow<SellerEntity?> = sellerDao.observeSellerById(id)

    fun getProductsBySeller(sellerId: String): Flow<List<ProductEntity>> =
        productDao.getProductsBySeller(sellerId)

    suspend fun updateSellerProfile(seller: SellerEntity) {
        sellerDao.updateSeller(seller)
    }

    // Inventory CRUD
    suspend fun addProduct(product: ProductEntity) {
        productDao.insertProduct(product)
    }

    suspend fun updateProduct(product: ProductEntity) {
        productDao.updateProduct(product)
    }

    suspend fun deleteProduct(productId: String) {
        productDao.deleteProduct(productId)
    }

    // Orders
    fun getOrdersForCustomer(customerId: String): Flow<List<OrderEntity>> =
        orderDao.getOrdersForCustomer(customerId)

    fun getOrdersForSeller(sellerId: String): Flow<List<OrderEntity>> =
        orderDao.getOrdersForSeller(sellerId)

    fun observeOrderById(orderId: String): Flow<OrderEntity?> =
        orderDao.observeOrderById(orderId)

    suspend fun placeOrder(
        customer: UserEntity,
        deliveryAddress: String,
        seller: SellerEntity,
        cartItems: List<CartItem>
    ): String {
        val now = System.currentTimeMillis()
        val orderId = "ord_${now}"
        val orderNumber = "AQ-${(1000..9999).random()}"
        val total = cartItems.sumOf { it.total }

        val initialTimestamps = mapOf(OrderStatus.PENDING.displayName to now)

        val order = OrderEntity(
            id = orderId,
            orderNumber = orderNumber,
            customerId = customer.id,
            customerName = customer.name,
            customerPhone = customer.phone,
            deliveryAddressText = deliveryAddress,
            sellerId = seller.id,
            sellerBrandName = seller.brandName,
            sellerPhone = seller.phone,
            itemsJson = Converters.cartItemsToJson(cartItems),
            totalAmount = total,
            status = OrderStatus.PENDING.displayName,
            statusTimestampsJson = Converters.timestampsToJson(initialTimestamps),
            createdAtMillis = now
        )

        orderDao.insertOrder(order)
        return orderId
    }

    suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        val existing = orderDao.getOrderById(orderId) ?: return
        val currentTimestamps = Converters.jsonToTimestamps(existing.statusTimestampsJson).toMutableMap()
        currentTimestamps[newStatus.displayName] = System.currentTimeMillis()

        orderDao.updateOrderStatus(
            orderId = orderId,
            status = newStatus.displayName,
            statusTimestampsJson = Converters.timestampsToJson(currentTimestamps)
        )
    }

    // Analytics / Seller Earnings Calculations
    fun getSellerEarningsSummary(sellerId: String): Flow<SellerEarningsSummary> {
        return orderDao.getOrdersForSeller(sellerId).map { orders ->
            val deliveredOrders = orders.filter { it.status.equals(OrderStatus.DELIVERED.displayName, ignoreCase = true) }
            val now = System.currentTimeMillis()

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = now

            // Start of today
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val todayStart = calendar.timeInMillis

            // Start of week (7 days ago)
            val weekStart = todayStart - (6 * 86400000L)

            // Start of month (30 days ago)
            val monthStart = todayStart - (29 * 86400000L)

            val todayEarnings = deliveredOrders
                .filter { it.createdAtMillis >= todayStart }
                .sumOf { it.totalAmount }

            val weeklyEarnings = deliveredOrders
                .filter { it.createdAtMillis >= weekStart }
                .sumOf { it.totalAmount }

            val monthlyEarnings = deliveredOrders
                .filter { it.createdAtMillis >= monthStart }
                .sumOf { it.totalAmount }

            val allTimeEarnings = deliveredOrders.sumOf { it.totalAmount }
            val completedCount = deliveredOrders.size
            val avgOrderVal = if (completedCount > 0) allTimeEarnings / completedCount else 0.0

            // Weekly Trend: Last 7 days breakdown
            val dayLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            val weeklyTrend = mutableListOf<Pair<String, Double>>()
            for (i in 6 downTo 0) {
                val dayStartMillis = todayStart - (i * 86400000L)
                val dayEndMillis = dayStartMillis + 86400000L

                val dayCal = Calendar.getInstance().apply { timeInMillis = dayStartMillis }
                val dayOfWeek = dayCal.get(Calendar.DAY_OF_WEEK) // 1=Sun, 2=Mon...
                val labelIndex = if (dayOfWeek == Calendar.SUNDAY) 6 else dayOfWeek - 2
                val dayName = dayLabels.getOrElse(labelIndex) { "Day" }

                val amount = deliveredOrders
                    .filter { it.createdAtMillis in dayStartMillis..<dayEndMillis }
                    .sumOf { it.totalAmount }

                weeklyTrend.add(Pair(dayName, amount))
            }

            // Top Selling Products Breakdown
            val productCountMap = mutableMapOf<String, Int>()
            deliveredOrders.forEach { order ->
                val items = Converters.jsonToCartItems(order.itemsJson)
                items.forEach { item ->
                    val current = productCountMap.getOrDefault(item.productTitle, 0)
                    productCountMap[item.productTitle] = current + item.quantity
                }
            }

            val topSellingList = productCountMap.entries
                .sortedByDescending { it.value }
                .take(4)
                .map { Pair(it.key, it.value) }

            SellerEarningsSummary(
                todayEarnings = todayEarnings,
                weeklyEarnings = weeklyEarnings,
                monthlyEarnings = monthlyEarnings,
                allTimeEarnings = allTimeEarnings,
                totalOrdersCount = orders.size,
                completedOrdersCount = completedCount,
                averageOrderValue = avgOrderVal,
                weeklyTrend = weeklyTrend,
                topSellingProducts = topSellingList
            )
        }
    }
}
