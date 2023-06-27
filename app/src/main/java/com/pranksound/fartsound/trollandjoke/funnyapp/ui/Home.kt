package com.pranksound.fartsound.trollandjoke.funnyapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.pranksound.fartsound.trollandjoke.funnyapp.contract.ApiClientContract
import com.pranksound.fartsound.trollandjoke.funnyapp.databinding.ActivityHomeBinding
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataImage
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataSound
import com.pranksound.fartsound.trollandjoke.funnyapp.presenter.ApiClientPresenter
import com.pranksound.fartsound.trollandjoke.funnyapp.ui.adapter.OffOrHotAdapter
import com.pranksound.fartsound.trollandjoke.funnyapp.ui.adapter.ParentSoundAdapter
import com.pranksound.fartsound.trollandjoke.funnyapp.ui.adapter.RecyclerView

class Home : AppCompatActivity(), ApiClientContract.Listens,
    com.pranksound.fartsound.trollandjoke.funnyapp.ui.adapter.RecyclerView {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var presenter: ApiClientPresenter
    private lateinit var adapter: ParentSoundAdapter
    private lateinit var listHash: MutableList<Triple<DataImage, Boolean, List<DataSound>>>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        presenter = ApiClientPresenter()
        presenter.getListParentSound(this)
        setAdapterHotSound()
        setAdapterOffLine()
    }

    private fun setAdapterHotSound() {
//        val adapter = OffOrHotAdapter()
//        val lmg = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//        binding.mRcy.layoutManager = lmg
//        binding.mRcy.adapter = adapter
    }

    private fun setAdapterOffLine() {

    }

    override fun onSuccess(list: List<Any>) {
        //ITEM-CHECK-LIST CHILD
        listHash = list.map {
            Triple(
                it as DataImage,
                false,
                mutableListOf<DataSound>()
            )
        }.toList().toMutableList()
        adapter = ParentSoundAdapter(listHash, presenter, this)
        val lmg = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.mRcy.layoutManager = lmg
        binding.mRcy.adapter = adapter

    }

    override fun onFailed(e: String) {
        Toast.makeText(this@Home, "Kiểm tra mạng", Toast.LENGTH_SHORT).show()

    }

    override fun itemClick(triple: Triple<DataImage, Boolean, List<DataSound>>, position: Int) {
        listHash[position] = triple
        try {
            adapter.setData(listHash)
        } catch (e: Exception) {
            Log.d("dddddddđff", e.toString())
        }

    }

}