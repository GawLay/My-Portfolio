package com.kyrie.data.storage.local

import android.content.Context
import android.content.SharedPreferences

class PrefSourceImpl(context: Context) : IPrefSource {
    @Suppress("PrivatePropertyName")
    private val PREF_KEY = ":appPrefKey"
    private val mPref: SharedPreferences = context.getSharedPreferences(
        PREF_KEY,
        Context.MODE_PRIVATE
    )

    override fun saveBooleanWithCommit(key: String, value: Boolean) {
        mPref.edit().putBoolean(key, value).apply()
    }

    override fun saveBoolean(key: String, value: Boolean) {
        mPref.edit().putBoolean(key, value).apply()
    }

    override fun saveInt(key: String, value: Int) {
        mPref.edit().putInt(key, value).apply()
    }

    override fun saveString(key: String, value: String) {
        mPref.edit().putString(key, value).apply()
    }


    override fun getBoolean(key: String): Boolean {
        return mPref.getBoolean(key, false)
    }

    override fun getLoginStatus(key: String): Boolean {
        return mPref.getBoolean(key, false)
    }


    override fun getString(key: String, defValue: String): String? {
        return mPref.getString(key, "")
    }

    override fun getInt(key: String): Int {
        return mPref.getInt(key, 404)
    }

    override fun clearPref(key: String) {
        mPref.edit().remove(key).apply()
    }

    override fun saveLong(key: String, value: Long) {
        mPref.edit().putLong(key, value).apply()
    }

    override fun getLong(key: String): Long {
        return mPref.getLong(key, 0L)
    }

//    fun getAppServiceStatus(context: Context?): AppInfoResponse.AppInfo? {
//        val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
//        val gson = Gson()
//        val json = sp.getString(com.st.stapp.utils.PrefUtils.APP_SERVICE_STATUS, "")
//        return gson.fromJson(json, AppInfoResponse.AppInfo::class.java)
//    }


}