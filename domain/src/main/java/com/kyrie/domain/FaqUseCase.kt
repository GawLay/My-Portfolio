package com.kyrie.domain

import com.kyrie.data.repository.faq.FaqRepository

class FaqUseCase(private val faqRepository: FaqRepository) {
    suspend fun getFaqList() = faqRepository.getFaqList()
}
