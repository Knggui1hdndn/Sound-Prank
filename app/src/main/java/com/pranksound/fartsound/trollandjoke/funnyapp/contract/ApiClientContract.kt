package com.pranksound.fartsound.trollandjoke.funnyapp.contract

import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataImage
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataSound
import java.util.Objects

interface ApiClientContract {
    interface View{
        fun showListChildSound()
        fun showListParentSound()
        
    }
    interface Listens {
        fun onSuccess(list:List<Objects>)
        fun onFailed(e:String)
    }

    interface Presenter{
        fun getListChildSound(id :String,listens: Listens)
        fun getListParentSound(listens: Listens)
    }
}