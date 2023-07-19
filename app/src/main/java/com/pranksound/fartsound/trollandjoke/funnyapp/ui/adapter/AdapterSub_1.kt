package com.pranksound.fartsound.trollandjoke.funnyapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pranksound.fartsound.trollandjoke.funnyapp.R
import com.pranksound.fartsound.trollandjoke.funnyapp.databinding.ItemSub1Binding

class AdapterSub_1(val list: List<Pair<String, Int>>) :
    RecyclerView.Adapter<AdapterSub_1.AdapterSub_1ViewHolder>() {
    inner class AdapterSub_1ViewHolder(val binding: ItemSub1Binding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(pair: Pair<String, Int>) {
            with(binding){
                img.setImageResource(pair.second)
                txt.text=pair.first
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterSub_1ViewHolder {
        return AdapterSub_1ViewHolder(ItemSub1Binding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: AdapterSub_1ViewHolder, position: Int) {
        holder.bind(list[position])
    }
}