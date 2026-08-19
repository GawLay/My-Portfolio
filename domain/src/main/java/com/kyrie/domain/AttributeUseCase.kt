package com.kyrie.domain

import com.kyrie.data.repository.attribute.AttributeRepository

class AttributeUseCase(private val attributeRepository: AttributeRepository) {
    suspend fun getAttributeList() = attributeRepository.getAttributeList()
}
