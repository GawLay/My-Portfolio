package com.kyrie.myportfolio.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.kyrie.utility.R

abstract class BaseFragment<T : ViewBinding>  : Fragment() {


    private val resId by lazy {
        R.anim.layout_animation_slide_from_bottom
    }
    private val resFallDownId by lazy {
        R.anim.layout_animation_slide_from_top
    }
    protected val recyclerViewAnimation: LayoutAnimationController by lazy {
        AnimationUtils.loadLayoutAnimation(requireContext(), resId)
    }

    protected val recyclerViewExitAnimation: LayoutAnimationController by lazy {
        AnimationUtils.loadLayoutAnimation(requireContext(), resFallDownId)
    }

    private var _binding: T? = null
    protected val binding get() = _binding!!
    protected abstract fun setBinding(inflater: LayoutInflater,container: ViewGroup?): T
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = setBinding(inflater,container)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        onClickEvents()
        observe()
    }
    open fun initViews(){}
    open fun onClickEvents(){}
    open fun observe(){}
}