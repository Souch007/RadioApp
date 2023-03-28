package com.netcast.radio.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.netcast.radio.BR
import com.netcast.radio.MainViewModel
import com.netcast.radio.R
import com.netcast.radio.base.BaseActivity
import com.netcast.radio.base.ViewModelFactory
import com.netcast.radio.databinding.ActivitySettingsBinding
import com.netcast.radio.request.AppApis
import com.netcast.radio.request.RemoteDataSource
import com.netcast.radio.request.repository.AppRepository
import com.netcast.radio.ui.ui.settings.SettingsFragment

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