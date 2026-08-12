package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole {
    CUSTOMER, SELLER
}

enum class OrderStatus(val displayName: String, val stepOrder: Int) {
    PENDING("Pending", 1),
    PROCESSING("Processing", 2),
    PACKED("Packed", 3),
    PICKED_UP("Picked Up", 4),
    OUT_FOR_DELIVERY("Out for Delivery", 5),
    DELIVERED("Delivered", 6),
    CANCELLED("Cancelled", 0);

    companion object {
        fun fromString(status: String): OrderStatus {
            return entries.find { it.displayName.equals(status, ignoreCase = true) } ?: PENDING
        }
    }
}

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val role: String, // "CUSTOMER" or "SELLER"
    val avatarUrl: String = "",
    val sellerId: String? = null
)

@Entity(tableName = "addresses")
data class AddressEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val label: String, // "Home", "Work", "Other"
    val addressText: String,
    val landmark: String = "",
    val isDefault: Boolean = false
)

@Entity(tableName = "sellers")
data class SellerEntity(
    @PrimaryKey val id: String,
    val ownerName: String,
    val brandName: String,
    val locationAddress: String,
    val latitude: Double = 12.9716,
    val longitude: Double = 77.5946,
    val phone: String,
    val businessHours: String = "8:00 AM - 8:00 PM",
    val photoUrl: String = "",
    val description: String = "",
    val isVerified: Boolean = true,
    val rating: Float = 4.8f,
    val reviewCount: Int = 124
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val sellerId: String,
    val brand: String,
    val capacityLiters: Float,
    val weightKg: Float,
    val price: Double,
    val inStock: Boolean = true,
    val deliveryTimeEst: String = "30-45 mins",
    val imageUrl: String = "",
    val description: String = ""
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val orderNumber: String,
    val customerId: String,
    val customerName: String,
    val customerPhone: String,
    val deliveryAddressText: String,
    val sellerId: String,
    val sellerBrandName: String,
    val sellerPhone: String,
    val itemsJson: String, // JSON array of CartItem
    val totalAmount: Double,
    val status: String, // OrderStatus displayName
    val statusTimestampsJson: String, // Map<String, Long> as JSON
    val createdAtMillis: Long = System.currentTimeMillis()
)

data class CartItem(
    val productId: String,
    val productTitle: String,
    val capacityLiters: Float,
    val unitPrice: Double,
    val quantity: Int,
    val imageUrl: String = ""
) {
    val total: Double get() = unitPrice * quantity
}

data class SellerEarningsSummary(
    val todayEarnings: Double,
    val weeklyEarnings: Double,
    val monthlyEarnings: Double,
    val allTimeEarnings: Double,
    val totalOrdersCount: Int,
    val completedOrdersCount: Int,
    val averageOrderValue: Double,
    val weeklyTrend: List<Pair<String, Double>>, // Day -> Amount
    val topSellingProducts: List<Pair<String, Int>> // Product title -> Units sold
)
