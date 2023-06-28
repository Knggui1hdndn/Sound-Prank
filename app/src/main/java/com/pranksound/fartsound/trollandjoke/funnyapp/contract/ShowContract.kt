package com.pranksound.fartsound.trollandjoke.funnyapp.contract

import android.content.res.AssetFileDescriptor

interface ShowContract {
    interface MusicPlayerView {
        fun loadSuccess()
        fun load()
        fun loadFailed(e: String)
        fun showCurrentItem(int: Int)
        fun showMenuPopup()

    }

    interface MusicPlayerPresenter {
        fun setLooping(isLooping:Boolean)
        fun clickMenuPopup()
        fun clickItemMenuPopup(position:Int)
        fun nextItem()
        fun prevItem()
        fun getMaxVolume(): Int
        fun playMusicOnl(url: String)
        fun playMusicOff(raw: AssetFileDescriptor)
        fun playMusic (url: String)
        fun getCurrentVolume():Int

        fun pauseMusic()
        fun adjustVolume(volume: Int)
        fun setFavorite(isFavorite: Boolean)
        fun setRepeatInterval(intervalSeconds: Int)
    }
}