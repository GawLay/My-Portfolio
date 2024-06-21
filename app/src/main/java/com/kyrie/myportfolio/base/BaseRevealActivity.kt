package com.kyrie.myportfolio.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.OnBackPressedCallback
import androidx.viewbinding.ViewBinding
import com.kyrie.utility.animation.CircularRevealAnim

abstract class BaseRevealActivity<T : ViewBinding> : BaseUtilityAppCompat() {
    lateinit var binding: T
    var revealAnimation: CircularRevealAnim? = null
    abstract fun onCreated(savedInstanceState: Bundle?)
    abstract fun setBinding(inflater: LayoutInflater): T
    abstract fun handleBackPress()

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature()
        super.onCreate(savedInstanceState)
        binding = this.setBinding(layoutInflater)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this, backPressedCallback)
        onCreated(savedInstanceState)
        setupRevealAnim()
        onClickEvents()
    }

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            handleBackPress()
        }
    }

    open fun requestWindowFeature() {}
    open fun onClickEvents() {}

    /**
     * initialized once and for all for all of circular reveal destination activities
     * For Eg:
     * @see [com.kyrie.myportfolio.aboutMe.AboutMeActivity]
     *  @see [com.kyrie.myportfolio.experience.ExperienceActivity]
     * **/
    private fun setupRevealAnim() {
        val intent = intent
        //start Reveal
        revealAnimation = CircularRevealAnim(binding.root, intent, this)
    }

}