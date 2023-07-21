package com.pranksound.fartsound.trollandjoke.funnyapp.presenter

import android.content.Context
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.core.net.toUri
import com.pranksound.fartsound.trollandjoke.funnyapp.Constraints
import com.pranksound.fartsound.trollandjoke.funnyapp.FileHandler
import com.pranksound.fartsound.trollandjoke.funnyapp.R
import com.pranksound.fartsound.trollandjoke.funnyapp.broadcast.ListensChangeNetwork
import com.pranksound.fartsound.trollandjoke.funnyapp.contract.ApiClientContract
import com.pranksound.fartsound.trollandjoke.funnyapp.contract.ShowContract
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataImage
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataSound
import com.pranksound.fartsound.trollandjoke.funnyapp.ui.Show
import com.pranksound.fartsound.trollandjoke.funnyapp.ui.adapter.SoundParentAdapter


class ShowPresenter(
    private val view: ShowContract.ShowView,
    val context: Context,
    private val apiClientPresenter: ApiClientPresenter,
) : ShowContract.ShowPresenter {
    private val listPositionUnchecked = mutableSetOf<Int>()
    private var listSoundChild = mutableListOf<DataSound>()
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var mediaPlayer = MediaPlayer()
    private var currentPosition = 0
    private var isDownload = false
    private var isLooping = false
    private var isRepeatInterval = false
    private var isStart = false
    private lateinit var itemDataSound: DataSound
    private var source: String = ""
    private lateinit var mDataImage: DataImage
    private var callingActivity = ""
    private var isCallingActivity = false
    private lateinit var intent: Intent
    private var listSize: Int = 0
    private var checkNetWork = false
    private var repeatInterval = 0
    var listName = arrayListOf<String>()
    fun setUpActivity(intent: Intent) {
        checkNetWork = ListensChangeNetwork.isConnectNetwork == Constraints.CONNECTION_NETWORK
        this.intent = intent
        val callingActivity = intent.getStringExtra(Constraints.ACTIVITY_LAUNCH).toString()
        currentPosition = intent.getIntExtra(Constraints.SOUND_CHILD_CLICK, 0)
        isCallingActivity = callingActivity == "Favorite"
        if (isCallingActivity) {
            listName = intent.getStringArrayListExtra("listName")!!
            setUpFavoriteActivity(intent)
        } else {
            setUpNormalActivity(intent)
        }
        view.showCurrentItem(currentPosition)
        view.onDataImage(mDataImage)
        listSize = listSoundChild.size
    }

    override fun setUpFavoriteActivity(intent: Intent) {
        checkNetWork = intent.getBooleanExtra("checkNetwork", false)
        val listFavoriteOff = FileHandler.getFavoriteOff(context)
        val listFavoriteOnl = FileHandler.getFavoriteOnl(context)
        if (!checkNetWork) {
            listSoundChild.addAll(listFavoriteOff.map { it.second })
        } else {
            listSoundChild.addAll(listFavoriteOff.map { it.second })
            listSoundChild.addAll(listFavoriteOnl.map { it.second })
        }
        val soundChild = listSoundChild[currentPosition].source
        mDataImage = getDataImgFavorite(soundChild)
        view.setAdapter(listSoundChild)
    }

    override fun setUpNormalActivity(intent: Intent) {
        mDataImage = getDataImage()
        view.onDataImage(mDataImage)
        if (!checkNetWork && SoundParentAdapter.SIZE < 50) {
            setUpListDisconnected()
            view.setAdapter(listSoundChild)
        } else {
            setUpListConnected()
        }
    }

    private fun setUpListDisconnected() {
        try {
            listSoundChild = try {
                FileHandler.getFileAssetByParentSound(context, mDataImage.name).toMutableList()
            } catch (e: Exception) {
                FileHandler.getDataSoundChildFromInternalStorage(
                    context, mDataImage.name
                )[0].third.toMutableList()
            }
            view.setAdapter(listSoundChild)
        } catch (_: Exception) {
        }
    }

    private fun setUpListConnected() {
        apiClientPresenter.getListChildSound(mDataImage.id, object : ApiClientContract.Listens {
            override fun onSuccess(list: List<Any>) {
                val listsSound = list as MutableList<DataSound>
                val list1 = FileHandler.getAllFileAsset(context)
                val list11 =
                    list1.filter { it.first.name.lowercase() == mDataImage.name.lowercase() }
                if (list11.isNotEmpty()) {
                    list11[0].third.forEachIndexed { i, o ->
                        listsSound[i].source = o.source
                    }
                }
                view.setAdapter(listsSound)
                listSoundChild = listsSound
                listSize = listSoundChild.size
                (context as Show).onSuccess(listsSound)
            }

            override fun onFailed(e: String) {
                (context as Show).onFailed(e)
            }
        })
    }

    override fun handlerCbFavouriteClick(isChecked: Boolean) {
        checkDownLoad(mDataImage.name, itemDataSound.source, currentPosition)
        handleFavoriteChecked(isChecked, itemDataSound, mDataImage, currentPosition)
        if (!isChecked) listPositionUnchecked.add(currentPosition)
        else listPositionUnchecked.remove(currentPosition)
    }

    override fun handlerDownloadClick() {
        view.clickDownload()
        var position = currentPosition
        if (callingActivity == "Favorite") {
            position = getPositionSound(source)
        }
        downLoad(mDataImage, itemDataSound, position)
    }

    override fun handlePageScrolled(position: Int) {
        view.onPagerScroll()
        repeatInterval=-1
        isRepeatInterval = false
        currentPosition = position
        isLooping = false
        isStart = false
        handel.removeCallbacks(run)
        pauseMusic()
        setRepeatInterval(-1)
        if (listSoundChild.size > 0) {
            itemDataSound = listSoundChild[position]
            source = itemDataSound.source
            if (isCallingActivity) {
                mDataImage = getDataImgFavorite(source)
                checkDownLoad(mDataImage.name.trim(), source, getPositionSound(source))
            } else {
                checkDownLoad(mDataImage.name, source, currentPosition)
            }
            isFavorite(source)
            view.setImg(listSoundChild[currentPosition].image)
        }
    }


    override fun handleFavoriteChecked(
        isChecked: Boolean, dataSound: DataSound, mDataImage: DataImage, position: Int
    ) {
        with(FileHandler) {
            if (isDownload) {
                if (isChecked) saveFavoriteOff(dataSound, mDataImage, context, position)
                else removeFavoriteOff(context, dataSound)
            } else {
                if (isChecked) saveFavoriteOnl(dataSound, mDataImage, context, position)
                else removeFavoriteOnl(context, dataSound)
            }
        }
    }

    private fun getDataImage(): DataImage {
        val intent = intent
        currentPosition = intent.getIntExtra(Constraints.SOUND_CHILD_CLICK, 0)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(Constraints.PARENT_SOUND, DataImage::class.java)!!
        } else {
            intent.getSerializableExtra(Constraints.PARENT_SOUND) as DataImage
        }
    }

    override fun getDataImgFavorite(source: String): DataImage {
        view.onDataImage(getFavorite(source).first)
        return getFavorite(source).first
    }

    override fun getPositionSound(source: String): Int {
        return getFavorite(source).third
    }

    override fun getFavorite(source: String): Triple<DataImage, DataSound, Int> {
        val sizeFavoriteOff = FileHandler.getFavoriteOff(context).size
        val sizeFavoriteOnl = FileHandler.getFavoriteOnl(context).size
        return if (isFavorite(source) && !checkNetWork) {
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
            if (!b) {
                view.isDownload(true, R.drawable.download_24px)
            } else {
                view.isDownload(false, R.drawable.download_success)
                listSoundChild[currentPosition].source = s
            }
        }
    }

    override fun setSoundWhenSuccess() {
        val dataSoundChildList =
            FileHandler.getDataSoundChildFromInternalStorage(context, mDataImage.name)
        val dataSoundChild = dataSoundChildList[0].third.size
        if (isFavorite(source)) {
            FileHandler.removeFavoriteOnl(context, itemDataSound)
            FileHandler.saveFavoriteOff(
                dataSoundChildList[0].third[dataSoundChild - 1],
                mDataImage,
                context, currentPosition
            )
        }
        source = dataSoundChildList[0].third[dataSoundChild - 1].source
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
                                        context, bitmapImgImage, mImg.name, mImg.name + position
                                    )
                                    FileHandler.saveFileToAppDirectory(
                                        sound, mImg.name, mImg.name + position, context
                                    )
                                    setSoundWhenSuccess()
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
        if (isLooping && repeatInterval>0){
            setRepeatInterval(repeatInterval)
        }else  {handel.removeCallbacks(run)}
        Log.e("sssssssssssssssss",this.isLooping.toString())
    }

    override fun clickMenuPopup() {
        view.showMenuPopup()
    }

    override fun clickItemMenuPopup(position: Int) {
        val list = context.resources.getStringArray(R.array.time)
        if (position == -1) {
            setRepeatInterval(-1);return
        }
        val item = list[position]
        val repeatInterval = when {
            item.contains("s") -> item.split("s")[0].toInt() * 1000
            item.contains("m") -> item.split("m")[0].toInt() * 60000
            else -> {
                0
            }
        }
        if (mediaPlayer.isPlaying) mediaPlayer.stop()
        this.repeatInterval = repeatInterval
        handel.removeCallbacks(run)
        setRepeatInterval(this.repeatInterval)
    }

    override fun nextItem() {
        currentPosition += 1
        if (currentPosition >= listSize) {
            currentPosition = 0
        }
        view.showCurrentItem(currentPosition)
        view.setImg(listSoundChild[currentPosition].image)
    }

    override fun prevItem() {
        currentPosition--
        if (currentPosition < 0) {
            currentPosition = listSize - 1
        }
        view.showCurrentItem(currentPosition)
        view.setImg(listSoundChild[currentPosition].image)
    }

    override fun getMaxVolume(): Int {
        return audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
    }
