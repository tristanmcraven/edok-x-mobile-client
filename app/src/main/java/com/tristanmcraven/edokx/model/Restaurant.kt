package com.tristanmcraven.edok.model

import java.time.LocalTime


data class Restaurant(
    val id: UInt,
    val name: String,
    val address: String,
    val openingTime: LocalTime,
    val closingTime: LocalTime,
    val legalInfo: String,
    val legalAddress: String,
    val inn: String,
    val managerId: UInt,
    val pending: Boolean,
    val rejectReason: String?,
    val disabled: Boolean,
    val disableReason: String,
    val image: ByteArray
)
