package com.netcast.radio.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

open class BaseFragment < T: ViewDataBinding>(@LayoutRes private val layoutResourceId : Int) : Fragment(){
    private var _binding: T? = null
    val binding : T get() = _binding!!
    open fun T.initialize(){}
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutResourceId,container!!,false)
        _binding?.lifecycleOwner = viewLifecycleOwner
        binding.initialize()
        return _binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}