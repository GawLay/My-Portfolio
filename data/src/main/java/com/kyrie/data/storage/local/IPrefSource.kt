package com.kyrie.data.storage.local

interface IPrefSource {
    fun saveInt(key: String, value: Int)
    fun saveString(key: String, value: String)
    fun getString(key: String, defValue: String = ""): String?
    fun getInt(key: String): Int
    fun clearPref(key: String)
    fun saveBoolean(key: String, value: Boolean)
    fun saveBooleanWithCommit(key: String, value: Boolean)
    fun saveLong(key: String, value: Long)
    fun getLong(key: String): Long
    fun getBoolean(key: String): Boolean
    fun getLoginStatus(key: String): Boolean
}