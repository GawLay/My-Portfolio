package com.kyrie.myportfolio.setting

import android.os.Bundle
import android.view.LayoutInflater
import androidx.lifecycle.lifecycleScope
import com.kyrie.data.storage.local.IPrefSource
import com.kyrie.myportfolio.base.BaseRevealActivity
import com.kyrie.myportfolio.databinding.ActivitySettingBinding
import com.kyrie.myportfolio.setting.attribute.AttributeActivity
import com.kyrie.myportfolio.setting.faq.FaqActivity
import com.kyrie.utility.constants.ThemeModeKey
import com.kyrie.utility.utility.ThemeUtil
import com.kyrie.utility.utility.startIntent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import com.kyrie.utility.R as UtilityR

class SettingActivity : BaseRevealActivity<ActivitySettingBinding>() {
    private val prefHelper: IPrefSource by inject()

    override fun onCreated(savedInstanceState: Bundle?) {
//        setThemeMode()
        setToolbar()
    }

    /**
     * will not implemented yet
     * */
    private fun setThemeMode() {
        prefHelper.getBoolean(ThemeModeKey.NIGHT_MODE.key).let { isNightMode ->
            if (isNightMode) {
                binding.switchDarkMode.isChecked = true
            } else {
                //user didn't select night mode
                //proceeding to check system theme mode
                binding.switchDarkMode.isChecked = ThemeUtil.isNightMode(this)
            }
        }
    }

    private fun setToolbar() {
        changeStatusColorFromSecondaryToDefault(750L)
        binding.includeToolbar.toolbar.apply {
            title = getString(UtilityR.string.title_setting)
            setNavigationOnClickListener {
                handleBackPress()
            }
        }

    }

    override fun onClickEvents() {
        binding.llAttribute.setOnClickListener {
            startIntent<AttributeActivity>()
//            if (Build.VERSION.SDK_INT > 33) {
//                overrideActivityTransition(
//                    UtilityR.anim.item_animation_slide_from_bottom,
//                    UtilityR.anim.anim_stay_still
//                )
//            } else {
            overridePendingTransition(
                com.kyrie.utility.R.anim.item_animation_slide_from_bottom,
                com.kyrie.utility.R.anim.anim_stay_still
            )
//            }
        }

        binding.llFaq.setOnClickListener {
            startIntent<FaqActivity>()
            overridePendingTransition(
                com.kyrie.utility.R.anim.item_animation_slide_from_bottom,
                com.kyrie.utility.R.anim.anim_stay_still
            )
//            }
        }

        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            prefHelper.saveBoolean(ThemeModeKey.NIGHT_MODE.key, isChecked)
            ThemeUtil.toggleNightMode(this, isChecked)
        }
    }

    override fun setBinding(inflater: LayoutInflater) = ActivitySettingBinding.inflate(inflater)

    override fun handleBackPress() {
        lifecycleScope.launch {
            delay(200)
            changeStatusColorFromDefaultToSecondary(300L)
            revealAnimation?.unRevealActivity()
        }
    }
}