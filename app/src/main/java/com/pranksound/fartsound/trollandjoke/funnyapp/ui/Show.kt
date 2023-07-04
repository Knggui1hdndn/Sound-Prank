package com.pranksound.fartsound.trollandjoke.funnyapp.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
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
import com.pranksound.fartsound.trollandjoke.funnyapp.databinding.RefreshBinding
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataImage
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataSound
import com.pranksound.fartsound.trollandjoke.funnyapp.presenter.ApiClientPresenter
import com.pranksound.fartsound.trollandjoke.funnyapp.presenter.ShowPresenter
import com.pranksound.fartsound.trollandjoke.funnyapp.ui.adapter.ChildSoundAdapterListen
import com.pranksound.fartsound.trollandjoke.funnyapp.ui.adapter.ShowChildSoundAdapter


class Show : AppCompatActivity(), ApiClientContract.Listens, ChildSoundAdapterListen,
    ShowContract.ShowView, OnSeekBarChangeListener {

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        showPresenter.adjustVolume(progress)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {

    }

    private lateinit var list: MutableList<DataSound>
    private lateinit var binding: ActivityShowBinding
    private var isDownload: Boolean = false
    private lateinit var itemDataSound: DataSound
    private lateinit var source: String
    private lateinit var layoutRefresh: RefreshBinding
    private lateinit var showPresenter: ShowPresenter
    private lateinit var apiClientPresenter: ApiClientPresenter
    private lateinit var mDataImage: DataImage
    private var callingActivity = ""
    private var isCallingActivity = false
    private var isDisconnect: Boolean = false
    private var checkFavorite: Boolean = false
    private var checkNetWork = false
    private var currentPosition = 0
    private var check = ""
    private val listPositionUnchecked = mutableSetOf<Int>()
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
            img.setOnClickListener { showPresenter.playMusic(source) }
            layoutRefresh.button.setOnClickListener { recreate() }
            imgDowload.setOnClickListener {
                var position = currentPosition
                if (callingActivity == "Favorite") {
                    position = showPresenter.getPositionSound(source)
                }
                showPresenter.downLoad(mDataImage, itemDataSound, position)
                imgDowload.isEnabled = false
                binding.mProgress1.visibility = View.VISIBLE
            }
            cbFavourite.setOnClickListener {
                showPresenter.handleFavoriteChecked(
                    cbFavourite.isChecked,
                    itemDataSound,
                    mDataImage,
                    currentPosition
                )
           if (!cbFavourite.isChecked) listPositionUnchecked.add(currentPosition) else listPositionUnchecked.remove(currentPosition)
            }
            registerForContextMenu(btnTime)
            mRcy.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                @SuppressLint("UseCompatLoadingForDrawables")
                override fun onPageScrolled(
                    position: Int, positionOffset: Float, positionOffsetPixels: Int
                ) {
                    currentPosition = position
                    showPresenter.currentPosition = currentPosition
                    if (list.size > 0) {
                        itemDataSound = list[currentPosition]
                        source = itemDataSound.source
                        if (isCallingActivity) {
                            mDataImage = showPresenter.getDataImgFavorite(source)
                        }
                        showPresenter.isFavorite(source)
                        showPresenter.checkDownLoad(mDataImage.name, source)
                        setImage()
                    }
                }
            })
        }
    }

    private fun setUpActivity() {
        layoutRefresh = binding.refresh
        binding.mConstraint.isEnabled = false
        apiClientPresenter = ApiClientPresenter()
        showPresenter = ShowPresenter(this, this, 0, apiClientPresenter, checkNetWork)
        list = mutableListOf()
        isDisconnect = ListensChangeNetwork.isConnectNetwork == Constraints.DISCONNECT_NETWORK
        binding.mConstraint.isEnabled = false
        val intent = intent
        callingActivity = intent.getStringExtra(Constraints.ACTIVITY_LAUNCH).toString()
        isCallingActivity = callingActivity == "Favorite"
        if (isCallingActivity) {
            binding.mProgress1.visibility = View.GONE
            setUpFavoriteActivity(intent)
        } else {
            setUpNormalActivity()
        }
        showPresenter = ShowPresenter(this, this, list.size, apiClientPresenter, checkNetWork)
    }

    override fun onBackPressed() {
        if (isCallingActivity) {
            val resultIntent = Intent()
            val arrayList= arrayListOf<Int>()
             arrayList.addAll(listPositionUnchecked)
             resultIntent.putIntegerArrayListExtra(Constraints.POSITION_FAVORITE_UNCHECKED, arrayList )
            setResult(Activity.RESULT_OK, resultIntent)
        }
        finish()
    }

    private fun setUpFavoriteActivity(intent: Intent) {
        checkNetWork = intent.getBooleanExtra("checkNetwork", false)
        checkFavorite = true
        currentPosition = intent.getIntExtra(Constraints.SOUND_CHILD_CLICK, 0)
        if (!checkNetWork) {
            list.addAll(FileHandler.getFavoriteOff(this).map { it.second })
        } else {
            list.addAll(FileHandler.getFavoriteOnl(this).map { it.second })
            list.addAll(FileHandler.getFavoriteOff(this).map { it.second })
        }
        setAdapter()
    }

    private fun setUpNormalActivity() {
        mDataImage = getDataImage()
        if (isDisconnect) {
            binding.mProgress1.visibility=View.GONE
            list = try {
                FileHandler.getFileAssetByParentSound(this, mDataImage.name).toMutableList()
            } catch (e: Exception) {
                FileHandler.getDataSoundChildFromInternalStorage(
                    this,
                    mDataImage.name
                )[0].third.toMutableList()
            }
            setAdapter()
        } else {
            apiClientPresenter.getListChildSound(mDataImage.id, this)
        }
        check = ListensChangeNetwork.isConnectNetwork
        if (check == Constraints.DISCONNECT_NETWORK) binding.imgDowload.visibility = View.GONE
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
        val dataSoundChildList =
            FileHandler.getDataSoundChildFromInternalStorage(this@Show, mDataImage.name)
        val dataSoundChild = dataSoundChildList[0].third.size
        if (showPresenter.isFavorite(source)) {
            FileHandler.removeFavoriteOnl(this, itemDataSound)
            FileHandler.saveFavoriteOff(
                dataSoundChildList[0].third[dataSoundChild - 1],
                mDataImage,
                this, currentPosition
            )
        }
        source = dataSoundChildList[0].third[dataSoundChild - 1].source
        binding.imgDowload.setImageDrawable(getDrawable(R.drawable.baseline_cloud_done_24))
        binding.imgDowload.isEnabled = false
        binding.mProgress1.visibility = View.INVISIBLE


    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun dowLoadFailed(e: String) {
        binding.imgDowload.setImageDrawable(getDrawable(R.drawable.baseline_cloud_download_24))
        binding.imgDowload.isEnabled = true
        binding.mProgress1.visibility = View.INVISIBLE
        Utilities.showSnackBar(binding.root, e)
    }

    override fun isFavorite(boolean: Boolean) {
        binding.cbFavourite.isChecked = boolean
    }

    override fun isDownload(boolean: Boolean, draw: Int) {
        isDownload = boolean
        with(binding) {
            imgDowload.setImageResource(draw)
            imgDowload.isEnabled = boolean


        }
    }

    private fun setImage() {
        val url = this.itemDataSound.image
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
        showPresenter = ShowPresenter(this, this, this.list.size, apiClientPresenter, checkNetWork)
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
        binding.refresh.root.visibility = View.VISIBLE
        binding.mConstraint.visibility = View.GONE
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
    private fun ViewGroup.deepForEach(function: View.() -> Unit) {
        this.forEach { child ->
            child.function()
            if (child is ViewGroup) {
                child.deepForEach(function)
            }
        }
    }

    override fun showCurrentItem(int: Int) {
        binding.mRcy.setCurrentItem(int, false)
        setImage()
    }


}