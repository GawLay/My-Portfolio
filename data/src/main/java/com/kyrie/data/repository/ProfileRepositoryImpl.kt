package com.kyrie.data.repository

import com.google.firebase.FirebaseException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.kyrie.data.firebaseConstants.FirebaseCollections
import com.kyrie.data.firebaseConstants.FirebaseDefaultException
import com.kyrie.data.models.Profile
import com.kyrie.data.remote.State
import com.kyrie.utility.utility.showLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class ProfileRepositoryImpl : ProfileRepository {
    private val profileDataDocumentRef =
        Firebase.firestore.collection(FirebaseCollections.PROFILE.name.lowercase())
            .document(FirebaseCollections.DATA.name.lowercase())

    override suspend fun getProfile(): Flow<State<Profile?>> =
        flow {
            emit(State.loading())
            try {
                val snapshot = profileDataDocumentRef.get().await()
                val profile = snapshot.toObject<Profile>()
                emit(State.success(profile))
            } catch (e: Exception) {
                emit(
                    State.failed(
                        e.message
                            ?: FirebaseDefaultException.DEFAULT_EXCEPTION.message,
                    ),
                )
            } catch (e: FirebaseException) {
                showLog("data profile fException$e")
                emit(
                    State.failed(
                        e.message ?: FirebaseDefaultException.FIREBASE_DEFAULT_EXCEPTION.message,
                    ),
                )
            }
        }
}
