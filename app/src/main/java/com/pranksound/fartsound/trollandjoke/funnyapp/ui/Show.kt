package com.pranksound.fartsound.trollandjoke.funnyapp.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
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

    private lateinit var list: MutableList<DataSound>
    private lateinit var binding: ActivityShowBinding
    private lateinit var showPresenter: ShowPresenter
    private lateinit var apiClientPresenter: ApiClientPresenter
    private lateinit var mDataImage: DataImage
    private var currentPosition = 0
    private var check = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpActivity()
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
                binding.mProgress1.visibility = View.VISIBLE
            }
            cbFavourite.setOnClickListener {
                with(FileHandler) {
                    if (check == Constraints.CONNECTION_NETWORK) {
                        saveFavoriteOnl(list[currentPosition], this@Show)
                    } else {
                        saveFavoriteOff(list[currentPosition], this@Show)
                    }
                }
            }
            registerForContextMenu(btnTime)
            mRcy.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                @SuppressLint("UseCompatLoadingForDrawables")
                override fun onPageScrolled(
                    position: Int, positionOffset: Float, positionOffsetPixels: Int
                ) {
                    currentPosition = position
                    showPresenter.currentPosition = currentPosition
                    showPresenter.checkFavorite(check, list[currentPosition].source)
                    showPresenter.checkDownLoad(mDataImage.name)
                    setImage()
                }
            })
        }
    }

    private fun setUpActivity() {
        binding.mConstraint.isEnabled = false
        mDataImage = getDataImage()
        apiClientPresenter = ApiClientPresenter()
        list = mutableListOf()
        showPresenter = ShowPresenter(this, this, 0, apiClientPresenter)
        binding.mConstraint.isEnabled = false
        val intent = intent

        if (intent.getStringExtra(Constraints.ACTIVITY_LAUNCH) =="Favorite") {
            currentPosition = intent.getIntExtra(Constraints.SOUND_CHILD_CLICK, 0)

            val checkNetWork =
                ListensChangeNetwork.isConnectNetwork == Constraints.DISCONNECT_NETWORK


            if (checkNetWork) {
                list.addAll(FileHandler.getFavoriteOff(this))
                onFailed("e")
            } else {
                list.addAll(FileHandler.getFavoriteOnl(this))
                list.addAll(FileHandler.getFavoriteOff(this))
                onFailed("e")
            }
        } else {
            mDataImage = getDataImage()
            val checkNetWork =
                ListensChangeNetwork.isConnectNetwork == Constraints.DISCONNECT_NETWORK
            if (checkNetWork) {
                try {
                    list = FileHandler.getFileAssetByParentSound(this, mDataImage.name).toMutableList()
                } catch (e: Exception) {
                    list =
                        FileHandler.getDataSoundChildFromInternalStorage(this, mDataImage.name)[0].third.toMutableList()
                }
                onFailed("e")
            } else {
                apiClientPresenter.getListChildSound(mDataImage.id, this)
            }
            check = ListensChangeNetwork.isConnectNetwork
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
        binding.imgDowload.isEnabled = false
        binding.mProgress1.visibility = View.INVISIBLE

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun dowLoadFailed(e: String) {
        binding.imgDowload.setImageDrawable(getDrawable(R.drawable.baseline_cloud_download_24))
        binding.imgDowload.isEnabled = true
        binding.mProgress1.visibility = View.INVISIBLE
        Utilities.showSnackBar(binding.root, "Lỗi mạng")
    }

    override fun isFavorite(boolean: Boolean) {
        binding.cbFavourite.isChecked = boolean
    }

    override fun isDownload(boolean: Boolean, draw: Int) {
        with(binding) {
            imgDowload.setImageResource(draw)
            imgDowload.isEnabled = boolean
        }
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
        this.list = list as MutableList<DataSound>
        showPresenter = ShowPresenter(this, this, this.list.size, apiClientPresenter)
        setImage()
        setAdapter()
        binding.mProgress1.visibility = View.INVISIBLE
        binding.mConstraint.isEnabled = true

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
        binding.mProgress1.visibility = View.INVISIBLE
        binding.imgDowload.visibility = View.VISIBLE
        binding.mConstraint.isEnabled = true

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
    }


    override fun showCurrentItem(int: Int) {
        binding.mRcy.setCurrentItem(int, false)
        setImage()
    }


}