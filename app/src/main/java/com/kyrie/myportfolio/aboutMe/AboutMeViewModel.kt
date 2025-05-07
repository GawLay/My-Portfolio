package com.kyrie.myportfolio.aboutMe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kyrie.domain.ProfileUseCase
import kotlinx.coroutines.flow.stateIn

class AboutMeViewModel(private val profileUseCase: ProfileUseCase) : ViewModel() {
    suspend fun getProfile() =
        profileUseCase.getProfileData().stateIn(
            scope = viewModelScope,
        )
}
