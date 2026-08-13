package com.example.refluenceds.data.remote.dto

import com.example.refluenceds.domain.model.Product
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductResponse(
    @Json(name = "products") val products: List<ProductDto>
)

@JsonClass(generateAdapter = true)
data class ProductDto(
    @Json(name = "id") val id: Int,
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String,
    @Json(name = "price") val price: Double
) {
    fun toDomain(): Product {
        return Product(
            id = id,
            title = title,
            description = description,
            price = price
        )
    }
}
