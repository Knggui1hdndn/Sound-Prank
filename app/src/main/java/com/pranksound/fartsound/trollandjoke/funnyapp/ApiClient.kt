package com.pranksound.fartsound.trollandjoke.funnyapp

import com.pranksound.fartsound.trollandjoke.funnyapp.Constraints.BASE_URL
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataImages
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataSounds
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url
import java.util.concurrent.TimeUnit

interface ApiClient {
    companion object {
        val okHttpClient = OkHttpClient.Builder()
            .callTimeout(3000L, TimeUnit.MILLISECONDS)//đặt thời gian tối đa kết nối với sv
            .readTimeout(3000L, TimeUnit.MILLISECONDS)// đặt thời gian tối đa để đọc dữ liệu
            .build()
        var url = BASE_URL
        var apiInterface: ApiClient = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(url+"/")
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(ApiClient::class.java)
    }
    @GET
    fun downloadStream(@Url url: String): Call<ResponseBody>

    @GET("category/{id}")
    fun getListChildSound(@Path("id") id: String): Call<DataSounds>

    @GET("category/")
    fun getListParentSound(): Call<DataImages>


}