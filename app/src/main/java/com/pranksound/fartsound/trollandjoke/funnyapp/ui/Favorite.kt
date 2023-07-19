package com.pranksound.fartsound.trollandjoke.funnyapp.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.pranksound.fartsound.trollandjoke.funnyapp.Constraints
import com.pranksound.fartsound.trollandjoke.funnyapp.FileHandler
import com.pranksound.fartsound.trollandjoke.funnyapp.application.BaseActivity
import com.pranksound.fartsound.trollandjoke.funnyapp.broadcast.ListensChangeNetwork
import com.pranksound.fartsound.trollandjoke.funnyapp.contract.ApiClientContract
import com.pranksound.fartsound.trollandjoke.funnyapp.databinding.ActivityFavoriteBinding
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataSound
import com.pranksound.fartsound.trollandjoke.funnyapp.presenter.ApiClientPresenter
import com.pranksound.fartsound.trollandjoke.funnyapp.ui.adapter.ChildSoundClickListens
import com.pranksound.fartsound.trollandjoke.funnyapp.ui.adapter.SoundChildFavoriteAdapter


class Favorite : BaseActivity(), ChildSoundClickListens {
    private lateinit var favorite: ActivityFavoriteBinding
    private lateinit var adapterFavorite: SoundChildFavoriteAdapter
    private lateinit var listSound: MutableList<DataSound>
    private var listNameSound: ArrayList<String> = arrayListOf()
    private var listOnl: List<DataSound> = arrayListOf()
    private var listOff: List<DataSound> = arrayListOf()
    private var listPosition: ArrayList<Int> = arrayListOf()
    private var checkNetwork: Boolean = false
    val combinedList = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        favorite = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(favorite.root)
        listSound = mutableListOf()
        setAdapter()
        val apiClient = ApiClientPresenter()
        favorite.imgBack.setOnClickListener {
            finish()
        }
        showBannerAds(favorite.bannerAds)
        showBannerAds(favorite.bannerAds2)

        val favoriteOnlList = FileHandler.getFavoriteOnl(this@Favorite)
        val favoriteOffList = FileHandler.getFavoriteOff(this@Favorite)

        listOnl = favoriteOnlList.map { it.second }
        listOff = favoriteOffList.map { it.second }

        if (ListensChangeNetwork.isConnectNetwork == Constraints.CONNECTION_NETWORK) {
            listNameSound.addAll(favoriteOffList.map { it.first.name } + favoriteOnlList.map { it.first.name })
            listPosition.addAll(favoriteOffList.map { it.third } + favoriteOnlList.map { it.third })

        } else {
            listNameSound.addAll(favoriteOffList.map { it.first.name })
            listPosition.addAll(favoriteOffList.map { it.third })
        }

        for (i in 0 until minOf(listNameSound.size, listPosition.size)) {
            val elementA = listPosition[i].toString()
            val elementB = listNameSound[i]
            val combinedElement = "$elementB $elementA"
            combinedList.add(combinedElement)
        }
        apiClient.getListParentSound(object : ApiClientContract.Listens {
            override fun onSuccess(list: List<Any>) {
                handleSuccess(list, listOnl, listOff)
            }

            override fun onFailed(e: String) {
                handleFailure(listOff)
            }
        })
    }

    private fun handleSuccess(
        list: List<Any>,
        favoriteOnlList: List<DataSound>,
        favoriteOffList: List<DataSound>
    ) {
        checkNetwork = true
        ListensChangeNetwork.isConnectNetwork = Constraints.CONNECTION_NETWORK
        favorite.mProgress.visibility = View.GONE
        listSound.addAll(listOff)
        listSound.addAll(listOnl)
        setAdapter()
    }

    private fun handleFailure(favoriteOffList: List<DataSound>) {
        checkNetwork = false
        ListensChangeNetwork.isConnectNetwork = Constraints.DISCONNECT_NETWORK
        favorite.mProgress.visibility = View.GONE
        listSound.addAll(listOff)
        setAdapter()
    }

    private val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data!!.getIntegerArrayListExtra(Constraints.POSITION_FAVORITE_UNCHECKED)
                ?.forEach {
                    listSound.removeAt(it)
                }
            adapterFavorite.setData(listSound)
        }
    }

    private fun setAdapter() {
        adapterFavorite = SoundChildFavoriteAdapter(listSound, combinedList, this)
        favorite.mRcy.apply {
            layoutManager = GridLayoutManager(this@Favorite, 2)
            adapter = adapterFavorite
        }
    }

    override fun itemClick(position: Int) {
        Log.d("dsssssssssssssss", position.toString())
        val intent = Intent(this, Show::class.java)
        intent.putExtra(Constraints.ACTIVITY_LAUNCH, "Favorite")
        intent.putExtra("sound", listSound[position].source)
        intent.putExtra("checkNetwork", checkNetwork)
        intent.putStringArrayListExtra("listName", combinedList)
        intent.putExtra(Constraints.SOUND_CHILD_CLICK, position)
        startForResult.launch(intent)
    }
}