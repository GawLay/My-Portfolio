package com.kyrie.utility.utility

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

object ThemeUtil {
    fun toggleNightMode(context: Context, nightMode: Boolean = false) {
        val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        if (nightMode) {
            uiModeManager.nightMode = UiModeManager.MODE_NIGHT_YES
        } else {
            uiModeManager.nightMode = UiModeManager.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(
            if (nightMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    fun isNightMode(context: Context): Boolean {
        val config = context.resources.configuration
        val nightModeFlags = config.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES
    }

}