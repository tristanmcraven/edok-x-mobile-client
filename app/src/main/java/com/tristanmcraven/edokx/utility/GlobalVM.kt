package com.tristanmcraven.edokx.utility

import com.tristanmcraven.edok.model.Cart
import com.tristanmcraven.edok.model.User

object GlobalVM {
    var currentUser: User? = null
    var carts: List<Cart>? = null
}