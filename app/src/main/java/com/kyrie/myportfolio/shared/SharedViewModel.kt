package com.kyrie.myportfolio.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kyrie.domain.ExpUseCase
import com.kyrie.domain.ProfileUseCase
import kotlinx.coroutines.flow.stateIn

class SharedViewModel(
    private val expUseCase: ExpUseCase,
    private val profileUseCase: ProfileUseCase,
) : ViewModel() {
    suspend fun getProfile() = profileUseCase.getProfileData().stateIn(viewModelScope)

    suspend fun getExpList() = expUseCase.getExpList().stateIn(viewModelScope)
}
