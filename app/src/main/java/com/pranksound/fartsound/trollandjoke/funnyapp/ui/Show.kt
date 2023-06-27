package com.pranksound.fartsound.trollandjoke.funnyapp.ui

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.ORIENTATION_HORIZONTAL
import androidx.viewpager2.widget.ViewPager2.Orientation
import com.pranksound.fartsound.trollandjoke.funnyapp.Constraints
import com.pranksound.fartsound.trollandjoke.funnyapp.contract.ApiClientContract
import com.pranksound.fartsound.trollandjoke.funnyapp.databinding.ActivityShowBinding
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataImage
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataSound
import com.pranksound.fartsound.trollandjoke.funnyapp.presenter.ApiClientPresenter
import com.pranksound.fartsound.trollandjoke.funnyapp.ui.adapter.OffOrHotAdapter
import com.pranksound.fartsound.trollandjoke.funnyapp.ui.adapter.OffOrHotAdapterListens
import com.squareup.picasso.Picasso
import java.security.AccessController.getContext


class Show : AppCompatActivity(), ApiClientContract.Listens, OffOrHotAdapterListens {
    private lateinit var list: List<DataSound>
    private lateinit var binding: ActivityShowBinding
    private var currentPosition = 0
    private var check = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val presenter = ApiClientPresenter()
        val mDataImage = getDataImage()

        if (mDataImage != null) {
            presenter.getListChildSound(mDataImage.id, this)

        }
        binding.btnNext.setOnClickListener {
            currentPosition++
            if (currentPosition >= list.size) {
                currentPosition = 0
            }

            binding.mRcy.setCurrentItem(currentPosition, false)
            Picasso.get().load(this.list[currentPosition].image).into(binding.img)

        }
        binding.btnPre.setOnClickListener {
            currentPosition--
            if (currentPosition < 0) {
                currentPosition = list.size - 1
            }

            binding.mRcy.setCurrentItem(currentPosition, false)
            Picasso.get().load(this.list[currentPosition].image).into(binding.img)

        }

        binding.mRcy.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int, positionOffset: Float, positionOffsetPixels: Int
            ) {
                Picasso.get().load(list[position].image).into(binding.img)
            }
        })
    }

    private fun getDataImage(): DataImage? {
        val intent = intent
        currentPosition = intent.getIntExtra(Constraints.SOUND_CHILD_CLICK, 0)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(Constraints.PARENT_SOUND, DataImage::class.java)
        } else {
            intent.getSerializableExtra(Constraints.PARENT_SOUND) as DataImage
        }
    }

    override fun onSuccess(list: List<Any>) {
        this.list = list as List<DataSound>
        Picasso.get().load(this.list[currentPosition].image).into(binding.img)
        val adapter = OffOrHotAdapter(this.list, Constraints.VIEW_TYPE_HOT, this)
        binding.mRcy.apply {
            val comPosite = CompositePageTransformer()
            comPosite.addTransformer(MarginPageTransformer(30))
            offscreenPageLimit = 3
            clipToPadding = false
            clipChildren = false
            setPageTransformer(comPosite)
            this.adapter = adapter
            binding.mRcy.setCurrentItem(currentPosition, true)
        }
    }

    override fun onFailed(e: String) {

    }

    override fun itemClick(bitmap: Bitmap, linkSound: String, checkFirst: Boolean) {
        binding.img.setImageBitmap(bitmap)

    }
}