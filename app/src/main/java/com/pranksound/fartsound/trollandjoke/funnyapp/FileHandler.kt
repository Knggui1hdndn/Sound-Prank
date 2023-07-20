package com.pranksound.fartsound.trollandjoke.funnyapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.util.Log
import androidx.core.content.FileProvider
import com.pranksound.fartsound.trollandjoke.funnyapp.Constraints.AUTHORITY
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataImage
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataSound
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


object FileHandler {
    fun saveFileToAppDirectory(
        inputStream: InputStream,
        fileName: String,
        fileNameChild: String,
        context: Context,
    ) {
        val outputFile = checkExistsAndCreateFile(fileName, fileNameChild, context, "mp3")
        try {
            val outputStream = FileOutputStream(outputFile)
            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }
            outputStream.close()
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    fun saveImgToAppDirectory(
        context: Context,
        bitmap: Bitmap,
        fileName: String,
        fileNameChild: String
    ) {
        val outputFile = checkExistsAndCreateFile(fileName, fileNameChild, context, "png")
        val outputStream = FileOutputStream(outputFile)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
    }

    private fun checkExistsAndCreateFile(
        fileName: String,
        fileNameChild: String,
        context: Context,
        type: String
    ): File {
        val folderParent = File(context.filesDir, fileName)
        if (!folderParent.exists()) {
            folderParent.mkdir()
        }
        val folderChild = File(folderParent, fileNameChild)
        if (!folderChild.exists()) {
            folderChild.mkdir()
        }
        return File(folderChild, "${fileNameChild}.$type")
    }

    fun saveImgParentToAppDirectory(
        context: Context,
        bitmap: Bitmap,
        fileName: String
    ) {
        val outputFile = File(context.filesDir, fileName)
        if (!outputFile.exists()) {
            outputFile.mkdir()
        }
        val img = File(outputFile, "img.png")
        val outputStream = FileOutputStream(img)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
    }

    fun getDataSoundChildFromInternalStorage(
        context: Context,
        byNameParent: String?
    ): MutableList<Triple<DataImage, Boolean, List<DataSound>>> {
        val list = mutableListOf<Triple<DataImage, Boolean, List<DataSound>>>()
        var parentDirectories = getFolderName(context, null)
        if (byNameParent != null) {
            parentDirectories = parentDirectories.filter { it == byNameParent }.toMutableList()
        }
        var listDataSound = mutableListOf<DataSound>()
        var imgParentSound = ""
        for (fileNameParent in parentDirectories) {//Lấy các thư mục lớn nhất sau dirZ
           if (fileNameParent.length<=30){
               val directory = File(context.filesDir, fileNameParent)
               val fileChilds = directory.listFiles()
               fileChilds?.forEach { fileChild ->//Lấy các  thư mục con của  lớn nhất
                   if (fileChild.isFile && fileChild.name == "img.png") {
                       imgParentSound = getFileUri(context, fileChild)
                   }
                   val directoryChild = File(directory, fileChild.name)
                   val fileImgSounds = directoryChild.listFiles()
                   val dataSound = processFileImgSound(context, fileImgSounds)
                   if (dataSound != null) {
                       listDataSound.add(dataSound)
                   }
               }
               list.add(Triple(DataImage("0", fileNameParent, imgParentSound), false, listDataSound))
               listDataSound = mutableListOf()
           }
        }
        return list
    }

    private fun processFileImgSound(context: Context, fileImgSounds: Array<File>?): DataSound? {
        var uriSound: String? = null
        var uriImg: String? = null
        var dataSound: DataSound? = null
        fileImgSounds?.forEach { fileImgSound ->
            val extension = fileImgSound.extension
            if (fileImgSound.isFile && (extension == "mp3" || extension == "png")) {
                val contentUri = getFileUri(context, fileImgSound)
                if (extension == "mp3") {
                    uriSound = contentUri
                }
                if (extension == "png") {
                    uriImg = contentUri
                }
                if (uriImg != null && uriSound != null) {
                    dataSound = DataSound(uriSound!!, "false", uriImg!!)
                }
            }
        }
        return dataSound
    }

    private fun getFileUri(context: Context, file: File): String {
        return FileProvider.getUriForFile(context, AUTHORITY, file).toString()
    }

    fun saveFavoriteOnl(
        mDataSound: DataSound,
        mDataImage: DataImage,
        context: Context,
        position: Int
    ) {
        saveFavoriteSingle(context, mDataSound, mDataImage, Constraints.FAVORITE_ONL, position)
    }

    fun saveFavoriteOff(
        mDataSound: DataSound,
        mDataImage: DataImage,
        context: Context,
        position: Int
    ) {
        saveFavoriteSingle(context, mDataSound, mDataImage, Constraints.FAVORITE_Off, position)
    }

    private fun saveFavoriteSingle(
        context: Context,
        mDataSound: DataSound,
        mDataImage: DataImage,
        typeFavorite: String,
        position: Int
    ) {
        val getFavorite = getFavorite(context, typeFavorite)
        getFavorite.add(Triple(mDataImage, mDataSound, position))
        saveFavoriteList(context, getFavorite, typeFavorite)
    }

    fun removeFavoriteOnl(context: Context, mDataSound: DataSound) {
        val favoriteOnl = getFavoriteOnl(context).filter { it.second.source != mDataSound.source }
        saveFavoriteList(context, favoriteOnl, Constraints.FAVORITE_ONL)
    }

    fun removeFavoriteOff(context: Context, mDataSound: DataSound) {
        val favoriteOff = getFavoriteOff(context).filter { it.second.source != mDataSound.source }
        saveFavoriteList(context, favoriteOff, Constraints.FAVORITE_Off)
    }

    private fun saveFavoriteList(
        context: Context,
        favoriteList: List<Triple<DataImage, DataSound, Int>>,
        key: String
    ) {
        val shared = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        shared.edit().putString(key, buildFavoriteDataString(favoriteList.toMutableList())).apply()
    }


    @SuppressLint("WrongConstant")
    fun getFavoriteOnl(context: Context): MutableList<Triple<DataImage, DataSound, Int>> {
        return getFavorite(context, Constraints.FAVORITE_ONL)
    }

    @SuppressLint("WrongConstant")
    fun getFavoriteOff(context: Context): MutableList<Triple<DataImage, DataSound, Int>> {
        return getFavorite(context, Constraints.FAVORITE_Off)
    }

    private fun buildFavoriteDataString(list: MutableList<Triple<DataImage, DataSound, Int>>): String {
        val append = StringBuilder()
        list.forEach { list ->
            val mDataImage = list.first
            val mDataSound = list.second
            //DataImage-DataSound
            val sDataSound =
                mDataSound.let { "${it.source}&&${it.isPremium}&&${it.image}&&${list.third}\n" }
            val sDataImage = mDataImage.let { "${it.id}&&${it.name}&&${it.icon}&&" }
            append.append(sDataImage + sDataSound)
        }
        return append.toString()
    }

    private fun getFavorite(
        context: Context,
        typeFavorite: String
    ): MutableList<Triple<DataImage, DataSound, Int>> {
        val shared = context.getSharedPreferences(typeFavorite, Context.MODE_PRIVATE)
        val list = shared.getString(typeFavorite, "")
        val listSplit = list!!.split("\n")
        return parseSoundDataList(listSplit)
    }

    private fun parseSoundDataList(listSplit: List<String>): MutableList<Triple<DataImage, DataSound, Int>> {
        val mutableList = mutableListOf<Triple<DataImage, DataSound, Int>>()
        for (sound in listSplit) {
            val soundDataSplit = sound.split("&&")
            if (soundDataSplit.size > 3) {
                val dataSound = DataSound(soundDataSplit[3], soundDataSplit[4], soundDataSplit[5])
                val dataImage = DataImage(soundDataSplit[0], soundDataSplit[1], soundDataSplit[2])
                mutableList.add(Triple(dataImage, dataSound, soundDataSplit[6].toInt()))
            }
        }
        return mutableList
    }


    fun checkFileExists(
        context: Context,
        nameParent: String,
        pathSound: String,
        position: Int,
        call: (String, Boolean) -> Unit
    ) {
        var path = pathSound

        val check2 = File(path).exists()
        if (check2) {
            call(path, true)
            return
        }
         val check1 = isFileExistsInAssets(path, context)

        if (check1) {
            call(path, true)
            return
        }

        if (path.startsWith("content")) {
            call(path, true)
            return
        }
        path = "${context.filesDir}/$nameParent/${nameParent + position}/${nameParent + position}.mp3"
        Log.d("sdsadjalksdjasdd",path)

        val check3 = File(  path).exists()

        if (check3) {
            call(path, true)
            return
        }
        call(pathSound, false)
    }

    private fun isFileExistsInAssets(filePath: String?, context: Context ): Boolean {
        val assetManager: AssetManager = context.assets
        return try {
            val inputStream = assetManager.open(filePath!!)
            inputStream.close()
            true // Tệp tin tồn tại trong assets
        } catch (e: IOException) {
            false // Tệp tin không tồn tại trong assets
        }
    }

    private fun getFolderName(context: Context, nameParent: String?): MutableList<String> {
        val list = mutableListOf<String>()
        val directory = context.filesDir
        // Get a list of all files in the directory
        var files = directory.listFiles()
        if (files != null) {
            files.toMutableList()
            if (nameParent != null) files.toMutableList().removeIf { it.name != nameParent }

            for (file in files) {
                if (file.isDirectory) {
                    list.add(file.name)
                }
            }
        }
        return list
    }

    //nameParent bằng null thì get all
//    fun getSoundChildByNameParentFromAppDirectory(
//        context: Context,
//        nameParent: String?
//    ): MutableList<Triple<DataImage, Boolean, List<DataSound>>> {
//        val list = mutableListOf<Triple<DataImage, Boolean, List<DataSound>>>()
//        val imgParent: MutableList<String> = getImgParentSound(context, nameParent)
//        val nameParents: MutableList<String> = getFolderName(context, nameParent)
//        val soundChild: MutableList<Triple<DataImage, Boolean, List<DataSound>>> =
//            getDataSoundChildFromInternalStorage(context, nameParent)
//        var i = 0
//
//        nameParents.forEach { _ ->
//            list.add(
//                Triple(
//                    DataImage("0", nameParents[i], imgParent[i]),
//                    false,
//                    soundChild[i].third
//                )
//            )
//            i++
//        }
//        return list
//    }
//

    private fun getImgParentSound(context: Context, nameParent: String?): MutableList<String> {
        val list = mutableListOf<String>()
        val directory = context.filesDir
        // Get a list of all files in the directory
        val files = directory.listFiles()
        if (files != null) {
            if (nameParent != null) files.toMutableList().removeIf { it.name != nameParent }
            for (file in files) {
                if (file.isFile) {
                    val contentUri = getFileUri(context, file)
                    list.add(contentUri)
                }
            }
        }
        return list
    }


    //fun  Triple<DataImage, Boolean, List<DataSound>>
    fun getAllFileAsset(
        context: Context,
    ): List<Triple<DataImage, Boolean, List<DataSound>>> {
        val mng = context.assets
        val listsPathParent = listOf(
            "Airhorn",
            "Breaking",
            "Burp",
            "Car",
            "Hair Clipper"
        )
        val list = mutableListOf<Triple<DataImage, Boolean, List<DataSound>>>()

        listsPathParent.forEach { parent ->
            val image = mng.list("$parent/image/")
            val sound = mng.list("$parent/sound/")

            val listDataSound = sound?.map {
                DataSound("$parent/sound/$it", "false", "$parent/image/${image!![0]}")
            } ?: emptyList()

            val dataImage = DataImage("0", parent, "$parent/image/${image!![0]}")
            list.add(Triple(dataImage, false, listDataSound))
        }

        return list
    }

    fun getFileAssetByParentSound(context: Context, nameParent: String): List<DataSound> {
        val all = getAllFileAsset(context)
        return all.filter { nameParent == it.first.name }[0].third
    }
}