package com.pranksound.fartsound.trollandjoke.funnyapp.ui.adapter

import android.content.Context
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
import java.util.Locale


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
                check(context, mDataImage)
            }
        }

        private fun startActivity(context: Context, mDataImage: DataImage) {
            val intent = Intent(context, Show::class.java)
            intent.putExtra(Constraints.PARENT_SOUND, mDataImage)
            context.startActivity(intent)
        }

        private fun check(context: Context, mDataImage: DataImage) {
            val home = context as Home
            if (Utilities.hasMemeOrHot(mDataImage.name) || Utilities.countClickSound == 4 || Utilities.countClickSound == 0) {
                home.showInterstitial(true) {
                    startActivity(context, mDataImage)
                }
            } else {
                startActivity(context, mDataImage)
            }
            Utilities.countClickSound++
            if (Utilities.countClickSound == 4) Utilities.countClickSound = 0
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HotAdapterListensViewHolder {
        return HotAdapterListensViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_hot_sound, parent, false)
        )

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: HotAdapterListensViewHolder, position: Int) {
        holder.bind(list[position])
    }


}