package com.paraskcd.kcdsearch.data.api.search.adapters

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class AnswersDeserializer: JsonDeserializer<List<String>> {
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
                elem.isJsonObject -> extractStringFromObject(elem.asJsonObject)
                else -> null
            }
        }
    }

    private fun extractStringFromObject(obj: com.google.gson.JsonObject): String? {
        val keys = listOf("answer", "text", "title", "content")
        for (key in keys) {
            obj.get(key)?.takeIf { it.isJsonPrimitive && it.asJsonPrimitive.isString }?.asString?.let { return it }
        }
        obj.get("translations")?.takeIf { it.isJsonArray }?.asJsonArray?.firstOrNull()
            ?.takeIf { it.isJsonObject }?.asJsonObject?.get("text")
            ?.takeIf { it.isJsonPrimitive && it.asJsonPrimitive.isString }?.asString?.let { return it }
        return null
    }
}