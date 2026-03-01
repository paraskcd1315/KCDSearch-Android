package com.paraskcd.kcdsearch.data.api.contacts.dataSources

data class ContactResult(
    val name: String,
    val number: String,
    val photoUri: String? = null
)
