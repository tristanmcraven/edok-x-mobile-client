package com.tristanmcraven.edok.model

import java.time.LocalDateTime

data class Order(
    val id: UInt,
    val userId: UInt,
    val restaurantId: UInt,
    val cartId: UInt,
    val status: String,
    val createdAt: LocalDateTime,
    val deliveredAt: LocalDateTime?,
    val cancelledAt: LocalDateTime?,
    val address: String,
    val total: UInt
)
