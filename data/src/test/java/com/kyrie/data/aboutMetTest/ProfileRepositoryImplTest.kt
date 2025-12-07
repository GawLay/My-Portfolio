package com.kyrie.data.aboutMetTest

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.kyrie.data.models.Profile
import com.kyrie.data.remote.State
import com.kyrie.data.repository.ProfileRepositoryImpl
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.fail
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class ProfileRepositoryImplTest {
    private val mockFireStore = mockk<FirebaseFirestore>()
    private val mockDocumentSnapshot = mockk<DocumentSnapshot>()
    private val repository = ProfileRepositoryImpl()

    private fun assertState(
        state: State<Profile?>,
        expectedData: Profile? = null,
        expectedError: String? = null,
    ) {
        when (state) {
            is State.Success -> {
                if (expectedError != null) {
                    fail("Expected error but got success: ${state.data}")
                }
                Assert.assertEquals(expectedData, state.data)
            }

            is State.Failed -> {
                if (expectedData != null) {
                    fail("Expected success but got error: ${state.message}")
                }
                Assert.assertEquals(expectedError, state.message)
            }

            is State.Loading -> {} // Ignore
        }
    }

    @Test
    fun `getProfile should emit success when Firebase returns data`() =
        runTest {
            val testProfile = Profile(name = "Test Result Name", gmail = "testing@gmail.com")

            coEvery { mockDocumentSnapshot.toObject(Profile::class.java) } returns testProfile
            coEvery {
                mockFireStore.collection(any()).document(any()).get().await()
            } returns mockDocumentSnapshot

            val result = repository.getProfile()
            result.collect { assertState(it, expectedData = testProfile) }
        }

    @Test
    fun `getProfile should emit error when Firebase fails`() =
        runTest {
            val errorMessage = "Firebase error"
            coEvery { mockFireStore.collection(any()).document(any()).get().await() } throws
                Exception(
                    errorMessage,
                )
            val result = repository.getProfile()
            result.collect { assertState(it, expectedError = errorMessage) }
        }
}
