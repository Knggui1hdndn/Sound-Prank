package com.pranksound.fartsound.trollandjoke.funnyapp.contract

import java.io.InputStream

interface ApiClientContract {
    interface View{
        fun showListChildSound()
        fun showListParentSound()
        
    }
    interface Listens {
        fun onSuccess(list: List<Any >)
        fun onFailed(e:String)
    }

    interface Presenter{
        fun downloadStream (url:String,callback: (InputStream? )->Unit)
         fun getListChildSound(id :String,listens: Listens)
        fun getListParentSound(listens: Listens)
    }
}