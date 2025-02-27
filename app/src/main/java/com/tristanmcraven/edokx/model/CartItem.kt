package com.tristanmcraven.edok.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartItem(
    val id: UInt,
    val cartId: UInt,
    val foodId: UInt,
    var foodQuantity: UInt,
    val utensilsQuantity: UInt
) : Parcelable
