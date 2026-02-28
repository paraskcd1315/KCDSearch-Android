package com.paraskcd.kcdsearch.data.api.search.adapters

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class UnresponsiveEnginesDeserializer : JsonDeserializer<List<String>> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): List<String>? {
        if (json == null || json.isJsonNull) return emptyList()
        if (!json.isJsonArray) return emptyList()
        return json.asJsonArray.mapNotNull { elem ->
            when {
                elem.isJsonPrimitive && elem.asJsonPrimitive.isString -> elem.asString
                elem.isJsonArray -> elem.asJsonArray.firstOrNull()
                    ?.takeIf { it.isJsonPrimitive && it.asJsonPrimitive.isString }
                    ?.asString
                else -> null
            }
        }
    }
}