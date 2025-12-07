package com.kyrie.data.repository.exp

import com.kyrie.data.models.ExperienceDetailMap
import com.kyrie.data.models.Experiences
import com.kyrie.data.remote.State
import kotlinx.coroutines.flow.Flow

interface ExpRepository {
    suspend fun getExpList(): Flow<State<Experiences?>>

    suspend fun getExpDetail(documentId: String): Flow<State<ExperienceDetailMap?>>
}
