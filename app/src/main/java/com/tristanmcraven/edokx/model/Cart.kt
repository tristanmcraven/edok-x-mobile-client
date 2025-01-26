package com.tristanmcraven.edok.model

data class Cart(
    val id: UInt,
    val userId: UInt,
    val restaurantId: UInt,
    val status: String
)
