package com.baidu.netcast.ui.ui.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import com.baidu.netcast.databinding.ActivityWebviewBinding

class WebviewActivity : AppCompatActivity() {
    private lateinit var binding:ActivityWebviewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.webview.settings.javaScriptEnabled = true

        binding.webview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url != null) {
                    view?.loadUrl(url)
                }
                return true
            }
        }
        binding.webview.loadUrl(intent.getStringExtra("url")?:"")
    }
}