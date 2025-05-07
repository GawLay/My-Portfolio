package com.kyrie.myportfolio.splash

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import androidx.lifecycle.lifecycleScope
import com.kyrie.myportfolio.aboutMe.AboutMeActivity
import com.kyrie.myportfolio.base.BaseActivity
import com.kyrie.myportfolio.databinding.ActivitySplashBinding
import com.kyrie.utility.R
import com.kyrie.utility.animation.ALPHA
import com.kyrie.utility.animation.CircularRevealAnim
import com.kyrie.utility.animation.SCALE_X
import com.kyrie.utility.animation.SCALE_Y
import com.kyrie.utility.animation.TRANSLATION_Y
import com.kyrie.utility.animation.createAnim
import com.kyrie.utility.animation.startAnimSet
import com.kyrie.utility.math.GetXY
import com.kyrie.utility.utility.changeStatusBarColor
import com.kyrie.utility.utility.overridePendingTransitionExt
import com.kyrie.utility.utility.startRevealIntent
import kotlinx.coroutines.delay

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    override fun onCreated(savedInstanceState: Bundle?) {
//        hideStatusBar()
        changeStatusBarColor(secondaryStatusColor)
        val cx = GetXY.getCenterValueOfScreen(this).x
        val cy = GetXY.getCenterValueOfScreen(this).y
        lifecycleScope.launchWhenCreated {
            delay(500)
            binding.tvWelcomeMsg.apply {
                val helloTextFadeIn = createAnim(ALPHA, 0f, 1f)
                val translation = createAnim(TRANSLATION_Y, 0f, 90f)
                val scaleX = createAnim(SCALE_X, 0f, 1f)
                val scaleY = createAnim(SCALE_Y, 0f, 1f)
                startAnimSet(
                    helloTextFadeIn,
                    scaleX,
                    scaleY,
                    translation,
                )
            }
        }
        binding.lottieWelcome.addAnimatorListener(
            com.kyrie.utility.simplifyInteractors.AnimatorListenerAdapter(
                onEnd = {
                    revealActivity(cx, cy)
                },
            ),
        )
    }

    private fun revealActivity(
        cx: Int?,
        cy: Int?,
    ) {
        startRevealIntent<AboutMeActivity> {
            putExtra(CircularRevealAnim.EXTRA_CIRCULAR_REVEAL_X, cx)
            putExtra(CircularRevealAnim.EXTRA_CIRCULAR_REVEAL_Y, cy)
        }
        // prevent default transition animation
        overridePendingTransitionExt(
            false,
            R.anim.anim_stay_still,
            R.anim.anim_stay_still,
        )
    }

    override fun setBinding(inflater: LayoutInflater) = ActivitySplashBinding.inflate(inflater)

    override fun handleBackPress() {
        finishAffinity()
    }
}
