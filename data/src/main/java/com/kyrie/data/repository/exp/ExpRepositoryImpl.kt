package com.kyrie.data.repository.exp

import com.google.firebase.FirebaseException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.kyrie.data.firebaseConstants.FirebaseCollections
import com.kyrie.data.firebaseConstants.FirebaseDefaultException
import com.kyrie.data.models.ExperienceDetailMap
import com.kyrie.data.models.Experiences
import com.kyrie.data.remote.State
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class ExpRepositoryImpl : ExpRepository {
    private val firestore = Firebase.firestore
    private val expDocumentRef =
        firestore.collection(FirebaseCollections.EXPERIENCES.name.lowercase())
            .document(FirebaseCollections.LIST.name.lowercase())
    private val expDetailCollection =
        firestore.collection(FirebaseCollections.EXPERIENCE_DETAILS.name.lowercase())

    override suspend fun getExpList(): Flow<State<Experiences?>> = flow {
        emit(State.loading())
        try {
            val snap = expDocumentRef.get().await()
            val lists = snap.toObject(Experiences::class.java)
            emit(State.success(lists))
        } catch (e: Exception) {
            emit(
                State.failed(
                    e.message
                        ?: FirebaseDefaultException.DEFAULT_EXCEPTION.message
                )
            )
        } catch (e: FirebaseException) {
            emit(
                State.failed(
                    e.message ?: FirebaseDefaultException.FIREBASE_DEFAULT_EXCEPTION.message
                )
            )
        }
    }

    override suspend fun getExpDetail(documentId: String): Flow<State<ExperienceDetailMap?>> =
        flow {
            emit(State.loading())
            try {
                val expDetailDocumentRef = expDetailCollection.document(documentId)
                val snap = expDetailDocumentRef.get().await()
                val data = snap.toObject<ExperienceDetailMap>()
                expDetailDocumentRef.get().addOnCompleteListener {
                    val documentSnapshot = it.result
                }
                emit(State.success(data))
            } catch (e: Exception) {
                emit(
                    State.failed(
                        e.message
                            ?: FirebaseDefaultException.DEFAULT_EXCEPTION.message
                    )
                )
            } catch (e: FirebaseException) {
                emit(
                    State.failed(
                        e.message ?: FirebaseDefaultException.FIREBASE_DEFAULT_EXCEPTION.message
                    )
                )
            }
        }
}