package com.pranksound.fartsound.trollandjoke.funnyapp.ui.adapter

import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.pranksound.fartsound.trollandjoke.funnyapp.Constraints
import com.pranksound.fartsound.trollandjoke.funnyapp.R
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataImage
import com.pranksound.fartsound.trollandjoke.funnyapp.ui.Home
import com.pranksound.fartsound.trollandjoke.funnyapp.ui.Show
import com.pranksound.fartsound.trollandjoke.funnyapp.ui.Utilities


class MemeSoundAdapter(private val list: List<DataImage>) :
    RecyclerView.Adapter<MemeSoundAdapter.MemeAdapterListensViewHolder>() {

    inner class MemeAdapterListensViewHolder(private val view: View) :
        RecyclerView.ViewHolder(view) {
        private var img: ImageView = view.findViewById(R.id.img)


        fun bind(mDataImage: DataImage) {
            Utilities.setImage(mDataImage.icon, img, view.context)
            img.setOnClickListener {
                val context = it.context
                (context as Home).showInterstitial(true) {
                    val intent = Intent(context, Show::class.java)
                    intent.putExtra(Constraints.PARENT_SOUND, mDataImage)
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MemeAdapterListensViewHolder {
        return MemeAdapterListensViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_meme_sound, parent, false)
        )

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MemeAdapterListensViewHolder, position: Int) {
        holder.bind(list[position])
    }


}