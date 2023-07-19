package com.pranksound.fartsound.trollandjoke.funnyapp.ui.adapter

 import android.content.res.ColorStateList
 import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
 import com.pranksound.fartsound.trollandjoke.funnyapp.databinding.ItemSoundChildBinding
 import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataSound
import com.pranksound.fartsound.trollandjoke.funnyapp.ui.Utilities

interface ChildSoundClickListens {
    fun itemClick(position: Int)
}

class SoundChildAdapter(
    private var list: List<DataSound>,
    private val childSoundClickListens: ChildSoundClickListens,
    private val titleSoundParent:String
) :
    RecyclerView.Adapter<SoundChildAdapter.ChildSoundViewHolder>() {
    fun setData(list: List<DataSound>){
        this.list=list
        notifyDataSetChanged()
    }

    inner class ChildSoundViewHolder(val binding: ItemSoundChildBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(dataSound: DataSound, position: Int) {
            binding.txtName.text= "$titleSoundParent $position"
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