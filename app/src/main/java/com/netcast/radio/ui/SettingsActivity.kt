package com.netcast.radio.ui

import android.os.Bundle
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
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
//        setupStatusBar()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container)) { view, insets ->
            val systemBarSpacing = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                systemBarSpacing.left,
                systemBarSpacing.top,
                systemBarSpacing.right,
                systemBarSpacing.bottom
            )
            insets
        }
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

    private fun setupStatusBar() {
        val controller = WindowCompat.getInsetsController(
            window,
            window.decorView
        )
        controller.isAppearanceLightStatusBars = true
    }
    override val layoutRes: Int
        get() = R.layout.activity_settings
    override val bindingVariable: Int
        get() = BR.mainViewModel
    override val viewModelClass: Class<MainViewModel>
        get() = MainViewModel::class.java

}