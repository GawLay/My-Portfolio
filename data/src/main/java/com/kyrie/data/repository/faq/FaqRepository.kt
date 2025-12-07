package com.kyrie.data.repository.faq

import com.kyrie.data.models.FaqInfo
import com.kyrie.data.remote.State
import kotlinx.coroutines.flow.Flow

interface FaqRepository {
    suspend fun getFaqList(): Flow<State<FaqInfo?>>
}
