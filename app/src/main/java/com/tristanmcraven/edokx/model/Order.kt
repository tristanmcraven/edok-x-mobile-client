package com.tristanmcraven.edok.model

import java.time.LocalDateTime

data class Order(
    val id: UInt,
    val userId: UInt,
    val restaurantId: UInt,
    val cartId: UInt,
    val status: String,
    val createdAt: String,
    val deliveredAt: String?,
    val cancelledAt: String?,
    val address: String,
    val total: UInt,
    val kitchenStatusId: UInt
)
