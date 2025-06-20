package com.netcast.radio.base

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val appName = context.getString(context.applicationInfo.labelRes)
        val versionName = context.packageManager.getPackageInfo(context.packageName, 0).versionName

        // Build the request with headers
        val request = chain.request().newBuilder()
            .addHeader("name", appName)
            .addHeader("version", versionName!!)
            // Add more headers as needed
            .build()

        return chain.proceed(request)
    }
}
