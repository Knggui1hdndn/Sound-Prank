package com.pranksound.fartsound.trollandjoke.funnyapp.ui

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.pranksound.fartsound.trollandjoke.funnyapp.Constraints
import com.pranksound.fartsound.trollandjoke.funnyapp.R
import com.pranksound.fartsound.trollandjoke.funnyapp.contract.ApiClientContract
import com.pranksound.fartsound.trollandjoke.funnyapp.contract.ShowContract
import com.pranksound.fartsound.trollandjoke.funnyapp.databinding.ActivityShowBinding
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataImage
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataSound
import com.pranksound.fartsound.trollandjoke.funnyapp.presenter.ApiClientPresenter
import com.pranksound.fartsound.trollandjoke.funnyapp.presenter.ShowPresenter
import com.pranksound.fartsound.trollandjoke.funnyapp.ui.adapter.OffOrHotAdapter
import com.pranksound.fartsound.trollandjoke.funnyapp.ui.adapter.OffOrHotAdapterListens

class Show : AppCompatActivity(), ApiClientContract.Listens, OffOrHotAdapterListens,
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
    private var currentPosition = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val presenter = ApiClientPresenter()
        val mDataImage = getDataImage()
        showPresenter = ShowPresenter(this, this, 0)

        presenter.getListChildSound(mDataImage.id, this)
        with(binding) {
            seekBar.max = showPresenter.getMaxVolume()
            seekBar.setOnSeekBarChangeListener(this@Show)
            btnNext.setOnClickListener { showPresenter.nextItem() }

            btnPre.setOnClickListener { showPresenter.prevItem() }

            mRcy.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrolled(
                    position: Int, positionOffset: Float, positionOffsetPixels: Int
                ) {
                    currentPosition = position
                    setImage()
                }
            })
        }

    }


    private fun setImage() {
        val url = this.list[currentPosition].image
        Utilities.loadImg(url, binding.img)
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
        showPresenter = ShowPresenter(this, this, this.list.size)
        setImage()
        setAdapter()
    }

    private fun setAdapter() {
        val adapter = OffOrHotAdapter(this.list, Constraints.VIEW_TYPE_HOT, this)
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

    }

    override fun itemClick(bitmap: Bitmap, linkSound: String, checkFirst: Boolean) {
        binding.img.setImageBitmap(bitmap)

    }

    override fun loadSuccess() {
        TODO("Not yet implemented")
    }

    override fun load() {
        TODO("Not yet implemented")
    }

    override fun loadFailed(e: String) {
        TODO("Not yet implemented")
    }

    override fun pauseMusic() {
        TODO("Not yet implemented")
    }

    override fun adjustVolume(volume: Float) {
        TODO("Not yet implemented")
    }

    override fun setFavorite(isFavorite: Boolean) {
        TODO("Not yet implemented")
    }

    override fun setRepeatInterval(intervalSeconds: Int) {
        TODO("Not yet implemented")
    }

    override fun showCurrentItem(int: Int) {
        binding.mRcy.setCurrentItem(currentPosition, false)
        setImage()
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        resources.getStringArray(R.array.time).forEach {
            menu!!.add(0, v!!.id, 0, it)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val title = item.title
        showPresenter.setRepeatInterval(title!!.filter { it.isDigit() }.toString().toInt())

        return true
    }


}