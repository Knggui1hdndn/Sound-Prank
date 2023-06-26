package com.pranksound.fartsound.trollandjoke.funnyapp

import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataImage
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataSound
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface ApiClient {
    companion object {
        val okHttpClient = OkHttpClient.Builder()
            .callTimeout(4000L,TimeUnit.MILLISECONDS)
            .readTimeout(4000L,TimeUnit.MILLISECONDS)
            .build()
         var url = "http://167.172.146.247:6789/api/category/"
        var apiInterface: ApiClient = Retrofit.Builder().baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(ApiClient::class.java)
    }



    @GET("category/{id}")
    fun getListParentSound(@Path("id") id: String): Call<List<DataImage>>
    @GET
    fun getListChildSound(): List<DataSound>
}