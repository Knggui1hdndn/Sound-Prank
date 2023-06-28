package com.pranksound.fartsound.trollandjoke.funnyapp.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import com.pranksound.fartsound.trollandjoke.funnyapp.R
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataImage
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataSound
import com.squareup.picasso.Picasso

object Utilities {

    fun loadImg(url: String, img: ImageView) {
        Picasso.get().load(url).error(R.drawable.img).into(img)
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


}