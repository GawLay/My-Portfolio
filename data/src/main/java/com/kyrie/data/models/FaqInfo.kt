package com.kyrie.data.models

data class FaqInfo(
    val data: List<FaqData>? = listOf(),
)

data class FaqData(
    var isExpanded: Boolean = false,
    val question: String? = "",
    val answers: List<String> = listOf(),
)
