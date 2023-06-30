package com.pranksound.fartsound.trollandjoke.funnyapp.ui

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.pranksound.fartsound.trollandjoke.funnyapp.Constraints
import com.pranksound.fartsound.trollandjoke.funnyapp.FileHandler
import com.pranksound.fartsound.trollandjoke.funnyapp.broadcast.ListenNetwork
import com.pranksound.fartsound.trollandjoke.funnyapp.broadcast.ListensChangeNetwork
import com.pranksound.fartsound.trollandjoke.funnyapp.databinding.ActivityFavoriteBinding
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataSound
import com.pranksound.fartsound.trollandjoke.funnyapp.ui.adapter.ChildSoundAdapter
import com.pranksound.fartsound.trollandjoke.funnyapp.ui.adapter.ChildSoundClickListens
import java.util.regex.Pattern

class Favorite : AppCompatActivity(), ListenNetwork, ChildSoundClickListens {
    private lateinit var favorite: ActivityFavoriteBinding
    private lateinit var intentFilter: IntentFilter
    private lateinit var listensChangeNetwork: ListensChangeNetwork
    private lateinit var adapterFavorite: ChildSoundAdapter
    private lateinit var listSound: MutableList<DataSound>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        favorite = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(favorite.root)
        listSound = mutableListOf()
        intentFilter = IntentFilter(Constraints.CONNECTIVITY_CHANGE)
        listensChangeNetwork = ListensChangeNetwork(this)
        registerReceiver(listensChangeNetwork, intentFilter)
    }


    override fun onChangeNetwork(string: String) {
        listSound.clear()
        setAdapter()
    }

    private fun setAdapter() {
        adapterFavorite =
            if (ListensChangeNetwork.isConnectNetwork == Constraints.CONNECTION_NETWORK) {
                listSound.addAll(FileHandler.getFavoriteOnl(this))
                listSound.addAll(FileHandler.getFavoriteOff(this))
                ChildSoundAdapter(listSound, this)
            } else {
                listSound.addAll(FileHandler.getFavoriteOff(this))
                ChildSoundAdapter(listSound, this)
            }
        favorite.mRcy.apply {
            layoutManager = GridLayoutManager(this@Favorite, 2)
            adapter = adapterFavorite
        }
    }

    override fun itemClick(position: Int) {
        val intent = Intent(this, Show::class.java)
        intent.putExtra(Constraints.ACTIVITY_LAUNCH, "Favorite")
        intent.putExtra("sound", listSound[position].source)
        intent.putExtra(Constraints.SOUND_CHILD_CLICK,position)
        startActivity(intent)
    }
}