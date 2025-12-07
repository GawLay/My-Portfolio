package com.kyrie.domain

import com.kyrie.data.repository.exp.ExpRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn

class ExpUseCase(private val expRepository: ExpRepository) {
    suspend fun getExpList() = expRepository.getExpList().flowOn(Dispatchers.IO)

    suspend fun getExpDetail(documentId: String) = expRepository.getExpDetail(documentId).flowOn(Dispatchers.IO)
}
