package com.pranksound.fartsound.trollandjoke.funnyapp.presenter

import com.pranksound.fartsound.trollandjoke.funnyapp.ApiClient
import com.pranksound.fartsound.trollandjoke.funnyapp.contract.ApiClientContract
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataImage
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataImages
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataSound
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataSounds
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.util.Objects


class ApiClientPresenter() : ApiClientContract.Presenter {
    override fun getListChildSound(
        id: String,
        listens: ApiClientContract.Listens
    ) {
        ApiClient.apiInterface.getListChildSound(id)
            .enqueue(object : retrofit2.Callback<DataSounds> {
                override fun onResponse(
                    call: Call<DataSounds>,
                    response: Response<DataSounds>
                ) {
                    if (response.isSuccessful) response.let { listens.onSuccess(it.body()!!.data) }
                }

                override fun onFailure(call: Call<DataSounds>, t: Throwable) {
                    listens.onFailed(t.message.toString())
                }

            })
    }

    override fun getListParentSound(listens: ApiClientContract.Listens) {
        ApiClient.apiInterface.getListParentSound()
            .enqueue(object : retrofit2.Callback<DataImages> {
                override fun onResponse(
                    call: Call<DataImages>,
                    response: Response<DataImages>
                ) {
                    if (response.isSuccessful) response.body()?.let { listens.onSuccess(it.data ) }
                }

                override fun onFailure(call: Call<DataImages>, t: Throwable) {
                    listens.onFailed(t.message.toString())
                }

            })
    }
}