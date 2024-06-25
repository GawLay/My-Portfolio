package com.kyrie.data.models


data class Experiences(
    val data: ArrayList<ExperiencesData>? = arrayListOf()
)

data class ExperiencesData(
    val id: String? = "",
    val jobTitle: String? = "",
    val jobType: String? = "",
    val location: String? = "",
    val position: String? = "",
    val timestamp: String? = "",
    val priority: Int = 0
)

