package com.kyrie.data.firebaseConstants

enum class FirebaseCollections {
    PROFILE,
    SKILLS,
    SKILLS_DATA,
    SKILLS_DATA_IMAGES,
    IMAGE_LIST,
    EXPERIENCES,
    EXPERIENCE_DETAILS,
    DATA,
    LIST,
    TEMPLATES,
    PDF,
    FAQ,
    FAQ_LIST
}

enum class FirebaseDefaultException(val message: String) {
    FIREBASE_DEFAULT_EXCEPTION("firebase something went wrong"),
    DEFAULT_EXCEPTION("something went wrong"),
    EMPTY_DOCUMENT("empty document")
}