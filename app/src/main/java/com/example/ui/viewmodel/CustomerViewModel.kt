package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.Converters
import com.example.data.models.*
import com.example.data.repository.AquaRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CustomerViewModel(
    private val repository: AquaRepository
) : ViewModel() {

    // User State
    val currentUser: StateFlow<UserEntity?> = repository.currentUser

    // Discover Sellers Search & Filters
    val searchQuery = MutableStateFlow("")
    val isVerifiedOnlyFilter = MutableStateFlow(false)

    val sellers: StateFlow<List<SellerEntity>> = combine(
        repository.allSellers,
        searchQuery,
        isVerifiedOnlyFilter
    ) { sellersList, query, verifiedOnly ->
        sellersList.filter { seller ->
            val matchesQuery = query.isBlank() ||
                    seller.brandName.contains(query, ignoreCase = true) ||
                    seller.ownerName.contains(query, ignoreCase = true) ||
                    seller.locationAddress.contains(query, ignoreCase = true)
            val matchesVerified = !verifiedOnly || seller.isVerified
            matchesQuery && matchesVerified
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected Seller & Products View
    private val _selectedSellerId = MutableStateFlow<String?>("seller_1")
    val selectedSellerId: StateFlow<String?> = _selectedSellerId.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedSeller: StateFlow<SellerEntity?> = _selectedSellerId.flatMapLatest { id ->
        if (id == null) flowOf(null) else repository.observeSellerById(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedSellerProducts: StateFlow<List<ProductEntity>> = _selectedSellerId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else repository.getProductsBySeller(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectSeller(sellerId: String) {
        if (_selectedSellerId.value != sellerId) {
            _selectedSellerId.value = sellerId
            // Clear cart if switching seller
            _cartItems.value = emptyMap()
        }
    }

    // Cart Management
    private val _cartItems = MutableStateFlow<Map<String, CartItem>>(emptyMap())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.map { it.values.toList() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartTotal: StateFlow<Double> = _cartItems.map { map ->
        map.values.sumOf { it.total }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cartItemCount: StateFlow<Int> = _cartItems.map { map ->
        map.values.sumOf { it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun addToCart(product: ProductEntity) {
        val currentMap = _cartItems.value.toMutableMap()
        val existing = currentMap[product.id]
        if (existing != null) {
            currentMap[product.id] = existing.copy(quantity = existing.quantity + 1)
        } else {
            currentMap[product.id] = CartItem(
                productId = product.id,
                productTitle = "${product.brand} ${product.capacityLiters.toInt()}L",
                capacityLiters = product.capacityLiters,
                unitPrice = product.price,
                quantity = 1,
                imageUrl = product.imageUrl
            )
        }
        _cartItems.value = currentMap
    }

    fun decrementInCart(productId: String) {
        val currentMap = _cartItems.value.toMutableMap()
        val existing = currentMap[productId] ?: return
        if (existing.quantity > 1) {
            currentMap[productId] = existing.copy(quantity = existing.quantity - 1)
        } else {
            currentMap.remove(productId)
        }
        _cartItems.value = currentMap
    }

    fun clearCart() {
        _cartItems.value = emptyMap()
    }

    // Saved Addresses
    @OptIn(ExperimentalCoroutinesApi::class)
    val savedAddresses: StateFlow<List<AddressEntity>> = currentUser.flatMapLatest { user ->
        if (user == null) flowOf(emptyList()) else repository.getAddressesForUser(user.id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedAddressText = MutableStateFlow("742 Evergreen Terrace, Apt 4B, Metro City")

    fun addAddress(label: String, text: String, landmark: String, isDefault: Boolean) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            val newAddr = AddressEntity(
                id = "addr_${System.currentTimeMillis()}",
                userId = user.id,
                label = label,
                addressText = text,
                landmark = landmark,
                isDefault = isDefault
            )
            repository.addAddress(newAddr)
            selectedAddressText.value = text
        }
    }

    // Customer Orders
    @OptIn(ExperimentalCoroutinesApi::class)
    val customerOrders: StateFlow<List<OrderEntity>> = currentUser.flatMapLatest { user ->
        if (user == null) flowOf(emptyList()) else repository.getOrdersForCustomer(user.id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _orderPlacedSuccessId = MutableStateFlow<String?>(null)
    val orderPlacedSuccessId: StateFlow<String?> = _orderPlacedSuccessId.asStateFlow()

    fun placeOrder(onComplete: (String) -> Unit) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            val seller = selectedSeller.value ?: return@launch
            val items = cartItems.value
            if (items.isEmpty()) return@launch

            val orderId = repository.placeOrder(
                customer = user,
                deliveryAddress = selectedAddressText.value,
                seller = seller,
                cartItems = items
            )

            clearCart()
            _orderPlacedSuccessId.value = orderId
            onComplete(orderId)
        }
    }

    fun reorder(order: OrderEntity) {
        viewModelScope.launch {
            val items = Converters.jsonToCartItems(order.itemsJson)
            val newCart = items.associateBy { it.productId }
            _selectedSellerId.value = order.sellerId
            _cartItems.value = newCart
        }
    }

    fun dismissOrderSuccess() {
        _orderPlacedSuccessId.value = null
    }

    fun updateOrderStatus(orderId: String, nextStatus: OrderStatus) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, nextStatus)
        }
    }
}
