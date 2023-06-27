package com.pranksound.fartsound.trollandjoke.funnyapp

import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataImage
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataImages
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataSound
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataSounds
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
            .callTimeout(4000L,TimeUnit.MILLISECONDS)//đặt thời gian tối đa kết nối với sv
            .readTimeout(4000L,TimeUnit.MILLISECONDS)// đặt thời gian tối đa để đọc dữ liệu
            .build()
         var url = "http://167.172.146.247:6789/api/"
        var apiInterface: ApiClient = Retrofit.Builder().baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(ApiClient::class.java)
    }



    @GET("category/{id}")
    fun getListChildSound(@Path("id") id: String): Call<DataSounds>
    @GET("category/")
    fun getListParentSound(): Call<DataImages>
}