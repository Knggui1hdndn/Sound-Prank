package com.pranksound.fartsound.trollandjoke.funnyapp.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.pranksound.fartsound.trollandjoke.funnyapp.Constraints
import com.pranksound.fartsound.trollandjoke.funnyapp.R
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataSound
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.lang.Exception


interface OffOrHotAdapterListens {
    fun itemClick(bitmap: Bitmap, linkSound: String, checkFirst:Boolean)
}

class OffOrHotAdapter(
    private val list: List<DataSound>,
    private val position: Int,
    val offOrHotAdapterListens: OffOrHotAdapterListens
) :
    RecyclerView.Adapter<OffOrHotAdapter.OffOrHotAdapterViewHolder>() {

    inner class OffOrHotAdapterViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private var img: ImageView = view.findViewById(R.id.img)
        private var mView: CardView = view.findViewById(R.id.mView)


        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(mDataImage: DataSound) {
            Picasso.get().load(mDataImage.image).into(img)
            val picasso = Picasso.get().load(mDataImage.image)
            var bitmapp: Bitmap? =null
             picasso.into(object : Target {
                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {



                }

                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {

                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

                }

            })
            mView.setOnClickListener {
                offOrHotAdapterListens.itemClick((img.drawable as BitmapDrawable).bitmap,mDataImage.source,false)

            }

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OffOrHotAdapterViewHolder {
        return OffOrHotAdapterViewHolder(
            LayoutInflater.from(parent.context).inflate(
                if (viewType == Constraints.VIEW_TYPE_HOT) R.layout.item_hot_sound else R.layout.item_off,
                parent, false
            )
        )

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: OffOrHotAdapterViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemViewType(position: Int): Int {
        return if (this.position == 0) Constraints.VIEW_TYPE_OFF else Constraints.VIEW_TYPE_HOT
    }
}