package com.pranksound.fartsound.trollandjoke.funnyapp.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.prank.ShowChildSoundAdapter
import com.pranksound.fartsound.trollandjoke.funnyapp.Constraints
import com.pranksound.fartsound.trollandjoke.funnyapp.R
import com.pranksound.fartsound.trollandjoke.funnyapp.contract.ApiClientContract
import com.pranksound.fartsound.trollandjoke.funnyapp.contract.ShowContract
import com.pranksound.fartsound.trollandjoke.funnyapp.databinding.ActivityShowBinding
import com.pranksound.fartsound.trollandjoke.funnyapp.databinding.RefreshBinding
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataImage
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataSound
import com.pranksound.fartsound.trollandjoke.funnyapp.presenter.ApiClientPresenter
import com.pranksound.fartsound.trollandjoke.funnyapp.presenter.ShowPresenter
import com.pranksound.fartsound.trollandjoke.funnyapp.ui.adapter.ChildSoundClickListens


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
    lateinit var binding: ActivityShowBinding
    private var currentPosition = 0
    private lateinit var mDataImage: DataImage
    private lateinit var layoutRefresh: RefreshBinding
    private lateinit var showPresenter: ShowPresenter
    private lateinit var apiClientPresenter: ApiClientPresenter
    private var listName = arrayListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpActivity()
        with(binding) {
            txtTitleSoundChild.text = mDataImage.name
            mProgress1.visibility = View.VISIBLE
            with(showPresenter) {
                seekBar.max = getMaxVolume()

                seekBar.progress = getCurrentVolume()

                seekBar.setOnSeekBarChangeListener(this@Show)

                imgNext.setOnClickListener { nextItem() }

                cbLoop.setOnClickListener { setLooping(cbLoop.isChecked) }

                btnTime.setOnClickListener { clickMenuPopup() }

                imgPre.setOnClickListener { prevItem() }

                img.setOnClickListener { playMusic() }

                imgBack.setOnClickListener { finish(); setRepeatInterval(-1) }

                imgFavorite.setOnClickListener {
                    startActivity(
                        Intent(
                            this@Show,
                            Favorite::class.java
                        )
                    )
                }

                layoutRefresh.button.setOnClickListener { recreate() }

                imgDowload.setOnClickListener { handlerDownloadClick() }

                cbFavourite.setOnClickListener { handlerCbFavouriteClick(cbFavourite.isChecked) }

                registerForContextMenu(btnTime)

                mRcy.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageScrolled(
                        position: Int, positionOffset: Float, positionOffsetPixels: Int
                    ) {
                        handlePageScrolled(position)
                        binding.txtTime.text=""
                    }
                })
            }
        }
    }


    override fun onResume() {
        super.onResume()
        if (list.size > 0) {
            showPresenter.handlePageScrolled(currentPosition)
        }
    }

    private fun setUpActivity() {
        layoutRefresh = binding.refresh
        binding.mConstraint.isEnabled = false
        apiClientPresenter = ApiClientPresenter()
        showPresenter = ShowPresenter(this, this, apiClientPresenter)
        showPresenter.setUpActivity(intent)
    }

    override fun onBackPressed() {
        val resultIntent = Intent()
        val arrayList = arrayListOf<Int>()
//        arrayList.addAll(listPositionUnchecked)
//        resultIntent.putIntegerArrayListExtra(
//            Constraints.POSITION_FAVORITE_UNCHECKED,
//            arrayList
//        )
//        setResult(Activity.RESULT_OK, resultIntent)
//
        showPresenter.setRepeatInterval(-1)
        finish()
    }

    override fun showMenuPopup() {
        val popupMenu = PopupMenu(this, binding.btnTime)
        val menu = popupMenu.menu
        val list = resources.getStringArray(R.array.time)
        list.forEach {
            menu!!.add(0, 0, 0, it)
        }
        popupMenu.setOnMenuItemClickListener {
            binding.txtTime.text=it.title
            showPresenter.clickItemMenuPopup(list.indexOf(it.title))
            true
        }
        popupMenu.show()
    }

    override fun downLoadSuccess() {
        binding.imgDowload.setBackgroundResource(R.drawable.download_success)
        binding.imgDowload.isEnabled = false
        binding.mProgress1.visibility = View.INVISIBLE
    }


    override fun setAdapter(list: MutableList<DataSound>) {
        val adapter =
            ShowChildSoundAdapter(list, showPresenter.listName, object : ChildSoundClickListens {
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

    override fun onDataImage(mDataImage: DataImage) {
        this.mDataImage = mDataImage
    }

    override fun onPagerScroll() {
        binding.cbLoop.isChecked = false
        binding.mProgress1.visibility = View.INVISIBLE

    }

    override fun setImg(path: String) {
        Utilities.setImage(path, binding.img, this)
    }


    override fun onSuccess(list: List<Any>) {
        binding.mProgress1.visibility = View.INVISIBLE
        binding.mConstraint.isEnabled = true
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

    override fun onDestroy() {
        super.onDestroy()
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

    override fun showCurrentItem(int: Int) {
        currentPosition = int
        binding.mRcy.setCurrentItem(int, false)
    }

     override fun dowLoadFailed(e: String) {
        binding.imgDowload.setBackgroundResource(R.drawable.download_24px)
        binding.imgDowload.isEnabled = true
        binding.mProgress1.visibility = View.INVISIBLE
        Utilities.showSnackBar(binding.root, e)

    }

    override fun isFavorite(boolean: Boolean) {
        binding.cbFavourite.isChecked = boolean
    }

    override fun isDownload(boolean: Boolean, draw: Int) {
        with(binding) {
            imgDowload.setBackgroundResource(draw)
            imgDowload.isEnabled = boolean
        }
    }

    override fun clickDownload() {
        binding.imgDowload.isEnabled = false
        binding.mProgress1.visibility = View.VISIBLE
    }
}