package com.kyrie.data.models


data class ExperienceDetailMap(
    val detail: ExperienceDetail? = null
)

data class ExperienceDetail(
    val id: String? = "",
    val jobTitle: String? = "",
    val jobType: String? = "",
    val location: String? = "",
    val position: String? = "",
    val timestamp: String? = "",
    val projects: List<ExpDetailsData>? = listOf(),
    val skills: List<String>? = listOf()
)

data class ExpDetailsData(
    val name: String? = "",
    val data: List<String>? = listOf()
)