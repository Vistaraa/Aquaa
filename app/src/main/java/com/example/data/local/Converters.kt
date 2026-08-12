package com.example.data.local

import com.example.data.models.CartItem
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object Converters {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val cartListType = Types.newParameterizedType(List::class.java, CartItem::class.java)
    private val cartListAdapter = moshi.adapter<List<CartItem>>(cartListType)

    private val mapType = Types.newParameterizedType(Map::class.java, String::class.java, Long::class.javaObjectType)
    private val mapAdapter = moshi.adapter<Map<String, Long>>(mapType)

    fun cartItemsToJson(items: List<CartItem>): String {
        return cartListAdapter.toJson(items)
    }

    fun jsonToCartItems(json: String): List<CartItem> {
        return try {
            cartListAdapter.fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun timestampsToJson(map: Map<String, Long>): String {
        return mapAdapter.toJson(map)
    }

    fun jsonToTimestamps(json: String): Map<String, Long> {
        return try {
            mapAdapter.fromJson(json) ?: emptyMap()
        } catch (e: Exception) {
            emptyMap()
        }
    }
}
