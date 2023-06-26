package com.pranksound.fartsound.trollandjoke.funnyapp.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pranksound.fartsound.trollandjoke.funnyapp.Constraints
import com.pranksound.fartsound.trollandjoke.funnyapp.databinding.ItemChildSoundBinding
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataImage
import com.pranksound.fartsound.trollandjoke.funnyapp.ui.Ghost
import com.squareup.picasso.Picasso

class ChildSoundAdapter(private val list: List<DataImage>) :
    RecyclerView.Adapter<ChildSoundAdapter.ParentSoundViewHolder>() {
    class ParentSoundViewHolder(val binding: ItemChildSoundBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(dataImage: DataImage) {
            Picasso.get().load(dataImage.icon).into(binding.imageView)
            binding.imageView.setOnClickListener {
                val context = it.context
                val intent = Intent(context, Ghost::class.java)
                intent.putExtra(Constraints.SOUND_CHILD_CLICK, dataImage.id)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentSoundViewHolder {
        return ParentSoundViewHolder(ItemChildSoundBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ParentSoundViewHolder, position: Int) {
        holder.bind(list[position])
    }
}