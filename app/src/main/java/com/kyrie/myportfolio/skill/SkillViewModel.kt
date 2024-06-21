package com.kyrie.myportfolio.skill

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kyrie.domain.SkillUseCase
import kotlinx.coroutines.flow.stateIn

class SkillViewModel(private val useCase: SkillUseCase):ViewModel() {
    suspend fun getSkills() = useCase.getSkills().stateIn(scope = viewModelScope)
}