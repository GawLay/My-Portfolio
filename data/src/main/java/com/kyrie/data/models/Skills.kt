package com.kyrie.data.models

data class Skills(
    val data: ArrayList<SkillsData>? = arrayListOf(),
)

data class SkillsData(
    val name: String? = "",
    val url: String? = "",
    val priority: Int = 0,
)
