package com.pranksound.fartsound.trollandjoke.funnyapp.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.prank.ShowChildSoundAdapter
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
import com.pranksound.fartsound.trollandjoke.funnyapp.ui.adapter.ChildSoundClickListens
import com.pranksound.fartsound.trollandjoke.funnyapp.ui.adapter.SoundParentAdapter
import java.io.File


class Show : AppCompatActivity(), ApiClientContract.Listens,
    ShowContract.ShowView, OnSeekBarChangeListener {

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        showPresenter.adjustVolume(progress)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {

    }

    private var list: MutableList<DataSound> = mutableListOf()
    private lateinit var binding: ActivityShowBinding
    private var isDownload: Boolean = false
    private lateinit var itemDataSound: DataSound
    private var source: String = ""
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
    private var listName = arrayListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActivity()
        with(binding) {
            txtTitleSoundChild.text = mDataImage.name
            seekBar.max = showPresenter.getMaxVolume()
            seekBar.progress = showPresenter.getCurrentVolume()
            seekBar.setOnSeekBarChangeListener(this@Show)
            imgNext.setOnClickListener { showPresenter.nextItem() }
            cbLoop.setOnClickListener { showPresenter.setLooping(cbLoop.isChecked) }
            btnTime.setOnClickListener { showPresenter.clickMenuPopup() }
            imgPre.setOnClickListener { showPresenter.prevItem() }
            img.setOnClickListener {
                showPresenter.playMusic(source)
            }
            imgBack.setOnClickListener {
                finish()
                showPresenter.setRepeatInterval(-1)
            }
            imgFavorite.setOnClickListener {
                startActivity(Intent(this@Show, Favorite::class.java))
             }
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
                val isCheckedFavorite = cbFavourite.isChecked
                showPresenter.checkDownLoad(mDataImage.name, itemDataSound.source, currentPosition)
                showPresenter.handleFavoriteChecked(
                    isCheckedFavorite,
                    itemDataSound,
                    mDataImage,
                    currentPosition
                )
                if (!isCheckedFavorite)
                    listPositionUnchecked.add(currentPosition)
                else
                    listPositionUnchecked.remove(currentPosition)
            }
            registerForContextMenu(btnTime)
            mRcy.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrolled(
                    position: Int, positionOffset: Float, positionOffsetPixels: Int
                ) {
                    handlePageScrolled(position)
                }
            })
        }
    }

    private fun handlePageScrolled(position: Int) {
        currentPosition = position
        with(showPresenter) {
            showPresenter.pauseMusic()
            showPresenter.setRepeatInterval(-1)
            binding.cbLoop.isChecked = false
            if (list.size > 0) {
                itemDataSound = list[position]
                source = itemDataSound.source
                if (isCallingActivity) {
                    mDataImage = getDataImgFavorite(source)
                    val c = listName[position].trim().split("")
                    val int = c[c.size - 2].toInt()
                    checkDownLoad(mDataImage.name.trim(), source, int)
                } else {
                    checkDownLoad(mDataImage.name, source, position)
                }
                isFavorite(source)
                setImage()
            }
        }
    }

    override fun onResume() {
        super.onResume()
      if (list.size>0){  handlePageScrolled(currentPosition)}
    }
    private fun setUpActivity() {
        check = ListensChangeNetwork.isConnectNetwork
        layoutRefresh = binding.refresh
        binding.mConstraint.isEnabled = false
        apiClientPresenter = ApiClientPresenter()
        showPresenter = ShowPresenter(this, this, 0, apiClientPresenter)
        isDisconnect = ListensChangeNetwork.isConnectNetwork == Constraints.DISCONNECT_NETWORK
        val callingActivity = intent.getStringExtra(Constraints.ACTIVITY_LAUNCH).toString()
        isCallingActivity = callingActivity == "Favorite"
        if (isCallingActivity) {
            listName = intent.getStringArrayListExtra("listName")!!
            binding.mProgress1.visibility = View.GONE
            setUpFavoriteActivity(intent)
        } else {
            setUpNormalActivity()
        }
        showPresenter = ShowPresenter(this, this, list.size, apiClientPresenter)
    }

    override fun onBackPressed() {
             val resultIntent = Intent()
            val arrayList = arrayListOf<Int>()
            arrayList.addAll(listPositionUnchecked)
            resultIntent.putIntegerArrayListExtra(
                Constraints.POSITION_FAVORITE_UNCHECKED,
                arrayList
            )
            setResult(Activity.RESULT_OK, resultIntent)

        showPresenter.setRepeatInterval(-1)
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
        mDataImage = showPresenter.getDataImgFavorite(list[currentPosition].source)

        setAdapter()
    }

    private fun setUpNormalActivity() {
        mDataImage = getDataImage()
        if (isDisconnect && SoundParentAdapter.SIZE < 50) {
            setUpListDisconnected()
        } else {
            setUpListConnected()
        }
    }

    private fun setUpListDisconnected() {
        try {
            list = try {
                FileHandler.getFileAssetByParentSound(this, mDataImage.name).toMutableList()
            } catch (e: Exception) {
                FileHandler.getDataSoundChildFromInternalStorage(
                    this,
                    mDataImage.name
                )[0].third.toMutableList()
            }

        } catch (e: Exception) {
        }
        binding.mProgress1.visibility = View.GONE
        setAdapter()
    }

    private fun setUpListConnected() {
        apiClientPresenter.getListChildSound(mDataImage.id, this)
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
        binding.imgDowload.setBackgroundResource(R.drawable.download_success)
        binding.imgDowload.isEnabled = false
        binding.mProgress1.visibility = View.INVISIBLE
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun dowLoadFailed(e: String) {
        binding.imgDowload.setBackgroundResource(R.drawable.download_24px)
        binding.imgDowload.isEnabled = true
        binding.mProgress1.visibility = View.INVISIBLE
        Utilities.showSnackBar(binding.root, e)

    }

    override fun isFavorite(boolean: Boolean) {
        binding.cbFavourite.isChecked = boolean
    }

    override fun isDownload(boolean: Boolean, draw: Int, s: String, position: Int) {
        isDownload = boolean
        with(binding) {
            imgDowload.setBackgroundResource(draw)
            imgDowload.isEnabled = boolean
            if (!boolean) {
                list[currentPosition].source = s
            }
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
        showPresenter = ShowPresenter(this, this, this.list.size, apiClientPresenter)
        setAdapter()
        binding.mProgress1.visibility = View.INVISIBLE
        binding.mConstraint.isEnabled = true
    }

    private fun setAdapter() {


        val adapter = ShowChildSoundAdapter(this.list, listName, object : ChildSoundClickListens {
            override fun itemClick(position: Int) {

            }
        }, mDataImage.name)
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
        Utilities.showSnackBar(binding.root, getString(R.string.please_check_network))
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