package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.models.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        AddressEntity::class,
        SellerEntity::class,
        ProductEntity::class,
        OrderEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AquaDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun addressDao(): AddressDao
    abstract fun sellerDao(): SellerDao
    abstract fun productDao(): ProductDao
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile
        private var INSTANCE: AquaDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AquaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AquaDatabase::class.java,
                    "aquaconnect_db"
                )
                    .addCallback(AquaDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AquaDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database)
                }
            }
        }

        suspend fun populateDatabase(db: AquaDatabase) {
            val userDao = db.userDao()
            val addressDao = db.addressDao()
            val sellerDao = db.sellerDao()
            val productDao = db.productDao()
            val orderDao = db.orderDao()

            // 1. Users
            val customerUser = UserEntity(
                id = "usr_customer_1",
                name = "Alex Rivera",
                email = "alex@example.com",
                phone = "+1 555-0198",
                role = UserRole.CUSTOMER.name,
                avatarUrl = ""
            )
            val sellerUser1 = UserEntity(
                id = "usr_seller_1",
                name = "David Miller",
                email = "david@aquapure.com",
                phone = "+1 555-0245",
                role = UserRole.SELLER.name,
                sellerId = "seller_1"
            )
            val sellerUser2 = UserEntity(
                id = "usr_seller_2",
                name = "Elena Rostova",
                email = "elena@bluewave.com",
                phone = "+1 555-0377",
                role = UserRole.SELLER.name,
                sellerId = "seller_2"
            )
            userDao.insertUser(customerUser)
            userDao.insertUser(sellerUser1)
            userDao.insertUser(sellerUser2)

            // 2. Addresses
            val addr1 = AddressEntity(
                id = "addr_1",
                userId = "usr_customer_1",
                label = "Home",
                addressText = "742 Evergreen Terrace, Apt 4B, Metro City",
                landmark = "Near Central Water Tower",
                isDefault = true
            )
            val addr2 = AddressEntity(
                id = "addr_2",
                userId = "usr_customer_1",
                label = "Work",
                addressText = "Innovation Hub Tower, 12th Floor, Tech District",
                landmark = "Opposite Metro Gate 3",
                isDefault = false
            )
            addressDao.insertAddress(addr1)
            addressDao.insertAddress(addr2)

            // 3. Sellers
            val seller1 = SellerEntity(
                id = "seller_1",
                ownerName = "David Miller",
                brandName = "AquaPure Express",
                locationAddress = "24 Waterworks Rd, Central Metro (1.2 km)",
                phone = "+1 555-0245",
                businessHours = "7:00 AM - 9:00 PM",
                description = "RO + UV 7-stage purified mineral water. Leak-proof sealed 20L & 15L jugs with express 20-min delivery.",
                rating = 4.9f,
                reviewCount = 284,
                isVerified = true
            )
            val seller2 = SellerEntity(
                id = "seller_2",
                ownerName = "Elena Rostova",
                brandName = "BlueWave Hydro Dealers",
                locationAddress = "108 Ocean Boulevard, East District (2.8 km)",
                phone = "+1 555-0377",
                businessHours = "8:00 AM - 8:00 PM",
                description = "Natural spring alkaline drinking water (pH 8.5). Ergonomic handles and BPA-free food grade containers.",
                rating = 4.7f,
                reviewCount = 196,
                isVerified = true
            )
            val seller3 = SellerEntity(
                id = "seller_3",
                ownerName = "Marcus Chen",
                brandName = "HydroFresh Oasis",
                locationAddress = "55 Spring Ave, South Heights (3.5 km)",
                phone = "+1 555-0812",
                businessHours = "6:30 AM - 10:00 PM",
                description = "Ultra-filtered oxygenated water. Eco-refill program with instant jug exchange service.",
                rating = 4.8f,
                reviewCount = 142,
                isVerified = true
            )
            sellerDao.insertSeller(seller1)
            sellerDao.insertSeller(seller2)
            sellerDao.insertSeller(seller3)

            // 4. Products
            val prod1 = ProductEntity(
                id = "prod_101",
                sellerId = "seller_1",
                brand = "AquaPure Classic",
                capacityLiters = 20f,
                weightKg = 20f,
                price = 4.50,
                inStock = true,
                deliveryTimeEst = "20-30 mins",
                description = "20 Liter RO + UV Purified Drinking Water Jug. Double sealed cap."
            )
            val prod2 = ProductEntity(
                id = "prod_102",
                sellerId = "seller_1",
                brand = "AquaPure EasyTap",
                capacityLiters = 15f,
                weightKg = 15f,
                price = 3.80,
                inStock = true,
                deliveryTimeEst = "20-30 mins",
                description = "15 Liter Jug with integrated front push-spigot tap. Great for desks and countertops."
            )
            val prod3 = ProductEntity(
                id = "prod_103",
                sellerId = "seller_1",
                brand = "AquaPure Max Commercial",
                capacityLiters = 25f,
                weightKg = 25f,
                price = 5.80,
                inStock = true,
                deliveryTimeEst = "30-40 mins",
                description = "25 Liter heavy-duty water canister for offices, workouts, and gatherings."
            )

            val prod4 = ProductEntity(
                id = "prod_201",
                sellerId = "seller_2",
                brand = "BlueWave Spring",
                capacityLiters = 20f,
                weightKg = 20f,
                price = 5.00,
                inStock = true,
                deliveryTimeEst = "25-35 mins",
                description = "20 Liter Natural Spring Water with balanced electrolytes."
            )
            val prod5 = ProductEntity(
                id = "prod_202",
                sellerId = "seller_2",
                brand = "BlueWave Alkaline 8.5+",
                capacityLiters = 20f,
                weightKg = 20f,
                price = 6.50,
                inStock = true,
                deliveryTimeEst = "25-35 mins",
                description = "pH 8.5+ High Alkaline Water in UV-protected royal blue container."
            )

            val prod6 = ProductEntity(
                id = "prod_301",
                sellerId = "seller_3",
                brand = "HydroFresh Express",
                capacityLiters = 20f,
                weightKg = 20f,
                price = 4.20,
                inStock = true,
                deliveryTimeEst = "15-25 mins",
                description = "20 Liter chilled eco-filtered jug delivered on electric delivery bikes."
            )

            productDao.insertProduct(prod1)
            productDao.insertProduct(prod2)
            productDao.insertProduct(prod3)
            productDao.insertProduct(prod4)
            productDao.insertProduct(prod5)
            productDao.insertProduct(prod6)

            // 5. Initial Sample Orders
            val now = System.currentTimeMillis()
            val hourMs = 3600000L
            val dayMs = 86400000L

            val order1Cart = listOf(
                CartItem(
                    productId = "prod_101",
                    productTitle = "AquaPure Classic 20L",
                    capacityLiters = 20f,
                    unitPrice = 4.50,
                    quantity = 2
                )
            )
            val order1Timestamps = mapOf(
                OrderStatus.PENDING.displayName to now - (2 * hourMs),
                OrderStatus.PROCESSING.displayName to now - (90 * 60000L),
                OrderStatus.PACKED.displayName to now - (60 * 60000L),
                OrderStatus.PICKED_UP.displayName to now - (40 * 60000L),
                OrderStatus.OUT_FOR_DELIVERY.displayName to now - (15 * 60000L)
            )
            val order1 = OrderEntity(
                id = "ord_1001",
                orderNumber = "AQ-9201",
                customerId = "usr_customer_1",
                customerName = "Alex Rivera",
                customerPhone = "+1 555-0198",
                deliveryAddressText = "742 Evergreen Terrace, Apt 4B, Metro City",
                sellerId = "seller_1",
                sellerBrandName = "AquaPure Express",
                sellerPhone = "+1 555-0245",
                itemsJson = Converters.cartItemsToJson(order1Cart),
                totalAmount = 9.00,
                status = OrderStatus.OUT_FOR_DELIVERY.displayName,
                statusTimestampsJson = Converters.timestampsToJson(order1Timestamps),
                createdAtMillis = now - (2 * hourMs)
            )

            val order2Cart = listOf(
                CartItem(
                    productId = "prod_201",
                    productTitle = "BlueWave Spring 20L",
                    capacityLiters = 20f,
                    unitPrice = 5.00,
                    quantity = 2
                )
            )
            val order2Timestamps = mapOf(
                OrderStatus.PENDING.displayName to now - (1 * dayMs),
                OrderStatus.PROCESSING.displayName to now - (1 * dayMs - 15 * 60000L),
                OrderStatus.PACKED.displayName to now - (1 * dayMs - 30 * 60000L),
                OrderStatus.OUT_FOR_DELIVERY.displayName to now - (1 * dayMs - 45 * 60000L),
                OrderStatus.DELIVERED.displayName to now - (1 * dayMs - 60 * 60000L)
            )
            val order2 = OrderEntity(
                id = "ord_1002",
                orderNumber = "AQ-8842",
                customerId = "usr_customer_1",
                customerName = "Alex Rivera",
                customerPhone = "+1 555-0198",
                deliveryAddressText = "Innovation Hub Tower, 12th Floor, Tech District",
                sellerId = "seller_2",
                sellerBrandName = "BlueWave Hydro Dealers",
                sellerPhone = "+1 555-0377",
                itemsJson = Converters.cartItemsToJson(order2Cart),
                totalAmount = 10.00,
                status = OrderStatus.DELIVERED.displayName,
                statusTimestampsJson = Converters.timestampsToJson(order2Timestamps),
                createdAtMillis = now - (1 * dayMs)
            )

            val order3Cart = listOf(
                CartItem(
                    productId = "prod_101",
                    productTitle = "AquaPure Classic 20L",
                    capacityLiters = 20f,
                    unitPrice = 4.50,
                    quantity = 3
                )
            )
            val order3Timestamps = mapOf(
                OrderStatus.PENDING.displayName to now - (2 * dayMs),
                OrderStatus.DELIVERED.displayName to now - (2 * dayMs - 90 * 60000L)
            )
            val order3 = OrderEntity(
                id = "ord_1003",
                orderNumber = "AQ-7512",
                customerId = "usr_customer_1",
                customerName = "Alex Rivera",
                customerPhone = "+1 555-0198",
                deliveryAddressText = "742 Evergreen Terrace, Apt 4B, Metro City",
                sellerId = "seller_1",
                sellerBrandName = "AquaPure Express",
                sellerPhone = "+1 555-0245",
                itemsJson = Converters.cartItemsToJson(order3Cart),
                totalAmount = 13.50,
                status = OrderStatus.DELIVERED.displayName,
                statusTimestampsJson = Converters.timestampsToJson(order3Timestamps),
                createdAtMillis = now - (2 * dayMs)
            )

            // More delivered orders for seller_1 so earnings dashboard is full of analytics data
            for (i in 3..7) {
                val pastDays = i.toLong()
                val cart = listOf(
                    CartItem("prod_101", "AquaPure Classic 20L", 20f, 4.50, 2),
                    CartItem("prod_102", "AquaPure EasyTap 15L", 15f, 3.80, 1)
                )
                val total = 12.80
                val pastOrder = OrderEntity(
                    id = "ord_past_$i",
                    orderNumber = "AQ-${6000 + i}",
                    customerId = "usr_customer_$i",
                    customerName = "Customer #$i",
                    customerPhone = "+1 555-000$i",
                    deliveryAddressText = "Street $i, Sector $i, Metro",
                    sellerId = "seller_1",
                    sellerBrandName = "AquaPure Express",
                    sellerPhone = "+1 555-0245",
                    itemsJson = Converters.cartItemsToJson(cart),
                    totalAmount = total,
                    status = OrderStatus.DELIVERED.displayName,
                    statusTimestampsJson = Converters.timestampsToJson(mapOf(OrderStatus.DELIVERED.displayName to now - (pastDays * dayMs))),
                    createdAtMillis = now - (pastDays * dayMs)
                )
                orderDao.insertOrder(pastOrder)
            }

            orderDao.insertOrder(order1)
            orderDao.insertOrder(order2)
            orderDao.insertOrder(order3)
        }
    }
}
