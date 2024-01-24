package com.baidu.netcast.base

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.baidu.netcast.db.AppDatabase


open class BaseFragment < T: ViewDataBinding>(@LayoutRes private val layoutResourceId : Int) : Fragment() {
    private var _binding: T? = null
    val binding: T get() = _binding!!
    open fun T.initialize() {}
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutResourceId, container!!, false)
        _binding?.lifecycleOwner = viewLifecycleOwner
        binding.initialize()
        return _binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun isWifiConnected(context: Context):Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
            capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)==true
        } else {
            val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
            activeNetwork?.typeName?.contains("wifi",ignoreCase = true)?:false
        }
    }

    fun initializeDB(context: Context): AppDatabase {
        return AppDatabase.getDatabaseClient(context)
    }

}