package com.pranksound.fartsound.trollandjoke.funnyapp.presenter

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.net.toUri
import com.pranksound.fartsound.trollandjoke.funnyapp.Constraints
import com.pranksound.fartsound.trollandjoke.funnyapp.FileHandler
import com.pranksound.fartsound.trollandjoke.funnyapp.R
import com.pranksound.fartsound.trollandjoke.funnyapp.broadcast.ListensChangeNetwork
import com.pranksound.fartsound.trollandjoke.funnyapp.contract.ShowContract
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataImage
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataSound
import java.io.File


class ShowPresenter(
    private val view: ShowContract.ShowView,
    val context: Context,
    private val listSize: Int,
    private val apiClientPresenter: ApiClientPresenter,
) :
    ShowContract.ShowPresenter {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var mediaPlayer = MediaPlayer()
    var currentPosition = 0
    private var isDownload = false
    private var isLooping = false
    override fun handleFavoriteChecked(
        isChecked: Boolean,
        dataSound: DataSound,
        mDataImage: DataImage,
        position: Int
    ) {
        with(FileHandler) {
            if (isDownload) {
                if (isChecked)
                    saveFavoriteOff(dataSound, mDataImage, context, position)
                else
                    removeFavoriteOff(context, dataSound)
            } else {
                if (isChecked)
                    saveFavoriteOnl(dataSound, mDataImage, context, position)
                else
                    removeFavoriteOnl(context, dataSound)
            }
        }
    }

    override fun getDataImgFavorite(source: String): DataImage {
        return getFavorite(source).first
    }

    override fun getPositionSound(source: String): Int {
        return getFavorite(source).third
    }

    override fun getFavorite(source: String): Triple<DataImage, DataSound, Int> {
        val sizeFavoriteOff = FileHandler.getFavoriteOff(context).size
        val sizeFavoriteOnl = FileHandler.getFavoriteOnl(context).size
        return if (isFavorite(source) && ListensChangeNetwork.isConnectNetwork != Constraints.CONNECTION_NETWORK) {
            try {
                FileHandler.getFavoriteOff(context)[currentPosition]
            } catch (e: Exception) {
                FileHandler.getFavoriteOnl(context)[currentPosition]
            }
        } else {
            if (sizeFavoriteOnl == 0) {
                FileHandler.getFavoriteOff(context)[currentPosition]
            }
            if (sizeFavoriteOff == 0) {
                FileHandler.getFavoriteOnl(context)[currentPosition]
            }
            if (currentPosition >= sizeFavoriteOnl) {
                FileHandler.getFavoriteOff(context)[currentPosition - sizeFavoriteOnl]
            } else {
                FileHandler.getFavoriteOnl(context)[currentPosition]
            }

        }
    }

    override fun isFavorite(sound: String): Boolean {
        val isFavoriteOnl = FileHandler.getFavoriteOnl(context).any { it.second.source == sound }
        val isFavoriteOff = FileHandler.getFavoriteOff(context).any { it.second.source == sound }

        view.isFavorite(isFavoriteOff || isFavoriteOnl)
        return isFavoriteOff || isFavoriteOnl
    }


    override fun checkDownLoad(nameParentSound: String, pathSound: String, position: Int) {
        FileHandler.checkFileExists(context, nameParentSound, pathSound, position) { s, b ->
            isDownload = b
            Log.d("sodggdggdsdfgg", isDownload.toString())

            if (!b) {
                view.isDownload(true, R.drawable.download_24px, s, position)
            } else {
                view.isDownload(false, R.drawable.baseline_cloud_done_24, s, position)
            }
        }
    }

    override fun downLoad(mImg: DataImage, mSound: DataSound, position: Int) {
        try {
            apiClientPresenter.downloadStream(mSound.source) { sound ->
                if (sound != null) {
                    apiClientPresenter.downloadStream(mImg.icon) { imgParent ->
                        if (imgParent != null) {
                            apiClientPresenter.downloadStream(mSound.image) { imgChild ->
                                if (imgChild != null) {
                                    val bitmapImgSound = BitmapFactory.decodeStream(imgParent)
                                    val bitmapImgImage = BitmapFactory.decodeStream(imgChild)
                                    FileHandler.saveImgParentToAppDirectory(
                                        context, bitmapImgSound, mImg.name
                                    )
                                    FileHandler.saveImgToAppDirectory(
                                        context,
                                        bitmapImgImage,
                                        mImg.name,
                                        mImg.name + position
                                    )
                                    FileHandler.saveFileToAppDirectory(
                                        sound, mImg.name, mImg.name + position, context
                                    )
                                    view.downLoadSuccess()
                                } else {
                                    view.dowLoadFailed(context.getString(R.string.please_check_network))
                                }
                            }
                        } else {
                            view.dowLoadFailed(context.getString(R.string.please_check_network))
                        }
                    }
                } else {
                    view.dowLoadFailed(context.getString(R.string.please_check_network))
                }
            }
        } catch (e: Exception) {
            view.dowLoadFailed(context.getString(R.string.please_check_network))
        }
    }

    override fun setLooping(isLooping: Boolean) {
        this.isLooping = isLooping

    }

    override fun clickMenuPopup() {
        view.showMenuPopup()
    }

    override fun clickItemMenuPopup(position: Int) {
        val list = context.resources.getStringArray(R.array.time)
        if (position==-1){ setRepeatInterval(-1);return}
        val item = list[position]
        val repeatInterval = when {
            item.contains("s") -> item.split("s")[0].toInt() * 1000
            item.contains("m") -> item.split("m")[0].toInt() * 60000
            else -> {
                0
            }
        }
        setRepeatInterval(repeatInterval)
    }

    override fun nextItem() {
        currentPosition++
        if (currentPosition >= listSize) {
            currentPosition = 0
        }
        view.showCurrentItem(currentPosition)
    }

    override fun prevItem() {
        currentPosition--
        if (currentPosition < 0) {
            currentPosition = listSize - 1
        }
        view.showCurrentItem(currentPosition)

    }

    override fun getMaxVolume(): Int {
        return audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
    }

    override fun playMusicOnl(url: String) {
        if (mediaPlayer.isPlaying) mediaPlayer.stop()
        mediaPlayer = MediaPlayer()
        view.load()
        mediaPlayer.setDataSource(context, url.toUri())
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            mediaPlayer.start()
            view.loadSuccess()
        }
        mediaPlayer.setOnErrorListener { mp, what, _ ->
            setError(what)

            false
        }
    }

    private fun setError(what: Int) {
//        when (what) {
//            MediaPlayer.MEDIA_ERROR_UNKNOWN -> {
//                // Xử lý lỗi không xác định
//                view.loadFailed("Unknown error occurred")
//            }
//
//            MediaPlayer.MEDIA_ERROR_IO -> {
//                // Xử lý lỗi đọc/ghi tệp tin hoặc luồng
//                view.loadFailed("IO error occurred")
//
//            }
//
//            MediaPlayer.MEDIA_ERROR_MALFORMED -> {
//                // Xử lý lỗi định dạng không hợp lệ
//                view.loadFailed("Malformed media error occurred")
//            }
//
//            MediaPlayer.MEDIA_ERROR_TIMED_OUT -> {
//                // Xử lý lỗi quá thời gian chờ
//                view.loadFailed("Timeout error occurred")
//            }
//
//            else -> {
//                view.loadFailed("Không có kết nối mạng")
//
//            }
//        }
        view.loadFailed(context.getString(R.string.please_check_network))
    }

    override fun playMusicOff(raw: AssetFileDescriptor) {
        if (mediaPlayer.isPlaying) mediaPlayer.stop()
        mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource(raw)
        mediaPlayer.prepare()
        mediaPlayer.start()
    }

    override fun playMusic(url: String) {
        Handler(Looper.myLooper()!!).postDelayed({
            if (!mediaPlayer.isPlaying) view.loadFailed(context.getString(R.string.please_check_network))
        }, 2000)
        try {
            val check = context.assets.openFd(url)
            playMusicOff(check)
        } catch (e: Exception) {
            playMusicOnl(url)
        }

    }

    override fun getCurrentVolume(): Int {
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
    }

    override fun pauseMusic() {
        if (mediaPlayer.isPlaying) mediaPlayer.pause()

    }

    override fun adjustVolume(volume: Int) {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0)
    }

    override fun setFavorite(isFavorite: Boolean) {

    }

    override fun setRepeatInterval(intervalSeconds: Int) {
        val run = Runnable {
            mediaPlayer.seekTo(0)
            mediaPlayer.start()
        }
        val handel = Handler(Looper.getMainLooper())
        if (!mediaPlayer.isPlaying) {
            handlerInterval(handel, run, intervalSeconds)
        }
        mediaPlayer.setOnCompletionListener {
            handlerInterval(handel, run, intervalSeconds)
        }
    }

    private fun handlerInterval(handel: Handler, run: Runnable, intervalSeconds: Int) {
        if (intervalSeconds == -1) {
            handel.removeCallbacks(run)
        } else {
            handel.postDelayed(run, intervalSeconds.toLong())
        }
    }
}