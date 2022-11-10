package com.coderoids.radio.base

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.PersistableBundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.coderoids.radio.db.AppDatabase
import com.coderoids.radio.request.AppApis
import com.coderoids.radio.request.RemoteDataSource
import com.coderoids.radio.request.repository.AppRepository
import com.coderoids.radio.ui.radio.RadioViewModel
import com.coderoids.radio.ui.radioplayermanager.episodedata.Data
import com.google.android.exoplayer2.Player
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

abstract class BaseActivity<VM: BaseViewModel, VDB:ViewDataBinding> : AppCompatActivity() , Player.Listener {
    protected lateinit var viewModel:VM
    protected lateinit var dataBinding:VDB

    @get:LayoutRes
    abstract val layoutRes:Int
    abstract val bindingVariable: Int
    abstract val viewModelClass: Class<VM>
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var _baseViewModel: BaseViewModel
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPredEditor: SharedPreferences.Editor
    var appDatabase : AppDatabase? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this,layoutRes)
        dataBinding.lifecycleOwner = this
        appDatabase = initializeDB(this)
        viewModelFactory = getViewModelFactory()
        viewModel = ViewModelProvider(this@BaseActivity,viewModelFactory).get(viewModelClass)

        dataBinding.setVariable(bindingVariable, viewModel)
        dataBinding.executePendingBindings()

        sharedPreferences = getSharedPreferences("appData", Context.MODE_PRIVATE)
        sharedPredEditor = sharedPreferences.edit()

        AppSingelton._isFavUpdated.observe(this) {
            val gson = Gson();
            val json = gson.toJson(AppSingelton.favouritesRadioArray)
            sharedPredEditor.putString("FavChannels", json).apply()
        }
    }

    fun initializeDB(context: Context) : AppDatabase {
        return AppDatabase.getDatabaseClient(context)
    }

    fun insertOfflineData(data : Data){
        CoroutineScope(IO).launch {
            appDatabase!!.appDap().insertOfflineEpisode(data);
        }
    }

    fun getOfflineData():  List<Data> {
        if(appDatabase==null){
            initializeDB(applicationContext)
        }
        return appDatabase!!.appDap().getOfflineEpisodes()
    }

    private fun getViewModelFactory(): ViewModelFactory {
        val remoteDataSource = RemoteDataSource()
        return ViewModelFactory(AppRepository(remoteDataSource.buildApi(AppApis::class.java)))
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        AppSingelton._currentPlayingChannel = AppSingelton._radioSelectedChannel
        AppSingelton._currenPlayingChannelId = AppSingelton._radioSelectedChannelId
        AppSingelton._playingStarted.value = isPlaying
    }
}

