package com.tristanmcraven.edok.model

import android.os.Parcel
import android.os.Parcelable

data class User(
    val id: UInt,
    val name: String,
    val surname: String,
    val phoneNumber: String,
    val email: String?,
    val login: String,
    val password: String,
    val pfp: ByteArray?,
    val lastAddress: String?
)
//    : Parcelable {
//    constructor(parcel: Parcel) : this(
//        parcel.readInt().toUInt(),
//        parcel.readString()!!,
//        parcel.readString()!!,
//        parcel.readString()!!,
//        parcel.readString(),
//        parcel.readString()!!,
//        parcel.readString()!!,
//        parcel.read,
//        parcel.readString()!!
//    )
//}
