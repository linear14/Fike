package com.dongldh.fike.retrofit

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object FikeRetrofit {
    val gson = GsonBuilder()
        .setLenient()
        .create()

    // For Logging
    private fun okHttpClientBuilder(): OkHttpClient.Builder {
        val okhttpClientBuilder = OkHttpClient.Builder()
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        okhttpClientBuilder.addInterceptor(logging)

        return okhttpClientBuilder
    }

    // http://openapi.seoul.go.kr:8088/54625942766c696e36386c4258656e/json/bikeList/1/20
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://openapi.seoul.go.kr:8088/54625942766c696e36386c4258656e/json/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(okHttpClientBuilder().build())
        .build()

    val getApiService: ApiService = retrofit.create(ApiService::class.java)
}