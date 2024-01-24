package com.baidu.netcast.ui

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.baidu.netcast.BR
import com.baidu.netcast.MainViewModel
import com.baidu.netcast.R
import com.baidu.netcast.base.BaseActivity
import com.baidu.netcast.base.ViewModelFactory
import com.baidu.netcast.databinding.ActivitySettingsBinding
import com.baidu.netcast.request.AppApis
import com.baidu.netcast.request.RemoteDataSource
import com.baidu.netcast.request.repository.AppRepository
import com.baidu.netcast.ui.ui.settings.SettingsFragment

class SettingsActivity : BaseActivity<MainViewModel, ActivitySettingsBinding>() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var mainViewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Settings)
        val factory = getViewModelFactory()
        mainViewModel = ViewModelProvider(this@SettingsActivity, factory).get(MainViewModel::class.java)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_settings)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.settings_container, SettingsFragment.newInstance())
                .commitNow()
        }
        binding.ivBackSettingsAv.setOnClickListener {
            finish()
        }
        mainViewModel.radiotimer.observe(this){
           binding.btnAlaram?.text=it
        }
    }
    private fun getViewModelFactory(): ViewModelFactory {
        val remoteDataSource = RemoteDataSource()
        return ViewModelFactory(AppRepository(remoteDataSource.buildApi(AppApis::class.java)))
    }

    override val layoutRes: Int
        get() = R.layout.activity_settings
    override val bindingVariable: Int
        get() = BR.mainViewModel
    override val viewModelClass: Class<MainViewModel>
        get() = MainViewModel::class.java

}