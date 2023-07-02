package com.pranksound.fartsound.trollandjoke.funnyapp.ui

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.pranksound.fartsound.trollandjoke.funnyapp.Constraints
import com.pranksound.fartsound.trollandjoke.funnyapp.FileHandler
import com.pranksound.fartsound.trollandjoke.funnyapp.R
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
        setAdapterMeme(Constraints.LIST_MEME)
        binding.mRcyHot.visibility = View.VISIBLE
        binding.mRcyMeme.visibility = View.VISIBLE
    }

    override fun onFailed(e: String) {
        binding.mProgress.visibility = View.GONE
        Utilities.showSnackBar(binding.root, "Vui lòng kiểm tra kết nối")
        if (listHash.size ==0){
            ListensChangeNetwork.isConnectNetwork = Constraints.DISCONNECT_NETWORK
            listHash.addAll(FileHandler.getAllFileAsset(this).toMutableList())
            listHash.addAll(FileHandler.getDataSoundChildFromInternalStorage(this, null))
        }else{
            ListensChangeNetwork.isConnectNetwork = Constraints.CONNECTION_NETWORK
        }
        setAdapter()
    }

    private lateinit var binding: ActivityHomeBinding
    private lateinit var presenter: ApiClientPresenter
    private lateinit var adapter: ParentSoundAdapter
    private lateinit var adapterHot: HotSoundAdapter
    private lateinit var adapterMeme: HotSoundAdapter
    private lateinit var intentFilter: IntentFilter
    private lateinit var listensChangeNetwork: ListensChangeNetwork
    private lateinit var listHash: MutableList<Triple<DataImage, Boolean, List<DataSound>>>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.mToolBar)
        listHash = mutableListOf()
        presenter = ApiClientPresenter()
        presenter.getListParentSound(this)
        listensChangeNetwork = ListensChangeNetwork(this)
        intentFilter = IntentFilter(Constraints.CONNECTIVITY_CHANGE)
        registerReceiver(listensChangeNetwork, intentFilter)
    }


    private fun setAdapter() {
        adapter = ParentSoundAdapter(listHash, presenter, this)
        val lmg = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.mRcy.layoutManager = lmg
        binding.mRcy.adapter = adapter
    }

    private fun setAdapterMeme(lists: List<DataImage>) {
        adapterMeme = HotSoundAdapter(lists)
        val lmg = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.mRcyMeme.layoutManager = lmg
        binding.mRcyMeme.adapter = adapterMeme
    }

    private fun setAdapterHot(list: List<DataImage>) {
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
         }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.favourite) {
            startActivity(Intent(this, Favorite::class.java))
        }
        return true
    }

    override fun onChangeNetwork(string: String) {
        when (string) {
            Constraints.CONNECTION_NETWORK -> {
                presenter.getListParentSound(this)
                Utilities.showSnackBar(binding.root, "Đang load")
            }

            Constraints.DISCONNECT_NETWORK -> {
                onFailed("lỗi")
                Utilities.showSnackBar(binding.root, "Vui lòng kiểm tra kết nối")
            }
        }
    }

}