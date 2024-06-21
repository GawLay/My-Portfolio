package com.kyrie.data.repository.skill

import com.google.firebase.FirebaseException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kyrie.data.firebaseConstants.FirebaseCollections
import com.kyrie.data.firebaseConstants.FirebaseDefaultException
import com.kyrie.data.models.Skills
import com.kyrie.data.remote.State
import com.kyrie.utility.utility.showLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await


class SkillsRepositoryImpl : SkillsRepository {
    private val skillDocumentRef =
        Firebase.firestore.collection(FirebaseCollections.SKILLS.name.lowercase())
            .document(FirebaseCollections.LIST.name.lowercase())

    override suspend fun getSkillList(): Flow<State<Skills?>> = flow {
        emit(State.loading())
        try {
            val snap = skillDocumentRef.get().await()
            val lists = snap.toObject(Skills::class.java)
            showLog("skills.  lists $lists")
            emit(State.success(lists))
        } catch (e: Exception) {
            showLog("skills. Exception $e")
            emit(
                State.failed(
                    e.message
                        ?: FirebaseDefaultException.DEFAULT_EXCEPTION.message
                )
            )
        } catch (e: FirebaseException) {
            showLog("skills. fException$e")
            emit(
                State.failed(
                    e.message ?: FirebaseDefaultException.FIREBASE_DEFAULT_EXCEPTION.message
                )
            )
        }
    }

}

