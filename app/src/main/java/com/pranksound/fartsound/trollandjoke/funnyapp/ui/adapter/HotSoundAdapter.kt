package com.pranksound.fartsound.trollandjoke.funnyapp.ui.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.pranksound.fartsound.trollandjoke.funnyapp.Constraints
import com.pranksound.fartsound.trollandjoke.funnyapp.R
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataImage
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataSound
import com.pranksound.fartsound.trollandjoke.funnyapp.ui.Show
import com.pranksound.fartsound.trollandjoke.funnyapp.ui.Utilities


interface HotSoundAdapterListen {
    fun itemClick(bitmap: Bitmap, linkSound: String, checkFirst: Boolean)
}

class HotSoundAdapter(
    private val list: List<DataImage>,

    ) :
    RecyclerView.Adapter<HotSoundAdapter.HotAdapterListensViewHolder>() {

    inner class HotAdapterListensViewHolder(private val view: View) :
        RecyclerView.ViewHolder(view) {
        private var img: ImageView = view.findViewById(R.id.img)
        private var mView: CardView = view.findViewById(R.id.mView)


        fun bind(mDataImage: DataImage) {
            Utilities.setImage(mDataImage.icon, img, view.context)
            mView.setOnClickListener {
                val context = it.context
                val intent = Intent(context, Show::class.java)
                intent.putExtra(Constraints.PARENT_SOUND, mDataImage)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HotAdapterListensViewHolder {
        return HotAdapterListensViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_hot_sound, parent, false)
        )

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: HotAdapterListensViewHolder, position: Int) {
        holder.bind(list[position])
    }


}