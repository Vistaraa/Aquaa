package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.models.*
import com.example.data.repository.AquaRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SellerViewModel(
    private val repository: AquaRepository
) : ViewModel() {

    // Current Seller ID (Defaults to seller_1)
    val activeSellerId = MutableStateFlow("seller_1")

    @OptIn(ExperimentalCoroutinesApi::class)
    val sellerProfile: StateFlow<SellerEntity?> = activeSellerId.flatMapLatest { id ->
        repository.observeSellerById(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val sellerProducts: StateFlow<List<ProductEntity>> = activeSellerId.flatMapLatest { id ->
        repository.getProductsBySeller(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val earningsSummary: StateFlow<SellerEarningsSummary?> = activeSellerId.flatMapLatest { id ->
        repository.getSellerEarningsSummary(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Orders Filter State
    val selectedStatusFilter = MutableStateFlow<OrderStatus?>(null)
    val orderSearchQuery = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    private val rawSellerOrders: Flow<List<OrderEntity>> = activeSellerId.flatMapLatest { id ->
        repository.getOrdersForSeller(id)
    }

    val filteredSellerOrders: StateFlow<List<OrderEntity>> = combine(
        rawSellerOrders,
        selectedStatusFilter,
        orderSearchQuery
    ) { orders, statusFilter, query ->
        orders.filter { order ->
            val matchesStatus = statusFilter == null ||
                    order.status.equals(statusFilter.displayName, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                    order.customerName.contains(query, ignoreCase = true) ||
                    order.orderNumber.contains(query, ignoreCase = true) ||
                    order.deliveryAddressText.contains(query, ignoreCase = true)
            matchesStatus && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Order Pipeline Actions
    fun updateOrderStatus(orderId: String, nextStatus: OrderStatus) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, nextStatus)
        }
    }

    // Product Inventory Actions
    fun saveProduct(
        id: String?,
        brand: String,
        capacityLiters: Float,
        weightKg: Float,
        price: Double,
        inStock: Boolean,
        deliveryTimeEst: String,
        description: String
    ) {
        viewModelScope.launch {
            val sellerId = activeSellerId.value
            val productId = id ?: "prod_${System.currentTimeMillis()}"
            val product = ProductEntity(
                id = productId,
                sellerId = sellerId,
                brand = brand,
                capacityLiters = capacityLiters,
                weightKg = weightKg,
                price = price,
                inStock = inStock,
                deliveryTimeEst = deliveryTimeEst,
                description = description
            )
            if (id == null) {
                repository.addProduct(product)
            } else {
                repository.updateProduct(product)
            }
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            repository.deleteProduct(productId)
        }
    }

    // Profile Actions
    fun updateProfile(
        brandName: String,
        ownerName: String,
        phone: String,
        locationAddress: String,
        businessHours: String,
        description: String
    ) {
        viewModelScope.launch {
            val current = sellerProfile.value ?: return@launch
            val updated = current.copy(
                brandName = brandName,
                ownerName = ownerName,
                phone = phone,
                locationAddress = locationAddress,
                businessHours = businessHours,
                description = description
            )
            repository.updateSellerProfile(updated)
        }
    }
}
