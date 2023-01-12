package com.netcast.radio.base

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.netcast.radio.PlayingChannelData
import com.netcast.radio.R
import com.netcast.radio.db.AppDatabase
import com.netcast.radio.request.AppApis
import com.netcast.radio.request.AppConstants
import com.netcast.radio.request.RemoteDataSource
import com.netcast.radio.request.repository.AppRepository
import com.netcast.radio.ui.radioplayermanager.episodedata.Data
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.ExecutionException


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
        }

        requestNotificationPermission()

    }

    open fun requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) return
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            NOTIFICATION_PERMISSION_CODE
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
            if(fdelete.exists()) {
                if (AppSingelton.downloadedIds.matches("".toRegex())) {
                    AppSingelton.downloadedIds = data.id
                } else if (!AppSingelton.downloadedIds.contains(data.id + ""))
                    AppSingelton.downloadedIds =
                        AppSingelton.downloadedIds + "," + data.id
            } else {
                deletePodcastById(data.id)
                listOffline.remove(data)
            }
        }
        return listOffline.toList()
    }


    fun getOfflineDataById(id:String): Data {
        if (appDatabase == null) {
            initializeDB(applicationContext)
        }
        var data = appDatabase!!.appDap().getOfflineEpisodeById(id)
        return data
    }

    fun deletePodcastById(id:String) {
        if (appDatabase == null) {
            initializeDB(applicationContext)
        }
        var record = appDatabase!!.appDap().deleteOfflineEpisodeById(id)
        AppSingelton.downloadedIds = "";
        getOfflineData()
    }

    private fun getViewModelFactory(): ViewModelFactory {
        val remoteDataSource = RemoteDataSource()
        return ViewModelFactory(AppRepository(remoteDataSource.buildApi(AppApis::class.java)))
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        if (!isPlaying)
            Toast.makeText(this, "Buffering", Toast.LENGTH_SHORT).show()

        AppSingelton._currentPlayingChannel = AppSingelton._radioSelectedChannel
        AppSingelton._currenPlayingChannelId = AppSingelton._radioSelectedChannelId
        AppSingelton._playingStarted.value = isPlaying
        addToRecentlyPlayedList(AppSingelton._currentPlayingChannel)
        initListener(AppSingelton._currentPlayingChannel.value)

    }

    private fun addToRecentlyPlayedList(_currentPlayingChannel: MutableLiveData<PlayingChannelData>) {
        val gson = Gson();
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
        if (!isAlreadyAdded)
            recentlyPlayedChannelsArray.add(_currentPlayingChannel.value!!)
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
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
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

    private fun initListener(_currentPlayingChannel: PlayingChannelData?) {

        val notificationId = AppConstants.NOTIFICATION_ID
        val mediaDescriptionAdapter = object : PlayerNotificationManager.MediaDescriptionAdapter {
            override fun getCurrentSubText(player: Player): CharSequence? {
                return "netcast"
            }

            override fun getCurrentContentTitle(player: Player): String {
                return _currentPlayingChannel!!.name
            }

            override fun createCurrentContentIntent(player: Player): PendingIntent? {
                return null
            }

            override fun getCurrentContentText(player: Player): String {
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Html.fromHtml(_currentPlayingChannel!!.country, Html.FROM_HTML_MODE_COMPACT)
                        .toString()
                } else {
                    Html.fromHtml(_currentPlayingChannel!!.country).toString()
                }
            }

            override fun getCurrentLargeIcon(
                player: Player,
                callback: PlayerNotificationManager.BitmapCallback
            ): Bitmap? {
                var trackImage: Bitmap? = null
                val thread = Thread {
                    try {
                        val uri = Uri.parse(_currentPlayingChannel!!.favicon)
                        val bitmap = Glide.with(applicationContext)
                            .asBitmap()
                            .load(uri)
                            .submit().get()
                        trackImage = bitmap
                        callback.onBitmap(bitmap)
                    } catch (e: ExecutionException) {
                        e.printStackTrace()
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
                thread.start()
                return trackImage
            }
        }

        playerNotificationManager = PlayerNotificationManager.Builder(
            this,
            notificationId, "My_channel_id"
        )
            .setChannelNameResourceId(R.string.app_name)
            .setChannelDescriptionResourceId(R.string.notification_Channel_Description)
            .setMediaDescriptionAdapter(mediaDescriptionAdapter)
            .setNotificationListener(object : PlayerNotificationManager.NotificationListener {
                override fun onNotificationPosted(
                    notificationId: Int,
                    notification: Notification,
                    ongoing: Boolean
                ) {
                    Log.d("TAG", "onNotificationPosted: ")
                }

                override fun onNotificationCancelled(
                    notificationId: Int,
                    dismissedByUser: Boolean
                ) {
                }
            })
            .build()

        playerNotificationManager!!.setPriority(NotificationCompat.PRIORITY_LOW)
        playerNotificationManager!!.setUsePlayPauseActions(true)
        playerNotificationManager!!.setSmallIcon(R.drawable.logo)
        playerNotificationManager!!.setColorized(true)
        playerNotificationManager!!.setColor(0xFFBDBDBD.toInt())
        playerNotificationManager!!.setPlayer(AppSingelton.exoPlayer)
    }


}


