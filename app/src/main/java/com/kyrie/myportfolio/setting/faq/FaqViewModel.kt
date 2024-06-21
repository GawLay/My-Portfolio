package com.kyrie.myportfolio.setting.faq

import androidx.lifecycle.ViewModel
import com.kyrie.domain.FaqUseCase

class FaqViewModel(private val faqUseCase: FaqUseCase) : ViewModel() {
    suspend fun getFaqList() = faqUseCase.getFaqList()
}