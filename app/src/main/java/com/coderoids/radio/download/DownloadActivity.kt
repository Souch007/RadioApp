package com.coderoids.radio.download

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.coderoids.radio.BR
import com.coderoids.radio.R
import com.coderoids.radio.base.AppSingelton
import com.coderoids.radio.base.BaseActivity
import com.coderoids.radio.databinding.ActivityDownloadBinding
import com.coderoids.radio.request.AppConstants

class DownloadActivity : BaseActivity<DownloadViewModel,ActivityDownloadBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppSingelton.currentActivity = AppConstants.DownloadActivity

    }


    override val layoutRes: Int
        get() = R.layout.activity_download
    override val bindingVariable: Int
        get() = BR.downloadVM
    override val viewModelClass: Class<DownloadViewModel>
        get() = DownloadViewModel::class.java
}