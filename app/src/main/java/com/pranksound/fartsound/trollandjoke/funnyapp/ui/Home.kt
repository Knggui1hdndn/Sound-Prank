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
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.pranksound.fartsound.trollandjoke.funnyapp.Constraints
import com.pranksound.fartsound.trollandjoke.funnyapp.FileHandler
import com.pranksound.fartsound.trollandjoke.funnyapp.R
import com.pranksound.fartsound.trollandjoke.funnyapp.application.BaseActivity
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


class Home : BaseActivity(), ApiClientContract.Listens, RecyclerView, ListenNetwork {
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
        ListensChangeNetwork.isConnectNetwork = Constraints.CONNECTION_NETWORK
    }

    override fun onFailed(e: String) {
        ListensChangeNetwork.isConnectNetwork = Constraints.DISCONNECT_NETWORK
        binding.mProgress.visibility = View.GONE
        Utilities.showSnackBar(binding.root, getString(R.string.please_check_network))
        if (listHash.size == 0) {
            listHash.addAll(FileHandler.getAllFileAsset(this).toMutableList())
            listHash.addAll(FileHandler.getDataSoundChildFromInternalStorage(this, null))
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
        getConfigData(false)

        showBannerAds(binding.bannerAds)
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
            listHash.clear()
            recreate()
        }
    }

    private fun setAdapter() {
        SoundParentAdapter.SIZE = listHash.size
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


    override fun itemClick(triple: Triple<DataImage, Boolean, List<DataSound>>, position: Int) {0
        try {
            listHash[position] = triple
            adapter.setData(listHash)
        } catch (e: Exception) {
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.favourite -> startActivity(Intent(this, Favorite::class.java))
            R.id.setting -> startActivity(Intent(this, Setting::class.java))
        }

        return true
    }

    override fun onChangeNetwork(string: String) {
        when (string) {
            Constraints.CONNECTION_NETWORK -> {
                presenter.getListParentSound(this)
                binding.txtOff.text = getString(R.string.loading)
                binding.btnLoad.visibility = View.GONE
                binding.txtOff.visibility = View.VISIBLE
                binding.txtOff.isEnabled = false
            }

            Constraints.DISCONNECT_NETWORK -> {
                onFailed("lỗi")
                showMess()
                ListensChangeNetwork.isConnectNetwork = string
            }
        }
    }

    private fun showMess() {
        if (listHash.size < 50) {
            binding.txtOff.text = getString(R.string.currently_off)
            binding.txtOff.isEnabled = false
        } else {
            binding.txtOff.text = getString(R.string.no_internet_off)
            binding.txtOff.isEnabled = true
        }
        binding.txtOff.visibility = View.VISIBLE
        binding.btnLoad.visibility = View.VISIBLE
    }

}