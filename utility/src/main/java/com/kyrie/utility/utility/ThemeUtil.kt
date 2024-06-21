package com.kyrie.utility.utility

import androidx.appcompat.app.AppCompatDelegate

object ThemeUtil {
    fun toggleNightMode(nightMode: Boolean = false) {
        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

    }
}