fun remove(){
    handel.removeCallbacks(run)
}


    val h = Handler(Looper.myLooper()!!)
    override fun playMusicOnl(url: String) {
        h.removeCallbacksAndMessages(null)
        h.postDelayed({
            if (!mediaPlayer.isPlaying) view.loadFailed("Error loading")
        }, 3000)
        try {
            if (mediaPlayer.isPlaying) mediaPlayer.pause()
            view.load()
            mediaPlayer.setDataSource(context, url.toUri())
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                h.removeCallbacksAndMessages(null)
                mediaPlayer.start()
                view.loadSuccess()
            }

            mediaPlayer.setOnErrorListener { mp, what, _ ->
                setError(what)
                false
            }
        } catch (e: Exception) {
            Log.e("sadasdsadsad", e.toString())
        }
    }

    private fun setError(what: Int) {
        view.loadFailed("Error")
    }

    override fun playMusicOff(raw: AssetFileDescriptor) {
        if (mediaPlayer.isPlaying) mediaPlayer.stop()
        mediaPlayer.setDataSource(raw)
        mediaPlayer.prepare()
        mediaPlayer.start()

        mediaPlayer.setOnCompletionListener {

        }
    }


    override fun playMusic() {
        if (repeatInterval > 0) {
            play()
        } else {
            setRepeatInterval(0)
        }
    }

    override fun getCurrentVolume(): Int {
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
    }

    override fun pauseMusic() {
        mediaPlayer.stop()
        remove()
    }

    override fun adjustVolume(volume: Int) {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0)
    }

    override fun setFavorite(isFavorite: Boolean) {

    }
var time=0L
    private val run = object : Runnable {
        override fun run() {
            time=System.currentTimeMillis()-time
            Log.e("////////////////",repeatInterval.toString()+"timetimetimetimetimetimetime"+time)
            play()
            mediaPlayer.setOnCompletionListener {
                Log.e("////////////////",repeatInterval.toLong().toString())
                handel.removeCallbacks(this)
                if (isLooping && isStart) handel.postDelayed(this, repeatInterval.toLong()) else handel.removeCallbacks(this)
                time=System.currentTimeMillis()
            }
        }
    }

    private fun play() {
        isStart = true
        val pathSound = listSoundChild[currentPosition].source
        try {
            mediaPlayer.reset()
            val check = context.assets.openFd(pathSound)
            playMusicOff(check)
        } catch (e: Exception) {
            playMusicOnl(pathSound)
        }

    }

    private val handel = Handler(Looper.getMainLooper()!!)
    override fun setRepeatInterval(intervalSeconds: Int) {
        if (intervalSeconds == 0) {
            handel.postDelayed(run, repeatInterval.toLong())
        }
        if (intervalSeconds > 0) {
            handel.postDelayed(run, repeatInterval.toLong())
        }
    }
}