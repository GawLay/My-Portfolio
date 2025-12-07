package com.kyrie.data.repository.skill

import com.kyrie.data.models.Skills
import com.kyrie.data.remote.State
import kotlinx.coroutines.flow.Flow

interface SkillsRepository {
    suspend fun getSkillList(): Flow<State<Skills?>>
}
