package com.accountcreator

import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.logging.HttpLoggingInterceptor

private var client: OkHttpClient? = null


fun getOkHttpClient(): OkHttpClient {
    if (client == null) {
        //val logging = HttpLoggingInterceptor({ println(it) })
        //logging.level = HttpLoggingInterceptor.Level.BODY
        client = OkHttpClient()
        //client!!.interceptors().add(logging)
    }
    return client!!
}