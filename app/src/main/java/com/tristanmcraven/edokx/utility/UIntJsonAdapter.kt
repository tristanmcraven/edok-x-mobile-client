package com.tristanmcraven.edokx.utility

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson

class UIntJsonAdapter : JsonAdapter<UInt>() {
    @FromJson
    override fun fromJson(p0: JsonReader): UInt? {
        return p0.nextString()?.toUIntOrNull()
    }

    @ToJson
    override fun toJson(p0: JsonWriter, p1: UInt?) {
        p0.value(p1?.toString())
    }
}