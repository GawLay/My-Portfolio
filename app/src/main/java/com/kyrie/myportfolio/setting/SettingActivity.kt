package com.kyrie.myportfolio.setting

import android.os.Bundle
import android.view.LayoutInflater
import androidx.lifecycle.lifecycleScope
import com.kyrie.myportfolio.base.BaseRevealActivity
import com.kyrie.myportfolio.databinding.ActivitySettingBinding
import com.kyrie.myportfolio.setting.attribute.AttributeActivity
import com.kyrie.myportfolio.setting.faq.FaqActivity
import com.kyrie.utility.utility.ThemeUtil
import com.kyrie.utility.utility.startIntent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.kyrie.utility.R as UtilityR

class SettingActivity : BaseRevealActivity<ActivitySettingBinding>() {
    override fun onCreated(savedInstanceState: Bundle?) {
//        hideStatusBar()
        setToolbar()
    }

    private fun setToolbar() {
        changeStatusColorFromSecondaryToWhite(750L)
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
            ThemeUtil.toggleNightMode(isChecked)
        }
    }

    override fun setBinding(inflater: LayoutInflater) = ActivitySettingBinding.inflate(inflater)

    override fun handleBackPress() {
        lifecycleScope.launch {
            delay(200)
            changeStatusColorFromWhiteToSecondary(300L)
            revealAnimation?.unRevealActivity()
        }
    }
}