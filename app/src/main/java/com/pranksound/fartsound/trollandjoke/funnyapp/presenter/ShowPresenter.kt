package com.pranksound.fartsound.trollandjoke.funnyapp.presenter

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.core.net.toUri
import com.pranksound.fartsound.trollandjoke.funnyapp.FileHandler
import com.pranksound.fartsound.trollandjoke.funnyapp.R
import com.pranksound.fartsound.trollandjoke.funnyapp.contract.ShowContract
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataImage
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataSound
import java.io.IOException


class ShowPresenter(
    val view: ShowContract.MusicPlayerView,
    val context: Context,
    val listSize: Int,
    val apiClientPresenter: ApiClientPresenter
) :
    ShowContract.MusicPlayerPresenter {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var mediaPlayer = MediaPlayer()
    var currentPosition = 0
    private var isLooping = false
    override fun checkFavorite(stateNetWork: String, sound: String) {
        var isFavoriteOnl = FileHandler.getFavoriteOnl(context).any { it.second.source == sound }
        var isFavoriteOff = FileHandler.getFavoriteOff(context).any { it.second.source == sound }

        view.isFavorite(isFavoriteOff || isFavoriteOnl )
    }



    override fun checkDownLoad(nameParentSound: String,pathSound: String) {
        FileHandler.checkFileExists(context, nameParentSound,pathSound ,currentPosition).let {
            if (!it) {
                view.isDownload(true, R.drawable.baseline_cloud_download_24)
            } else {
                 view.isDownload(false, R.drawable.baseline_cloud_done_24)
            }
        }

    }

    override fun downLoad(mImg: DataImage, mSound: DataSound) {
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
                                        mImg.name + currentPosition
                                    )
                                    FileHandler.saveFileToAppDirectory(
                                        sound, mImg.name, mImg.name + currentPosition, context
                                    )
                                    view.downLoadSuccess()
                                } else {
                                    view.dowLoadFailed("Kiểm tra mạng")
                                }
                            }
                        } else {
                            view.dowLoadFailed("Kiểm tra mạng")
                        }
                    }
                } else {
                    view.dowLoadFailed("Kiểm tra mạng")
                }
            }
        } catch (e: Exception) {
            view.dowLoadFailed("Kiểm tra mạng" + e.toString())
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
        mediaPlayer.setOnErrorListener { mp, what, extra ->
            setError(what)
            false
        }
    }

    private fun setError(what: Int) {
        when (what) {
            MediaPlayer.MEDIA_ERROR_UNKNOWN -> {
                // Xử lý lỗi không xác định
                view.loadFailed("Unknown error occurred")
            }

            MediaPlayer.MEDIA_ERROR_IO -> {
                // Xử lý lỗi đọc/ghi tệp tin hoặc luồng
                view.loadFailed("IO error occurred")

            }

            MediaPlayer.MEDIA_ERROR_MALFORMED -> {
                // Xử lý lỗi định dạng không hợp lệ
                view.loadFailed("Malformed media error occurred")
            }

            MediaPlayer.MEDIA_ERROR_TIMED_OUT -> {
                // Xử lý lỗi quá thời gian chờ
                view.loadFailed("Timeout error occurred")
            }

            else -> {
                view.loadFailed("Không có kết nối mạng")

            }
        }
    }

    override fun playMusicOff(raw: AssetFileDescriptor) {

        if (mediaPlayer.isPlaying) mediaPlayer.stop()
        mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource(raw)
        mediaPlayer.prepare();
        mediaPlayer.start()
    }

    override fun playMusic(url: String) {
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
        if (!mediaPlayer.isPlaying) {
            Handler(Looper.getMainLooper()).postDelayed({
                mediaPlayer.seekTo(0)
                mediaPlayer.start()
            }, intervalSeconds.toLong())
        }
        mediaPlayer.setOnCompletionListener { mp ->
            if (isLooping) {
                Handler(Looper.getMainLooper()).postDelayed({
                    mediaPlayer.seekTo(0)
                    mediaPlayer.start()
                }, intervalSeconds.toLong())
            }
        }
    }
}