package com.kyrie.myportfolio.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.OnBackPressedCallback
import androidx.viewbinding.ViewBinding
import com.kyrie.utility.R as UtilityR

abstract class BaseActivity<T : ViewBinding> : BaseUtilityAppCompat() {
    lateinit var binding: T
    abstract fun onCreated(savedInstanceState: Bundle?)
    abstract fun setBinding(inflater: LayoutInflater): T
    abstract fun handleBackPress()
    private val resId by lazy {
        UtilityR.anim.layout_animation_slide_from_bottom
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature()
        super.onCreate(savedInstanceState)
        binding = this.setBinding(layoutInflater)
        setContentView(binding.root)
        onCreated(savedInstanceState)
        onClickEvents()
        onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            handleBackPress()
        }
    }

    open fun onBeforeSetContentView(){}
    open fun requestWindowFeature() {}
    open fun onClickEvents() {}

}