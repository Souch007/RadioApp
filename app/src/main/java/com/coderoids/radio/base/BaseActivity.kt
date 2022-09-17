package com.coderoids.radio.base

import android.os.Bundle
import android.os.PersistableBundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.coderoids.radio.request.AppApis
import com.coderoids.radio.request.RemoteDataSource
import com.coderoids.radio.request.repository.AppRepository
import com.coderoids.radio.ui.radio.RadioViewModel

abstract class BaseActivity<VM: BaseViewModel, VDB:ViewDataBinding> : AppCompatActivity(){
    protected lateinit var viewModel:VM
    protected lateinit var dataBinding:VDB


    @get:LayoutRes
    abstract val layoutRes:Int
    abstract val bindingVariable: Int
    abstract val viewModelClass: Class<VM>
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        dataBinding = DataBindingUtil.setContentView(this,layoutRes)
        dataBinding.lifecycleOwner = this
        viewModelFactory = getViewModelFactory()
        viewModel = ViewModelProvider(this@BaseActivity,viewModelFactory).get(viewModelClass)
        dataBinding.setVariable(bindingVariable, viewModel)
        dataBinding.executePendingBindings()
    }

    private fun getViewModelFactory(): ViewModelFactory {
        val remoteDataSource = RemoteDataSource()
        return ViewModelFactory(AppRepository(remoteDataSource.buildApi(AppApis::class.java)))
    }

}

