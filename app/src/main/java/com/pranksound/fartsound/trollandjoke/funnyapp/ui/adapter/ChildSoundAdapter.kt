package com.pranksound.fartsound.trollandjoke.funnyapp.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pranksound.fartsound.trollandjoke.funnyapp.databinding.ItemChildSoundBinding
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataSound
import com.pranksound.fartsound.trollandjoke.funnyapp.ui.Utilities
import com.squareup.picasso.Picasso

interface ChildSoundClickListens {
    fun itemClick(position: Int)
}

class ChildSoundAdapter(
    private val list: List<DataSound>,
    private val childSoundClickListens: ChildSoundClickListens
) :
    RecyclerView.Adapter<ChildSoundAdapter.ChildSoundViewHolder>() {
    inner class ChildSoundViewHolder(val binding: ItemChildSoundBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(dataSound: DataSound, position: Int) {
            Utilities.setImage(dataSound.image, binding.imageView, binding.root.context)
            binding.imageView.setOnClickListener {
                childSoundClickListens.itemClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildSoundViewHolder {
        return ChildSoundViewHolder(
            ItemChildSoundBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ChildSoundViewHolder, position: Int) {
        holder.bind(list[position], position)
    }
}