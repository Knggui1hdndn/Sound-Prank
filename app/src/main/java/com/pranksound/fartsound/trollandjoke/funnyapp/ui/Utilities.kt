package com.pranksound.fartsound.trollandjoke.funnyapp.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Patterns
import android.view.View
import android.widget.ImageView
import androidx.core.net.toUri
import com.google.android.material.snackbar.Snackbar
import com.pranksound.fartsound.trollandjoke.funnyapp.R
import com.squareup.picasso.Picasso
import java.util.Random


object Utilities {
    fun getRandomColor(): Int {
        val rnd = Random()
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
    }
    fun loadImg(url: String, img: ImageView) {


        try {
            if (Patterns.WEB_URL.matcher(url).matches()) {
                Picasso.get().load(url).fit().error(R.drawable.img).into(img)
            }else{
                Picasso.get().load(url.toUri()).fit().error(R.drawable.img).into(img)
            }

        } catch (e: java.lang.Exception) {

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
             loadImg(url, img)
        }

    }
    fun showSnackBar(v:View,s:String){
       Snackbar.make(v,s,Snackbar.LENGTH_SHORT).show()
    }


}