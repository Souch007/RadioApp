package com.baidu.netcast.ui.ui.settings

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import com.baidu.netcast.R
import com.baidu.netcast.databinding.ActivityFaqsBinding

class FAQsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFaqsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityFaqsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val type=intent.getStringExtra("type")
        binding.header.tvTitle.text=type
        binding.header.imgBack.setOnClickListener {
            finish()
        }
        if (type.equals("FAQS",true)) {
            binding.tvHeading.text = "FAQS"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                binding.tvDesc.text= Html.fromHtml(getString(R.string.faqs),Html.FROM_HTML_MODE_LEGACY)
            }
            else{
            binding.tvDesc.text = Html.fromHtml(getString(R.string.faqs))

            }
        }
        else{
            binding.tvHeading.text="LEGAL NOTICE"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                binding.tvDesc.text= getString(R.string.imprint)
                binding.tvDesc.text= Html.fromHtml(getString(R.string.imprint),Html.FROM_HTML_MODE_LEGACY)
            }
            else{
                binding.tvDesc.text = Html.fromHtml(getString(R.string.imprint))

            }
        }
    }
}