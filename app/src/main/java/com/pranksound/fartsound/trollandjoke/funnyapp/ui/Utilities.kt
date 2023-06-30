package com.pranksound.fartsound.trollandjoke.funnyapp.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.net.toUri
import com.google.android.material.snackbar.Snackbar
import com.pranksound.fartsound.trollandjoke.funnyapp.R
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataImage
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataSound
import com.squareup.picasso.Picasso

object Utilities {

    fun loadImg(url: String, img: ImageView) {
        try {
            Picasso.get().load(url).fit().error(R.drawable.img).into(img)
        }catch (e:java.lang.Exception){
            Picasso.get().load(url.toUri()).fit().error(R.drawable.img).into(img)
        }
    }

    private fun loadImg(bitmap: Bitmap, img: ImageView) {
        img.setImageBitmap(bitmap)
    }

    fun setImage(url: String, img: ImageView, context: Context) {
        try {
            val inputStream = context.assets.open(url)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            loadImg(bitmap, img)

        } catch (e: Exception) {
            Log.d("dddddddddssss",e.toString())
            loadImg(url, img)
        }

    }
    fun showSnackBar(v:View,s:String){
       Snackbar.make(v,s,Snackbar.LENGTH_SHORT).show()
    }


}