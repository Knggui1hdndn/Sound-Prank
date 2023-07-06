package com.pranksound.fartsound.trollandjoke.funnyapp.ui.adapter

 import android.content.res.ColorStateList
 import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
 import com.pranksound.fartsound.trollandjoke.funnyapp.databinding.ItemSoundChildBinding
 import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataSound
import com.pranksound.fartsound.trollandjoke.funnyapp.ui.Utilities

interface SoundChildFavoriteClickListens {
    fun itemClick(position: Int)
}

class SoundChildFavoriteAdapter(
    private var list: List<DataSound>,
    private var listImgData: MutableList<String>,
    private val childSoundClickListens: ChildSoundClickListens,
 ) :
    RecyclerView.Adapter<SoundChildFavoriteAdapter.ChildSoundViewHolder>() {
    fun setData(list: List<DataSound>){
        this.list=list
        notifyDataSetChanged()
    }
    inner class ChildSoundViewHolder(val binding: ItemSoundChildBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(dataSound: DataSound, position: Int) {
        try {
            binding.txtName.text= listImgData[position]
        }catch (e:Exception){}
            binding.cardView.backgroundTintList= ColorStateList.valueOf(Utilities.getRandomColor())
            Utilities.setImage(dataSound.image, binding.img, binding.root.context)
            binding.img.setOnClickListener {
                childSoundClickListens.itemClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildSoundViewHolder {
        return ChildSoundViewHolder(
            ItemSoundChildBinding.inflate(
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