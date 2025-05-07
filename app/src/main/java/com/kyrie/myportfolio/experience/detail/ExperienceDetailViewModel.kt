package com.kyrie.myportfolio.experience.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kyrie.data.models.ExperienceDetail
import com.kyrie.data.repository.exp.ExpRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn

class ExperienceDetailViewModel(private val expRepository: ExpRepository) : ViewModel() {
    suspend fun getExpDetail(documentId: String) = expRepository.getExpDetail(documentId).stateIn(viewModelScope)

    private val _expDetail = MutableStateFlow<ExperienceDetail?>(null)
    val expDetail: StateFlow<ExperienceDetail?> = _expDetail.asStateFlow()

    fun setExperienceDetailsList(data: ExperienceDetail) {
        _expDetail.value = data
    }
}
