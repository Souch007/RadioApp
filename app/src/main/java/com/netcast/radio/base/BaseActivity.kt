package com.netcast.radio.base

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.*
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.netcast.radio.PlayingChannelData
import com.netcast.radio.db.AppDatabase
import com.netcast.radio.request.AppApis
import com.netcast.radio.request.AppConstants
import com.netcast.radio.request.RemoteDataSource
import com.netcast.radio.request.repository.AppRepository
import com.netcast.radio.ui.podcast.CompletedEpisodes
import com.netcast.radio.ui.radioplayermanager.episodedata.Data
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.io.File
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit


abstract class BaseActivity<VM : BaseViewModel, VDB : ViewDataBinding> : AppCompatActivity(),
    Player.Listener {
    protected lateinit var viewModel: VM
    protected lateinit var dataBinding: VDB

    @get:LayoutRes
    abstract val layoutRes: Int
    abstract val bindingVariable: Int
    abstract val viewModelClass: Class<VM>
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var _baseViewModel: BaseViewModel
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPredEditor: SharedPreferences.Editor
    var appDatabase: AppDatabase? = null
    val NOTIFICATION_PERMISSION_CODE = 100123
    var playerNotificationManager: PlayerNotificationManager? = null

    //    private var playbackDisposable: Disposable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, layoutRes)
        dataBinding.lifecycleOwner = this
        appDatabase = initializeDB(this)
        viewModelFactory = getViewModelFactory()
        viewModel = ViewModelProvider(this@BaseActivity, viewModelFactory).get(viewModelClass)

        dataBinding.setVariable(bindingVariable, viewModel)
        dataBinding.executePendingBindings()

        sharedPreferences = getSharedPreferences("appData", Context.MODE_PRIVATE)
        sharedPredEditor = sharedPreferences.edit()

        AppSingelton._isFavUpdated.observe(this) {
            val gson = Gson()
            val json = gson.toJson(AppSingelton.favouritesRadioArray)
            sharedPredEditor.putString("FavChannels", json).apply()
//            AppSingelton._isFavDeleteUpdated.value=true
        }

        requestNotificationPermission()
        AppSingelton._SleepTimerEnd.observe(this) {
            if (it && AppSingelton.exoPlayer?.isPlaying == true) {
                AppSingelton.exoPlayer?.stop()
            }

        }
    }


    open fun requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) return
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), NOTIFICATION_PERMISSION_CODE
        )
    }

    fun initializeDB(context: Context): AppDatabase {
        return AppDatabase.getDatabaseClient(context)
    }

    fun insertOfflineData(data: Data) {
        CoroutineScope(IO).launch {
            appDatabase!!.appDap().insertOfflineEpisode(data);
        }
    }

    fun getOfflineData(): List<Data> {
        if (appDatabase == null) {
            initializeDB(applicationContext)
        }
        val listOfflineTemp = appDatabase!!.appDap().getOfflineEpisodes()
        val listOffline: ArrayList<Data> = ArrayList(listOfflineTemp.size)
        listOffline.addAll(listOfflineTemp)
        listOffline.forEachIndexed { index, data ->
            var data = data;
            val fdelete: File = File(data.fileURI)
            if (fdelete.exists()) {
                if (AppSingelton.downloadedIds.matches("".toRegex())) {
                    AppSingelton.downloadedIds = data.id
                } else if (!AppSingelton.downloadedIds.contains(data.id + "")) AppSingelton.downloadedIds =
                    AppSingelton.downloadedIds + "," + data.id
            } else {
                deletePodcastById(data.id)
                listOffline.remove(data)
            }
        }
        return listOffline.toList()
    }

    fun deleteCompletedEpisodes() {
        try {

            if (appDatabase == null) {
                initializeDB(applicationContext)
            }
//        val listOfflineTemp = appDatabase!!.appDap().getOfflineEpisodes()
            val time = TimeUnit.DAYS.toMillis(2)
            val list = getList<CompletedEpisodes>("completed_episodes")
//            if (!list.isNullOrEmpty()) {
            if (list != null) {
                for (i in 0 until list.size) {
                    val data = fromJson<CompletedEpisodes>(list[i].toString())
                    var isDayPassed =
                        (System.currentTimeMillis() - data.date) >= TimeUnit.DAYS.toMillis(2)
                    if (isDayPassed) {
                        CoroutineScope(Dispatchers.IO).launch {
                            val episodedata =
                                appDatabase!!.appDap().getOfflineEpisodeById(data.episode_id)
                            if (episodedata != null) {
                                appDatabase!!.appDap().deleteOfflineEpisodeById(episodedata.id)
                            }

                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    inline fun <reified T> fromJson(json: String?): T {
        return Gson().fromJson<T>(json, object : TypeToken<T>() {}.type)
    }

    fun getOfflineDataById(id: String): Data {
        if (appDatabase == null) {
            initializeDB(applicationContext)
        }
        var data = appDatabase!!.appDap().getOfflineEpisodeById(id)
        return data
    }

    fun deletePodcastById(id: String) {
        if (appDatabase == null) {
            initializeDB(applicationContext)
        }
        var record = appDatabase!!.appDap().deleteOfflineEpisodeById(id)
        AppSingelton.downloadedIds = ""
        getOfflineData()
    }

    private fun getViewModelFactory(): ViewModelFactory {
        val remoteDataSource = RemoteDataSource()
        return ViewModelFactory(AppRepository(remoteDataSource.buildApi(AppApis::class.java)))
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        try {
            AppSingelton._currentPlayingChannel = AppSingelton._radioSelectedChannel
            AppSingelton._currenPlayingChannelId = AppSingelton._radioSelectedChannelId
            AppSingelton._playingStarted.value = isPlaying
            addToRecentlyPlayedList(AppSingelton._currentPlayingChannel)
            val serviceIntent = Intent(this, AudioPlayerService::class.java)
            if (isPlaying) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    AudioPlayerService.startService(this)
                } else {
                    startService(Intent(serviceIntent))
                }
            }
//        initListener(AppSingelton._currentPlayingChannel.value)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        /*    playbackDisposable= playbackProgressObservable
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribe {
                     Log.i("TAG", "onIsPlayingChanged: $it")
                 }
     */
    }

    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
        Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        super.onPlayerStateChanged(playWhenReady, playbackState)
    }

    override fun onEvents(player: Player, events: Player.Events) {
        super.onEvents(player, events)

    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        try {
            if (playbackState == Player.STATE_ENDED) {
                val isAutoPlayEnable =
                    sharedPreferences.getBoolean(AppConstants.AUTO_PLAY_EPISODES, false)

                val mediaType = AppSingelton.radioSelectedChannel.value?.type
                if (mediaType?.matches("PODCAST".toRegex()) == true || mediaType?.matches("Episodes".toRegex()) == true || mediaType?.matches(
                        "Offline".toRegex()
                    ) == true
                ) {


                    val list = getList<CompletedEpisodes>("completed_episodes")?.toMutableList()
                    val completedEpisodes = CompletedEpisodes(
                        System.currentTimeMillis(),
                        AppSingelton._radioSelectedChannel.value?.id ?: ""
                    )
                    list?.add(completedEpisodes)
                    setList("completed_episodes", list)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addToRecentlyPlayedList(_currentPlayingChannel: MutableLiveData<PlayingChannelData>) {
        val gson = Gson()
        var recentlyPlayedChannelsArray = ArrayList<PlayingChannelData>()

        val json = sharedPreferences.getString("RecentlyPlayed", null)
        if (json != null) {
            val type = object : TypeToken<ArrayList<PlayingChannelData?>?>() {}.getType()
            recentlyPlayedChannelsArray = gson.fromJson(json, type)
        }
        var isAlreadyAdded = false
        for (i in recentlyPlayedChannelsArray) {
            if (i.id.matches(_currentPlayingChannel.value!!.id.toRegex())) {
                isAlreadyAdded = true
                break;
            }
        }
        if (!isAlreadyAdded) recentlyPlayedChannelsArray.add(_currentPlayingChannel.value!!)
        val gson2 = Gson();
        val dataArray = gson2.toJson(recentlyPlayedChannelsArray)
        sharedPredEditor.putString("RecentlyPlayed", dataArray).apply()
        manageRecentlyPlayed()
    }

    fun manageRecentlyPlayed() {
        val gson = Gson()
        val json = sharedPreferences.getString("RecentlyPlayed", null)
        if (json != null) {
            val type = object : TypeToken<ArrayList<PlayingChannelData?>?>() {}.getType()
            AppSingelton.recentlyPlayedArray = gson.fromJson(json, type)
            AppSingelton.isNewItemAdded.value = true
            if (AppSingelton.recentlyPlayedArray.size == 0) {
                val gson = Gson()
                val json = sharedPreferences.getString("RecentlyPlayed", null)
                if (json != null) {
                    val type = object : TypeToken<ArrayList<PlayingChannelData?>?>() {}.getType()
                    AppSingelton.recentlyPlayedArray = gson.fromJson(json, type)
                    AppSingelton.isNewItemAdded.value = true
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //granted
            } else {
                //denied
            }
        }
    }

    open fun checkServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    fun isWifiConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
            capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
        } else {
            val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
            activeNetwork?.typeName?.contains("wifi", ignoreCase = true) ?: false
        }
    }


    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {
        super.onPause()
    }


    fun storeObjectInSharedPref(dataObject: Any, prefName: String): Boolean {
        val dataObjectInJson = Gson().toJson(dataObject)
        sharedPredEditor.putString(prefName, dataObjectInJson)
        return sharedPredEditor.commit()
    }

    open fun <T> setList(key: String?, list: List<T>?) {
        val gson = Gson()
        val json = gson.toJson(list)
        set(key, json)
    }

    open operator fun set(key: String?, value: String?) {
        sharedPredEditor.putString(key, value)
        sharedPredEditor.commit()
    }

    open fun <T> getList(key: String?): List<T?>? {
        val arrayItems: List<T>
        val serializedObject = sharedPreferences.getString(key, null)
        if (serializedObject != null) {
            val gson = Gson()
            val type: Type = object : TypeToken<List<T?>?>() {}.type
            arrayItems = gson.fromJson<List<T>>(serializedObject, type)
            return arrayItems
        }
        return mutableListOf()
    }

}


