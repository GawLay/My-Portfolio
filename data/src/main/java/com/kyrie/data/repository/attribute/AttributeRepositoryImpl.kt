package com.kyrie.data.repository.attribute

import com.google.firebase.FirebaseException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.kyrie.data.firebaseConstants.FirebaseCollections
import com.kyrie.data.firebaseConstants.FirebaseDefaultException
import com.kyrie.data.models.AttributeInfo
import com.kyrie.data.remote.State
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class AttributeRepositoryImpl : AttributeRepository {
    private val attributeDocumentRef =
        Firebase.firestore.collection(FirebaseCollections.ATTRIBUTES.name.lowercase())
            .document(FirebaseCollections.ATTRIBUTE.name.lowercase())

    override suspend fun getAttributeList(): Flow<State<AttributeInfo?>> =
        flow {
            emit(State.loading())
            try {
                val snapshot = attributeDocumentRef.get().await()
                val attributeInfo = snapshot.toObject<AttributeInfo>()
                emit(State.success(attributeInfo))
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
