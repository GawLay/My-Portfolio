package com.kyrie.data.models

data class AttributeInfo(
    val data: List<Attributes>? = listOf(),
)

data class Attributes(
    val name: String = "",
    val attributeLine: String = "",
)
