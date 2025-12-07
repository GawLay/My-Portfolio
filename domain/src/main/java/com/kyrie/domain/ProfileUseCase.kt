package com.kyrie.domain

import com.kyrie.data.repository.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn

class ProfileUseCase(private val profileRepositoryImpl: ProfileRepository) {
    suspend fun getProfileData() = profileRepositoryImpl.getProfile().flowOn(Dispatchers.IO)
}
