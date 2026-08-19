package com.kyrie.myportfolio.setting.attribute

import androidx.lifecycle.ViewModel
import com.kyrie.domain.AttributeUseCase

class AttributeViewModel(private val attributeUseCase: AttributeUseCase) : ViewModel() {
    suspend fun getAttributeList() = attributeUseCase.getAttributeList()
}
