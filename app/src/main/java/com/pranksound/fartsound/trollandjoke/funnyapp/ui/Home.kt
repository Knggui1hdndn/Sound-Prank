package com.pranksound.fartsound.trollandjoke.funnyapp.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.TypefaceSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
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
import com.pranksound.fartsound.trollandjoke.funnyapp.ui.adapter.MemeSoundAdapter
import com.pranksound.fartsound.trollandjoke.funnyapp.ui.adapter.RecyclerView
import com.pranksound.fartsound.trollandjoke.funnyapp.ui.adapter.SoundParentAdapter


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
        binding.txtOff.visibility = View.GONE
        binding.btnLoad.visibility = View.GONE
    }

    override fun onFailed(e: String) {
        binding.mProgress.visibility = View.GONE
        Utilities.showSnackBar(binding.root, "Vui lòng kiểm tra kết nối")
        if (listHash.size == 0) {
            ListensChangeNetwork.isConnectNetwork = Constraints.DISCONNECT_NETWORK
            listHash.addAll(FileHandler.getAllFileAsset(this).toMutableList())
            listHash.addAll(FileHandler.getDataSoundChildFromInternalStorage(this, null))
        } else {
            ListensChangeNetwork.isConnectNetwork = Constraints.CONNECTION_NETWORK
        }
        setAdapter()
        showMess()
    }

    private lateinit var binding: ActivityHomeBinding
    private lateinit var presenter: ApiClientPresenter
    private lateinit var adapter: SoundParentAdapter
    private lateinit var adapterHot: HotSoundAdapter
    private lateinit var adapterMeme: MemeSoundAdapter
    private lateinit var intentFilter: IntentFilter
    private lateinit var listensChangeNetwork: ListensChangeNetwork
    private lateinit var listHash: MutableList<Triple<DataImage, Boolean, List<DataSound>>>

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //val typeFace = Typeface.create(resources.getFont(R.font.kodchasan), Typeface.BOLD)
        //        val toolbarTitle = SpannableString(binding.mToolBar.title)
        //        val toolbarTypefaceSpan = TypefaceSpan(typeFace)
        //        toolbarTitle.setSpan(
        //            toolbarTypefaceSpan,
        //            0,
        //            toolbarTitle.length,
        //            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        //        )
        //        binding.mToolBar.title = toolbarTitle
        setSupportActionBar(binding.mToolBar)
        listHash = mutableListOf()
        presenter = ApiClientPresenter()
        listensChangeNetwork = ListensChangeNetwork(this)
        intentFilter = IntentFilter(Constraints.CONNECTIVITY_CHANGE)
        registerReceiver(listensChangeNetwork, intentFilter)
        binding.btnLoad.setOnClickListener {
            binding.mProgress.visibility = View.VISIBLE
            presenter.getListParentSound(this)
            binding.btnLoad.visibility = View.INVISIBLE
        }
        binding.txtOff.setOnClickListener {
            recreate()
        }

    }


    private fun setAdapter() {
        adapter = SoundParentAdapter(listHash, presenter, this)
        val lmg = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.mRcy.layoutManager = lmg
        binding.mRcy.adapter = adapter
    }

    private fun setAdapterMeme(lists: List<DataImage>) {
        adapterMeme = MemeSoundAdapter(lists)
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
                binding.txtOff.text = "Đang load"
                binding.btnLoad.visibility = View.GONE
                binding.txtOff.visibility = View.VISIBLE
                binding.txtOff.isEnabled = false
            }

            Constraints.DISCONNECT_NETWORK -> {
                onFailed("lỗi")
                showMess()
            }
        }
    }

    private fun showMess() {
        if (listHash.size < 50) {
            binding.txtOff.text = "Không có kết nối Internet \n Đang dùng off"
            binding.txtOff.isEnabled = false
        } else {
            binding.txtOff.text = "Không có kết nối Internet \n Chuyển sang chế độ off"
            binding.txtOff.isEnabled = true
        }

        binding.txtOff.visibility = View.VISIBLE
        binding.btnLoad.visibility = View.VISIBLE
    }

}