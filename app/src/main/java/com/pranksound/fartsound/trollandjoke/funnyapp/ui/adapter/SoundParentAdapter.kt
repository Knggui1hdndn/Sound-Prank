package com.pranksound.fartsound.trollandjoke.funnyapp.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pranksound.fartsound.trollandjoke.funnyapp.Constraints
import com.pranksound.fartsound.trollandjoke.funnyapp.R
import com.pranksound.fartsound.trollandjoke.funnyapp.contract.ApiClientContract

import com.pranksound.fartsound.trollandjoke.funnyapp.databinding.ItemSoundParentBinding
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataImage
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataSound
import com.pranksound.fartsound.trollandjoke.funnyapp.presenter.ApiClientPresenter
import com.pranksound.fartsound.trollandjoke.funnyapp.ui.Show
import com.pranksound.fartsound.trollandjoke.funnyapp.ui.Utilities
import kotlin.properties.Delegates

interface RecyclerView {
    fun itemClick(triple: Triple<DataImage, Boolean, List<DataSound>>, position: Int)
}

class SoundParentAdapter(
    var lists: List<Triple<DataImage, Boolean, List<DataSound>>>,
    val presenter: ApiClientPresenter,
    val click: com.pranksound.fartsound.trollandjoke.funnyapp.ui.adapter.RecyclerView,

    ) : RecyclerView.Adapter<SoundParentAdapter.SoundParentViewHolder>() {
    companion object {
        var SIZE = 0
    }

    inner class SoundParentViewHolder(val binding: ItemSoundParentBinding) :
        RecyclerView.ViewHolder(binding.root), ChildSoundClickListens {
        private var childAdapter: SoundChildAdapter? = null
        private lateinit var mDataImage: DataImage
        private lateinit var context: Context
        private var isChecked by Delegates.notNull<Boolean>()

        fun bind(triple: Triple<DataImage, Boolean, List<DataSound>>, position: Int) {
            val mDataImage = triple.first
            isChecked = triple.second
            val listDataSound = triple.third
            this.mDataImage = mDataImage
            with(binding) {
                context = root.context
                txtTitleParent.text = mDataImage.name
                if (isChecked && listDataSound.isNotEmpty()) {
                    showChildSound(listDataSound, mDataImage, isChecked, position)
                }
                mRcy.visibility = if (isChecked) View.VISIBLE else View.GONE
                mConstraint.setOnClickListener {
                    isChecked = !isChecked
                    onShowProgress(listDataSound)
                    mRcy.visibility = if (isChecked) View.VISIBLE else View.GONE
                    if (isChecked) {
                        showChildSound(listDataSound, mDataImage, isChecked, position)
                    }
                }
            }
        }

        private fun onShowProgress(listDataSound: List<DataSound>) {
            if (listDataSound.isEmpty()) binding.mProgress.visibility =
                if (isChecked) View.VISIBLE else View.GONE
        }

        fun clearChildAdapter() {
            childAdapter = null
        }

        private fun showChildSound(
            listDataSound: List<DataSound>,
            mDataImage: DataImage,
            isChecked: Boolean, position: Int
        ) {
            childAdapter = null
            if (listDataSound.isNotEmpty()) {
                setChildAdapter(listDataSound);
                binding.mRcy.visibility = if (isChecked) View.VISIBLE else View.GONE
                click.itemClick(Triple(mDataImage, isChecked, listDataSound), position)
            } else {
                setChildAdapter(mDataImage.id) {
                    binding.mRcy.visibility = if (isChecked) View.VISIBLE else View.GONE
                    click.itemClick(Triple(mDataImage, isChecked, it), position)
                }
            }

        }

        @SuppressLint("SuspiciousIndentation")
        private fun setChildAdapter(id: String, call: (List<DataSound>) -> Unit) {
            var adapter: SoundChildAdapter? = null
            val lmg = GridLayoutManager(context, 3)
            presenter.getListChildSound(id, object : ApiClientContract.Listens {
                override fun onSuccess(list: List<Any>) {
                    binding.mProgress.visibility = View.GONE
                    binding.mRcy.layoutManager = lmg
                    adapter = SoundChildAdapter(
                        list as List<DataSound>,
                        this@SoundParentViewHolder,
                        mDataImage.name
                    )
                    binding.mRcy.adapter = adapter
                    call(list)
                }

                override fun onFailed(e: String) {
                    binding.mProgress.visibility = View.GONE
                    Utilities.showSnackBar(binding.root,context.getString(R.string.please_check_network))
                    isChecked = !isChecked
                }
            })
        }

        private fun setChildAdapter(list: List<DataSound>) {
            if (childAdapter == null) {
                if (list.isNotEmpty()) {
                    val lmg = GridLayoutManager(context, 3)
                    childAdapter = SoundChildAdapter(list, this, mDataImage.name)
                    binding.mRcy.layoutManager = lmg
                    binding.mRcy.adapter = childAdapter
                }
            }
        }

        override fun itemClick(position: Int) {
            val context = binding.root.context
            val intent = Intent(context, Show::class.java)
            intent.putExtra(Constraints.SOUND_CHILD_CLICK, position)
            intent.putExtra(Constraints.PARENT_SOUND, mDataImage)
            context.startActivity(intent)
        }
    }

    fun setData(lists: List<Triple<DataImage, Boolean, List<DataSound>>>) {
        this.lists = lists
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoundParentViewHolder {
        return SoundParentViewHolder(
            ItemSoundParentBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return lists.size
    }

    override fun onBindViewHolder(holder: SoundParentViewHolder, position: Int) {
        holder.clearChildAdapter()
        holder.bind(lists[position], position)

    }
}