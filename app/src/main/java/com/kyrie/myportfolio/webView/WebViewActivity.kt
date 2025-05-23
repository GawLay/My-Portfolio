package com.kyrie.myportfolio.webView

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import com.kyrie.myportfolio.base.BaseActivity
import com.kyrie.myportfolio.databinding.ActivityWebviewBinding
import com.kyrie.myportfolio.myResume.MyResumeActivity
import com.kyrie.utility.constants.WebViewIntentKey
import com.kyrie.utility.utility.overridePendingTransitionExt
import com.kyrie.utility.utility.startPlayStore
import com.kyrie.utility.R as UtilityR

class WebViewActivity : BaseActivity<ActivityWebviewBinding>() {
    companion object {
        const val MY_FALLBACK_LINKEDIN = "https://www.linkedin.com/in/phyoaungzaw/"
        const val MY_FALLBACK_GITHUB = "https://github.com/GawLay"
    }

    private var url = ""

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreated(savedInstanceState: Bundle?) {
        val type = intent.getStringExtra(WebViewIntentKey.URL_TYPE.key)
        var url = intent.getStringExtra(WebViewIntentKey.URL_KEY.key) ?: ""
        binding.webViewMyProfolio.setBuiltInZoomControl()
        if (url.isEmpty()) {
            when (type) {
                MyResumeActivity.LINKEDIN -> {
                    url = MY_FALLBACK_LINKEDIN
                }

                MyResumeActivity.GIT -> {
                    url = MY_FALLBACK_GITHUB
                }
            }
        }
        binding.webViewMyProfolio.load(url)
        binding.webViewMyProfolio.setOnPlayStoreAppLoaded { playStoreUrl ->

            startPlayStore(playStoreUrl) {
                // no need to handle fallback
            }

            true
        }
    }

    override fun onClickEvents() {
        binding.imgBtnBack.setOnClickListener {
            handleBackPress()
        }
    }

    override fun setBinding(inflater: LayoutInflater) = ActivityWebviewBinding.inflate(inflater)

    override fun handleBackPress() {
        finish()
        overridePendingTransitionExt(
            true,
            UtilityR.anim.anim_stay_still,
            UtilityR.anim.item_animation_slide_from_top,
        )
    }

    override fun onResume() {
        super.onResume()
        binding.webViewMyProfolio.onResume()
    }

    override fun onDestroy() {
        binding.webViewMyProfolio.stopLoading()
        binding.webViewMyProfolio.destroy()
        super.onDestroy()
    }

    override fun onPause() {
        binding.webViewMyProfolio.onPause()
        super.onPause()
    }
}
