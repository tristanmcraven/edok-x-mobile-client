package com.tristanmcraven.edok.model

import android.os.Parcel
import android.os.Parcelable
import java.time.LocalTime


data class Restaurant(
    val id: UInt,
    val name: String,
    val address: String,
    val openingTime: String,
    val closingTime: String,
    val legalInfo: String,
    val legalAddress: String,
    val inn: String,
    val managerId: UInt,
    val pending: Boolean,
    val rejectReason: String?,
    val disabled: Boolean,
    val disableReason: String?,
    val image: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt().toUInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt().toUInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readString(),
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id.toInt())
        parcel.writeString(name)
        parcel.writeString(address)
        parcel.writeString(openingTime)
        parcel.writeString(closingTime)
        parcel.writeString(legalInfo)
        parcel.writeString(legalAddress)
        parcel.writeString(inn)
        parcel.writeInt(managerId.toInt())
        parcel.writeByte(if (pending) 1 else 0)
        parcel.writeString(rejectReason)
        parcel.writeByte(if (disabled) 1 else 0)
        parcel.writeString(disableReason)
        parcel.writeString(image)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Restaurant> {
        override fun createFromParcel(parcel: Parcel): Restaurant {
            return Restaurant(parcel)
        }

        override fun newArray(size: Int): Array<Restaurant?> {
            return arrayOfNulls(size)
        }
    }
}
