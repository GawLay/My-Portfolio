package com.kyrie.data.repository.faq

import com.google.firebase.FirebaseException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.kyrie.data.firebaseConstants.FirebaseCollections
import com.kyrie.data.firebaseConstants.FirebaseDefaultException
import com.kyrie.data.models.FaqInfo
import com.kyrie.data.remote.State
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class FaqRepositoryImpl : FaqRepository {
    private val faqDocumentRef =
        Firebase.firestore.collection(FirebaseCollections.FAQ.name.lowercase())
            .document(FirebaseCollections.FAQ_LIST.name.lowercase())

    override suspend fun getFaqList(): Flow<State<FaqInfo?>> =
        flow {
            emit(State.loading())
            try {
                val snapshot = faqDocumentRef.get().await()
                val faqInfo = snapshot.toObject<FaqInfo>()
                emit(State.success(faqInfo))
            } catch (e: Exception) {
                emit(
                    State.failed(
                        e.message
                            ?: FirebaseDefaultException.DEFAULT_EXCEPTION.message,
                    ),
                )
            } catch (e: FirebaseException) {
                emit(
                    State.failed(
                        e.message ?: FirebaseDefaultException.FIREBASE_DEFAULT_EXCEPTION.message,
                    ),
                )
            }
        }
}
