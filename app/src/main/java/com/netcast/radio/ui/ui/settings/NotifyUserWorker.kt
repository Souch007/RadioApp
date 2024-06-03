package com.netcast.radio.ui.ui.settings

import android.content.Context
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.netcast.radio.base.ViewModelProvider
import com.netcast.radio.request.AppApis
import com.netcast.radio.request.AppConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotifyUserWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    private lateinit var apiService: AppApis
    override fun doWork(): Result {
        val retrofit = Retrofit.Builder()
            .baseUrl(AppConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(AppApis::class.java)
        val deviceID = Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID)
        val outTime = getCurrentDateTime()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                apiService.notifyAppKilled(
                    deviceID,
                    detectNetworkCountry(applicationContext) ?: "",
                    "",
                    outTime
                )
            } catch (e: Exception) {
                Log.d("Ecxeption", "doWork: ${e.printStackTrace()}")
            }
        }

        return Result.success()
    }

    private fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("yy-MM-dd HH:mm", Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
    }

    private fun detectNetworkCountry(context: Context): String? {
        return try {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            telephonyManager.networkCountryIso
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
