package com.pranksound.fartsound.trollandjoke.funnyapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pranksound.fartsound.trollandjoke.funnyapp.databinding.ItemSoundBinding
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataImage
import com.squareup.picasso.Picasso

class ParentSoundAdapter(val list: List<DataImage>) :
    RecyclerView.Adapter<ParentSoundAdapter.ParentSoundViewHolder>() {
    class ParentSoundViewHolder(val binding: ItemSoundBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(dataImage: DataImage) {
            Picasso.get().load(dataImage.icon).into(binding.imgParentSound)
            binding.txtTitleParent.text = dataImage.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentSoundViewHolder {
        return ParentSoundViewHolder(ItemSoundBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ParentSoundViewHolder, position: Int) {
        holder.bind(list[position])
    }
}