package com.kyrie.domain.aboutMeTest

import com.kyrie.data.models.Profile
import com.kyrie.data.remote.State
import com.kyrie.data.repository.ProfileRepository
import com.kyrie.domain.ProfileUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class ProfileUseCaseTest {
    private val mockProfileRepository = mockk<ProfileRepository>()
    private val profileUseCase = ProfileUseCase(mockProfileRepository)

    @Test
    fun `getProfileData should return flow from repository`() =
        runTest {
            val testProfile = Profile(name = "Test Result Name", gmail = "testing@gmail.com")
            val testState: State<Profile?> = State.success(testProfile)
            coEvery {
                mockProfileRepository.getProfile()
            } returns flowOf(testState)
            val result = profileUseCase.getProfileData()
            result.collect { state ->
                Assert.assertEquals(testState, state)
            }
        }

    @Test
    fun `getProfileData should should error state`() =
        runTest {
            val errorState: State<Profile?> = State.failed("Error message")
            coEvery { mockProfileRepository.getProfile() } returns flowOf(errorState)
            val result = profileUseCase.getProfileData()
            result.collect { state ->
                Assert.assertEquals(errorState, state)
            }
        }
}
