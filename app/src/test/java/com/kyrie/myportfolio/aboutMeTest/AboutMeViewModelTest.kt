package com.kyrie.myportfolio.aboutMeTest

import com.kyrie.data.models.Profile
import com.kyrie.data.remote.State
import com.kyrie.domain.ProfileUseCase
import com.kyrie.myportfolio.aboutMe.AboutMeViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class AboutMeViewModelTest {
    private val mockProfileUseCase = mockk<ProfileUseCase>()
    private val viewModel = AboutMeViewModel(mockProfileUseCase)

    @Test
    fun `getProfile should return State from useCase`() =
        runTest {
            val testProfile = Profile(name = "Test Result Name", gmail = "testing@gmail.com")
            val testState: State<Profile?> = State.success(testProfile)
            coEvery { mockProfileUseCase.getProfileData() } returns flowOf(testState)
            val result = viewModel.getProfile()
            Assert.assertEquals(testState, result.value)
        }
}
