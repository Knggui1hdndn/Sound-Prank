package com.pranksound.fartsound.trollandjoke.funnyapp.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pranksound.fartsound.trollandjoke.funnyapp.ApiClient
import com.pranksound.fartsound.trollandjoke.funnyapp.contract.ApiClientContract
import com.pranksound.fartsound.trollandjoke.funnyapp.databinding.ItemSoundBinding
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataImage
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataSound
import com.pranksound.fartsound.trollandjoke.funnyapp.presenter.ApiClientPresenter
import com.squareup.picasso.Picasso

class ParentSoundAdapter(val list: List<DataImage>, val presenter: ApiClientPresenter) :
    RecyclerView.Adapter<ParentSoundAdapter.ParentSoundViewHolder>() {

    inner class ParentSoundViewHolder(val binding: ItemSoundBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(dataImage: DataImage) {
            Picasso.get().load(dataImage.icon).into(binding.imgParentSound)
            binding.txtTitleParent.text = dataImage.name
            binding.mRcy.visibility=View.VISIBLE

            setChildAdapter(binding.root.context, dataImage.id)
            binding.mCheck.setOnCheckedChangeListener { buttonView, isChecked ->
                Log.d("sssssssssss",isChecked.toString())

//                binding.mRcy.visibility = if (isChecked) {
//                    setChildAdapter(buttonView.context, dataImage.id)
//                    View.VISIBLE
//                } else View.INVISIBLE
            }
        }

        private fun setChildAdapter(context: Context, id: String) {
            var adapter: ChildSoundAdapter? = null
            val lmg = GridLayoutManager(context, 4)
            if (adapter == null) {
                presenter.getListChildSound(id, object : ApiClientContract.Listens {
                    override fun onSuccess(list: List<Any>) {
                        binding.mProgress.visibility = View.GONE
                        binding.mRcy.layoutManager = lmg
                        adapter = ChildSoundAdapter(list as List<DataSound>)
                        binding.mRcy.adapter = adapter
                    }

                    override fun onFailed(e: String) {
                        Toast.makeText(context, e, Toast.LENGTH_SHORT).show()
                    }
                })
            }
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