package com.pranksound.fartsound.trollandjoke.funnyapp.contract

import android.content.Intent
import android.content.res.AssetFileDescriptor
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataImage
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataSound

interface ShowContract {
    interface ShowView {
        fun loadSuccess()
        fun load()
        fun loadFailed(e: String)
        fun showCurrentItem(int: Int)
        fun showMenuPopup()
        fun downLoadSuccess()
        fun dowLoadFailed(e:String)
        fun isFavorite(boolean: Boolean)
        fun isDownload(boolean: Boolean, draw: Int )
        fun clickDownload()
        fun setAdapter(list:  MutableList<DataSound>)
        fun onDataImage(mDataImage: DataImage)
        fun onPagerScroll()
        fun setImg(path:String)
    }

    interface ShowPresenter {
        fun setUpNormalActivity(intent: Intent )
        fun setUpFavoriteActivity(intent: Intent)
         fun handlerCbFavouriteClick(isChecked: Boolean)
        fun handlerDownloadClick( )
        fun handlePageScrolled(position: Int)
        fun clickMenuPopup()
        fun clickItemMenuPopup(position: Int)
        fun nextItem()
        fun prevItem()

        fun getDataImgFavorite(  source: String): DataImage
        fun getPositionSound( source: String): Int
        fun getFavorite( source: String): Triple<DataImage, DataSound, Int>
        fun isFavorite(sound: String): Boolean
        fun downLoad(mImg: DataImage, mSound: DataSound, position: Int)
        fun setLooping(isLooping: Boolean)

        fun getMaxVolume(): Int
        fun playMusicOnl(url: String)
        fun playMusicOff(raw: AssetFileDescriptor)
        fun playMusic()
        fun getCurrentVolume(): Int

        fun pauseMusic()
        fun adjustVolume(volume: Int)
        fun setFavorite(isFavorite: Boolean)
        fun setRepeatInterval(intervalSeconds: Int)
        fun handleFavoriteChecked(
            isChecked: Boolean,
            dataSound: DataSound,
            mDataImage: DataImage,
             position: Int
        )

         fun setSoundWhenSuccess()
        fun checkDownLoad(nameParentSound: String, pathSound: String, position: Int)

    }
}