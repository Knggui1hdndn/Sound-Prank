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
import com.pranksound.fartsound.trollandjoke.funnyapp.broadcast.ListensChangeNetwork
import com.pranksound.fartsound.trollandjoke.funnyapp.contract.ApiClientContract
import com.pranksound.fartsound.trollandjoke.funnyapp.databinding.ActivityFavoriteBinding
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataSound
import com.pranksound.fartsound.trollandjoke.funnyapp.presenter.ApiClientPresenter
import com.pranksound.fartsound.trollandjoke.funnyapp.ui.adapter.ChildSoundAdapter
import com.pranksound.fartsound.trollandjoke.funnyapp.ui.adapter.ChildSoundClickListens


class Favorite : AppCompatActivity(), ChildSoundClickListens {
    private lateinit var favorite: ActivityFavoriteBinding
    private lateinit var adapterFavorite: ChildSoundAdapter
    private lateinit var listSound: MutableList<DataSound>
    private   var checkNetwork: Boolean=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        favorite = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(favorite.root)
        listSound = mutableListOf()
        setAdapter()
        val apiClient = ApiClientPresenter()
        apiClient.getListParentSound(object : ApiClientContract.Listens {
            override fun onSuccess(list: List<Any>) {
                checkNetwork=true
                ListensChangeNetwork.isConnectNetwork=Constraints.CONNECTION_NETWORK
                favorite.mProgress.visibility=View.GONE
                listSound.addAll(FileHandler.getFavoriteOnl(this@Favorite).map { it.second })
                listSound.addAll(FileHandler.getFavoriteOff(this@Favorite).map { it.second })
                setAdapter()
            }

            override fun onFailed(e: String) {
                checkNetwork = false
                ListensChangeNetwork.isConnectNetwork = Constraints.DISCONNECT_NETWORK
                favorite.mProgress.visibility = View.GONE
                listSound.addAll(FileHandler.getFavoriteOff(this@Favorite).map { it.second })
                setAdapter()
            }

        })
    }

    private val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data!!.getIntegerArrayListExtra(Constraints.POSITION_FAVORITE_UNCHECKED)?.forEach {
                listSound.removeAt(it)
            }
            adapterFavorite.setData(listSound)
        }
    }

    private fun setAdapter() {
        adapterFavorite = ChildSoundAdapter(listSound, this)
        favorite.mRcy.apply {
            layoutManager = GridLayoutManager(this@Favorite, 2)
            adapter = adapterFavorite
        }
    }

    override fun itemClick(position: Int) {
        val intent = Intent(this, Show::class.java)
        intent.putExtra(Constraints.ACTIVITY_LAUNCH, "Favorite")
        intent.putExtra("sound", listSound[position].source)
        intent.putExtra("checkNetwork", checkNetwork)
        intent.putExtra(Constraints.SOUND_CHILD_CLICK, position)
        startForResult.launch(intent)
    }
}