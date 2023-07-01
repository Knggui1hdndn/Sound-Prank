package com.pranksound.fartsound.trollandjoke.funnyapp.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.pranksound.fartsound.trollandjoke.funnyapp.Constraints
import com.pranksound.fartsound.trollandjoke.funnyapp.R
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataSound
import com.pranksound.fartsound.trollandjoke.funnyapp.ui.Utilities


interface  ChildSoundAdapterListen {
    fun itemClick(bitmap: Bitmap, linkSound: String, checkFirst: Boolean)
}

class ShowChildSoundAdapter(
    private val list: List<DataSound>,
     val offOrHotAdapterListens: ChildSoundAdapterListen
) :
    RecyclerView.Adapter<ShowChildSoundAdapter.HotAdapterListensViewHolder>() {

    inner class HotAdapterListensViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private var img: ImageView = view.findViewById(R.id.img)
        private var mView: CardView = view.findViewById(R.id.mView)
        private var txt: TextView = view.findViewById(R.id.txt)


        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(mDataImage: DataSound) {
            Utilities.setImage(mDataImage.image, img,view.context)
            txt.text=mDataImage.image
            mView.setOnClickListener {
                offOrHotAdapterListens.itemClick(
                    (img.drawable as BitmapDrawable).bitmap,
                    mDataImage.source,
                    false
                )
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HotAdapterListensViewHolder {
        return HotAdapterListensViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_hot_sound_1  ,
                parent, false
            )
        )

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: HotAdapterListensViewHolder, position: Int) {
        holder.bind(list[position])
     }


}