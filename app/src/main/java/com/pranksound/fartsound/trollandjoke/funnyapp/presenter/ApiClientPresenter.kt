package com.pranksound.fartsound.trollandjoke.funnyapp.presenter

import com.pranksound.fartsound.trollandjoke.funnyapp.ApiClient
import com.pranksound.fartsound.trollandjoke.funnyapp.contract.ApiClientContract


class ApiClientPresenter ():ApiClientContract.Presenter{
    override fun getListChildSound(
        id: String,
        listens: ApiClientContract.Listens
    ) {
         ApiClient.apiInterface.
    }

    override fun getListParentSound(listens: ApiClientContract.Listens)  {

    }
}