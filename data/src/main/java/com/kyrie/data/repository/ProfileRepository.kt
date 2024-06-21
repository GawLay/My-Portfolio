package com.kyrie.data.repository

import com.kyrie.data.models.Profile
import com.kyrie.data.remote.State
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {

    suspend fun getProfile(): Flow<State<Profile?>>
}