package com.pranksound.fartsound.trollandjoke.funnyapp.ui

import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.pranksound.fartsound.trollandjoke.funnyapp.Constraints
import com.pranksound.fartsound.trollandjoke.funnyapp.FileHandler
import com.pranksound.fartsound.trollandjoke.funnyapp.broadcast.ListenNetwork
import com.pranksound.fartsound.trollandjoke.funnyapp.broadcast.ListensChangeNetwork
import com.pranksound.fartsound.trollandjoke.funnyapp.contract.ApiClientContract
import com.pranksound.fartsound.trollandjoke.funnyapp.databinding.ActivityHomeBinding
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataImage
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataSound
import com.pranksound.fartsound.trollandjoke.funnyapp.presenter.ApiClientPresenter
import com.pranksound.fartsound.trollandjoke.funnyapp.ui.adapter.HotSoundAdapter

import com.pranksound.fartsound.trollandjoke.funnyapp.ui.adapter.ParentSoundAdapter
import com.pranksound.fartsound.trollandjoke.funnyapp.ui.adapter.RecyclerView
import java.lang.reflect.Array

class Home : AppCompatActivity(), ApiClientContract.Listens, RecyclerView, ListenNetwork {
    override fun onSuccess(list: List<Any>) {
        binding.mProgress.visibility = View.GONE
        //ITEM-CHECK-LIST CHILD
        listHash = list.map {
            Triple(
                it as DataImage,
                false,
                mutableListOf<DataSound>()
            )
        }.toList().toMutableList()
        setAdapter()
        setAdapterHot(list as List<DataImage>)
    }

    override fun onFailed(e: String) {
        binding.mProgress.visibility = View.GONE
        Utilities.showSnackBar(binding.root, "Vui lòng kiểm tra kết nối")
        listHash.clear()
        listHash.addAll(FileHandler.getAllFileAsset(this).toMutableList())
        listHash.addAll(FileHandler.getDataSoundChildFromInternalStorage(this, null))

        setAdapter()

    }

    private lateinit var binding: ActivityHomeBinding
    private lateinit var presenter: ApiClientPresenter
    private lateinit var adapter: ParentSoundAdapter
    private lateinit var adapterHot: HotSoundAdapter
    private lateinit var intentFilter: IntentFilter
    private lateinit var listensChangeNetwork: ListensChangeNetwork
    private lateinit var listHash: MutableList<Triple<DataImage, Boolean, List<DataSound>>>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        listHash = mutableListOf()
        presenter = ApiClientPresenter()
        listensChangeNetwork = ListensChangeNetwork(this)
        intentFilter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(listensChangeNetwork, intentFilter)
Log.d("okokokok",filesDir.path)

    }

    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {
        super.onPause()
    }

    private fun setAdapter() {
        adapter = ParentSoundAdapter(listHash, presenter, this)
        val lmg = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.mRcy.layoutManager = lmg
        binding.mRcy.adapter = adapter
    }

    private fun setAdapterHot(list: List<DataImage>) {
        Log.d("ssssssssssssss", list.size.toString())
        val lists = list.shuffled()
        adapterHot = HotSoundAdapter(lists)
        val lmg = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.mRcyHot.layoutManager = lmg
        binding.mRcyHot.adapter = adapterHot
    }


    override fun itemClick(triple: Triple<DataImage, Boolean, List<DataSound>>, position: Int) {
        listHash[position] = triple
        try {
            adapter.setData(listHash)
        } catch (e: Exception) {
            Log.d("dddddddđff", e.toString())
        }
    }

    override fun onChangeNetwork(string: String) {
        when (string) {
            Constraints.CONNECTION_NETWORK -> {
                binding.mProgress.visibility = View.VISIBLE
                presenter.getListParentSound(this)
                Utilities.showSnackBar(binding.root, "Đang load")
            }

            Constraints.DISCONNECT_NETWORK -> {
                binding.mProgress.visibility = View.VISIBLE
                onFailed("lỗi")
                Utilities.showSnackBar(binding.root, "Vui lòng kiểm tra kết nối")

            }
        }
    }

}