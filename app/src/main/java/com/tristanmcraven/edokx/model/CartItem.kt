package com.tristanmcraven.edok.model

data class CartItem(
    val id: UInt,
    val cartId: UInt,
    val foodId: UInt,
    val foodQuantity: UInt,
    val utensilsQuantity: UInt
)
