package com.netcast.baidutv.ui

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.netcast.baidutv.BR
import com.netcast.baidutv.MainViewModel
import com.netcast.baidutv.R
import com.netcast.baidutv.base.BaseActivity
import com.netcast.baidutv.base.ViewModelFactory
import com.netcast.baidutv.databinding.ActivitySettingsBinding
import com.netcast.baidutv.request.AppApis
import com.netcast.baidutv.request.RemoteDataSource
import com.netcast.baidutv.request.repository.AppRepository
import com.netcast.baidutv.ui.ui.settings.SettingsFragment

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