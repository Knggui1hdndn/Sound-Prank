package com.pranksound.fartsound.trollandjoke.funnyapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pranksound.fartsound.trollandjoke.funnyapp.R
import com.pranksound.fartsound.trollandjoke.funnyapp.databinding.ItemSub1Binding
import com.pranksound.fartsound.trollandjoke.funnyapp.databinding.ItemSub2Binding

class AdapterSub_2(val list: List<Triple<String, String, Int>>) :
    RecyclerView.Adapter<AdapterSub_2.AdapterSub_2ViewHolder>() {
    inner class AdapterSub_2ViewHolder(val binding: ItemSub2Binding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(pair: Triple<String, String, Int>) {
            with(binding) {
                img.setImageResource(pair.third)
                txtMess.text = pair.second
                txtName.text = pair.first
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterSub_2ViewHolder {
        return AdapterSub_2ViewHolder(
            ItemSub2Binding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: AdapterSub_2ViewHolder, position: Int) {
        holder.bind(list[position])
    }
}