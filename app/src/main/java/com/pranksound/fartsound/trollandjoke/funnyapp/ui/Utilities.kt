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
         val colorList = listOf(
            "FFE6B5", "71C7FF", "9DA0FF", "61E1C3", "FE80A5",
            "FF8E67", "AAF2E1", "B6E3FF", "D478F7", "FEBFD2"
        )

        return Color.parseColor("#"+colorList.shuffled()[0])
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