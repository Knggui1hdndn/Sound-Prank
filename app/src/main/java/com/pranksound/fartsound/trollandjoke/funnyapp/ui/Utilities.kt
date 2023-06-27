package com.pranksound.fartsound.trollandjoke.funnyapp.ui

import android.widget.ImageView
import com.pranksound.fartsound.trollandjoke.funnyapp.R
import com.squareup.picasso.Picasso

object Utilities {
    fun loadImg(url:String,img:ImageView){
        Picasso.get().load(url).error(R.drawable.img).into(img)
    }
}
