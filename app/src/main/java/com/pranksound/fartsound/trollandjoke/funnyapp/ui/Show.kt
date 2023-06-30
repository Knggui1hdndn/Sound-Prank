package com.pranksound.fartsound.trollandjoke.funnyapp.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.pranksound.fartsound.trollandjoke.funnyapp.Constraints
import com.pranksound.fartsound.trollandjoke.funnyapp.FileHandler
import com.pranksound.fartsound.trollandjoke.funnyapp.R
import com.pranksound.fartsound.trollandjoke.funnyapp.broadcast.ListensChangeNetwork
import com.pranksound.fartsound.trollandjoke.funnyapp.contract.ApiClientContract
import com.pranksound.fartsound.trollandjoke.funnyapp.contract.ShowContract
import com.pranksound.fartsound.trollandjoke.funnyapp.databinding.ActivityShowBinding
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataImage
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataSound
import com.pranksound.fartsound.trollandjoke.funnyapp.presenter.ApiClientPresenter
import com.pranksound.fartsound.trollandjoke.funnyapp.presenter.ShowPresenter
import com.pranksound.fartsound.trollandjoke.funnyapp.ui.adapter.ChildSoundAdapterListen
import com.pranksound.fartsound.trollandjoke.funnyapp.ui.adapter.ShowChildSoundAdapter


class Show : AppCompatActivity(), ApiClientContract.Listens, ChildSoundAdapterListen,
    ShowContract.MusicPlayerView, OnSeekBarChangeListener {

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        showPresenter.adjustVolume(progress)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {

    }

    private lateinit var list: List<DataSound>
    private lateinit var binding: ActivityShowBinding
    private lateinit var showPresenter: ShowPresenter
    private lateinit var apiClientPresenter: ApiClientPresenter
    private var currentPosition = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowBinding.inflate(layoutInflater)
        setContentView(binding.root)
        apiClientPresenter = ApiClientPresenter()
        val mDataImage = getDataImage()
        list = mutableListOf()

        showPresenter = ShowPresenter(this, this, 0, apiClientPresenter)
        if (ListensChangeNetwork.isConnectNetwork == Constraints.DISCONNECT_NETWORK) {
            list = FileHandler.getFileAssetByParentSound(this, mDataImage.name)
        } else {
            apiClientPresenter.getListChildSound(mDataImage.id, this)
        }

        with(binding) {
            seekBar.max = showPresenter.getMaxVolume()
            seekBar.progress = showPresenter.getCurrentVolume()
            seekBar.setOnSeekBarChangeListener(this@Show)
            btnNext.setOnClickListener { showPresenter.nextItem() }
            swLoop.setOnClickListener { showPresenter.setLooping(swLoop.isChecked) }
            btnTime.setOnClickListener { showPresenter.clickMenuPopup() }
            btnPre.setOnClickListener { showPresenter.prevItem() }
            img.setOnClickListener { showPresenter.playMusic(list[currentPosition].source) }
            imgDowload.setOnClickListener {
                 showPresenter.downLoad(mDataImage, list[currentPosition])
                imgDowload.isEnabled = false
            }
            registerForContextMenu(btnTime)
            mRcy.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                @SuppressLint("UseCompatLoadingForDrawables")
                override fun onPageScrolled(
                    position: Int, positionOffset: Float, positionOffsetPixels: Int
                ) {
                    currentPosition = position
                    showPresenter.currentPosition = currentPosition
                    FileHandler.checkFileExists(this@Show, mDataImage.name, currentPosition).let {
                        with(binding) {
                            Log.d("kookkokok", it.toString())
                            if (!it) {
                                imgDowload.setBackgroundResource(R.drawable.baseline_cloud_download_24)

                            } else {
                                imgDowload.setBackgroundResource(R.drawable.baseline_cloud_done_24)

                            }
                        }
                    }
                    setImage()
                }
            })
        }
    }

    override fun showMenuPopup() {
        val popupMenu = PopupMenu(this, binding.btnTime)
        val menu = popupMenu.menu
        val list = resources.getStringArray(R.array.time)
        list.forEach {
            menu!!.add(0, 0, 0, it)
        }
        popupMenu.setOnMenuItemClickListener {
            showPresenter.clickItemMenuPopup(list.indexOf(it.title))
            true
        }
        popupMenu.show()
    }

    @SuppressLint("ResourceType", "UseCompatLoadingForDrawables")
    override fun downLoadSuccess() {
        binding.imgDowload.setImageDrawable(getDrawable(R.drawable.baseline_cloud_done_24))
        binding.imgDowload.isEnabled = true
    }

    override fun dowLoadFailed(e: String) {

    }

    private fun setImage() {
        val url = this.list[currentPosition].image
        Utilities.setImage(url, binding.img, this)
    }

    private fun getDataImage(): DataImage {
        val intent = intent
        currentPosition = intent.getIntExtra(Constraints.SOUND_CHILD_CLICK, 0)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(Constraints.PARENT_SOUND, DataImage::class.java)!!
        } else {
            intent.getSerializableExtra(Constraints.PARENT_SOUND) as DataImage
        }
    }

    override fun onSuccess(list: List<Any>) {
        this.list = list as List<DataSound>
        showPresenter = ShowPresenter(this, this, this.list.size, apiClientPresenter)
        setImage()
        setAdapter()

    }

    private fun setAdapter() {
        val adapter = ShowChildSoundAdapter(this.list, this)
        binding.mRcy.apply {
            val comPosit = CompositePageTransformer()
            comPosit.addTransformer(MarginPageTransformer(30))
            offscreenPageLimit = 3
            clipToPadding = false
            clipChildren = false
            setPageTransformer(comPosit)
            this.adapter = adapter
            binding.mRcy.setCurrentItem(currentPosition, true)
        }
    }

    override fun onFailed(e: String) {
        setAdapter()
    }

    override fun itemClick(bitmap: Bitmap, linkSound: String, checkFirst: Boolean) {
        binding.img.setImageBitmap(bitmap)
    }

    override fun onPause() {
        super.onPause()
        showPresenter.pauseMusic()
    }

    override fun loadSuccess() {
        binding.mProgress.visibility = View.INVISIBLE
    }

    override fun load() {
        binding.mProgress.visibility = View.VISIBLE

    }

    override fun loadFailed(e: String) {
        binding.mProgress.visibility = View.INVISIBLE
        Log.d("ssssssss", e)
    }


    override fun showCurrentItem(int: Int) {
        binding.mRcy.setCurrentItem(int, false)
        setImage()
    }


}