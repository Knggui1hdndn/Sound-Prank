package com.pranksound.fartsound.trollandjoke.funnyapp.presenter

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.pranksound.fartsound.trollandjoke.funnyapp.R
import com.pranksound.fartsound.trollandjoke.funnyapp.contract.ShowContract
import com.pranksound.fartsound.trollandjoke.funnyapp.ui.Utilities
import java.io.InputStream

class ShowPresenter(
    val view: ShowContract.MusicPlayerView,
    val context: Context,
    val listSize: Int
) :
    ShowContract.MusicPlayerPresenter {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var mediaPlayer = MediaPlayer()
    private var currentPosition = 0
    private var isLooping = false
    override fun setLooping(isLooping: Boolean) {
         this.isLooping=isLooping
        Log.d("Nguyenkhang",isLooping.toString())

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
        Log.d("Nguyenkhang",repeatInterval.toString())
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
        mediaPlayer.setDataSource(url)
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
else->{
    view.loadFailed("Không có kết nối mạng")

}
        }
    }

    override fun playMusicOff(raw: AssetFileDescriptor) {
        if (mediaPlayer.isPlaying) mediaPlayer.stop()
        mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource(raw)
        mediaPlayer.start()
    }

    override fun playMusic(url: String) {
       try {
           val check= context.assets.openFd(url)
           playMusicOff(check)
       }catch (e:Exception){
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
        if (!mediaPlayer.isPlaying){
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