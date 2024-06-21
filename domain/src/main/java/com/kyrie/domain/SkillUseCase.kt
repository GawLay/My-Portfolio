package com.kyrie.domain

import com.kyrie.data.repository.skill.SkillsRepository

class SkillUseCase(private val skillsRepository: SkillsRepository) {
    suspend fun getSkills() =
        skillsRepository.getSkillList()

}