package com.tristanmcraven.edok.model

data class Food(
    val id: UInt,
    val name: String,
    val restaurantId: UInt,
    val foodCategoryId: UInt,
    val photo: ByteArray,
    val description: String,
    val price: UInt,
    val disabled: Boolean
)
