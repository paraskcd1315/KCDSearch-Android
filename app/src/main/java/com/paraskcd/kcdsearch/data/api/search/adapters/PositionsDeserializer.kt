package com.paraskcd.kcdsearch.data.api.search.adapters

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class PositionsDeserializer: JsonDeserializer<List<Int>> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?,
    ): List<Int> {
        if (json == null || json.isJsonNull) return emptyList()
        return when {
            json.isJsonArray -> json.asJsonArray.mapNotNull { elem ->
                if (elem.isJsonPrimitive && elem.asJsonPrimitive.isNumber) {
                    elem.asInt
                } else null
            }
            else -> emptyList()
        }
    }
}