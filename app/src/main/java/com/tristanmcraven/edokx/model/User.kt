package com.tristanmcraven.edok.model

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
