package com.pranksound.fartsound.trollandjoke.funnyapp.contract

interface ShowContract {
    interface MusicPlayerView {
        fun loadSuccess()
        fun load()
        fun loadFailed(e: String)
        fun pauseMusic()
        fun adjustVolume(volume: Float)
        fun setFavorite(isFavorite: Boolean)
        fun setRepeatInterval(intervalSeconds: Int)
        fun showCurrentItem(int: Int)
    }

    interface MusicPlayerPresenter {
        fun nextItem()
        fun prevItem()
        fun getMaxVolume(): Int
        fun playMusic(url: String)
        fun playMusic(raw: Int)
        fun pauseMusic()
        fun adjustVolume(volume: Int)
        fun setFavorite(isFavorite: Boolean)
        fun setRepeatInterval(intervalSeconds: Int)
    }
}