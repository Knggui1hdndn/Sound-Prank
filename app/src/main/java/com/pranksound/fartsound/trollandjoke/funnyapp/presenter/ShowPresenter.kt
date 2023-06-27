package com.pranksound.fartsound.trollandjoke.funnyapp.presenter

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.pranksound.fartsound.trollandjoke.funnyapp.contract.ShowContract

class ShowPresenter(val view: ShowContract.MusicPlayerView, val context: Context,val listSize:Int) :
    ShowContract.MusicPlayerPresenter {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var mediaPlayer = MediaPlayer()
    private var currentPosition = 0
    override fun nextItem() {
        currentPosition++
        if (currentPosition >= listSize) {
            currentPosition = 0
        }
    }

    override fun prevItem() {
        currentPosition--
        if (currentPosition < 0) {
            currentPosition = listSize - 1
        }
    }

    override fun getMaxVolume(): Int {
        return audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
    }

    override fun playMusic(url: String) {
        mediaPlayer = MediaPlayer()
        view.load()
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            mediaPlayer.start()
            view.loadSuccess()
        }
        mediaPlayer.setOnErrorListener { mp, what, extra ->
            when (what) {
                MediaPlayer.MEDIA_ERROR_UNKNOWN -> {
                    // Xử lý lỗi không xác định
                    view.loadFailed("Unknown error occurred: $extra")
                }

                MediaPlayer.MEDIA_ERROR_IO -> {
                    // Xử lý lỗi đọc/ghi tệp tin hoặc luồng
                    view.loadFailed("IO error occurred: $extra")

                }

                MediaPlayer.MEDIA_ERROR_MALFORMED -> {
                    // Xử lý lỗi định dạng không hợp lệ
                    view.loadFailed("Malformed media error occurred: $extra")
                }

                MediaPlayer.MEDIA_ERROR_TIMED_OUT -> {
                    // Xử lý lỗi quá thời gian chờ
                    view.loadFailed("Timeout error occurred: $extra")
                }

            }
            false
        }
    }

    override fun playMusic(raw: Int) {
        mediaPlayer = MediaPlayer.create(context, raw)

        mediaPlayer.start()
    }


    override fun pauseMusic() {

    }

    override fun adjustVolume(volume: Int) {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0)
    }

    override fun setFavorite(isFavorite: Boolean) {

    }

    override fun setRepeatInterval(intervalSeconds: Int) {
        mediaPlayer.setOnCompletionListener { mp ->
            // Khi phát nhạc hoàn thành, thực hiện lặp lại sau một khoảng thời gian
            val delayMillis = 5000 // Khoảng thời gian lặp lại (đơn vị: milliseconds)
            Handler(Looper.getMainLooper()).postDelayed({
                // Kiểm tra nếu MediaPlayer đang chạy
                if (mediaPlayer.isPlaying) {
                    // Thiết lập lại MediaPlayer để phát lại từ đầu
                    mediaPlayer.seekTo(0)
                    mediaPlayer.start()
                }
            }, delayMillis.toLong())
        }
    }
}