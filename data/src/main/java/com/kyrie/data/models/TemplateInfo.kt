package com.kyrie.data.models

data class TemplateInfo(
    val template_title: String? = "",
    val data: ArrayList<TemplateData>? = arrayListOf(),
)

data class TemplateData(
    val name: String? = "",
    val description: String? = "",
    val file_name: String? = "",
    val image_url: String? = "",
)
