package com.kyrie.data.repository.attribute

import com.kyrie.data.models.AttributeInfo
import com.kyrie.data.remote.State
import kotlinx.coroutines.flow.Flow

interface AttributeRepository {
    suspend fun getAttributeList(): Flow<State<AttributeInfo?>>
}
