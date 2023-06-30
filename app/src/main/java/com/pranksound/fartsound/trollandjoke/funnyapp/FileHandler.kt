package com.pranksound.fartsound.trollandjoke.funnyapp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.pranksound.fartsound.trollandjoke.funnyapp.Constraints.AUTHORITY
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataImage
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataSound
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.lang.StringBuilder
import java.util.Arrays


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
            parentDirectories = parentDirectories.filter { it != byNameParent }.toMutableList()
            Log.d("plpppppppppssppppppp",parentDirectories.size.toString())

        }
        var listDataSound = mutableListOf<DataSound>()
        var imgParentSound = ""
        for (fileNameParent in parentDirectories) {//Lấy các thư mục lớn nhất sau dirZ
            val directory = File(context.filesDir, fileNameParent)
            val fileChilds = directory.listFiles()

            fileChilds?.forEach { fileChild ->//Lấy các  thư mục con của  lớn nhất
                if (fileChild.isFile && fileChild.name == "img.png") {
                    imgParentSound = FileProvider.getUriForFile(
                        context,
                        AUTHORITY,
                        fileChild
                    ).toString()
                }
                val directoryChild = File(directory, fileChild.name)
                val fileImgSounds = directoryChild.listFiles()
                var uriSound: String? = null
                var uriImg: String? = null
                fileImgSounds?.forEach { fileImgSound ->
                    val extension = fileImgSound.extension
                    if (fileImgSound.isFile && (extension == "mp3" || extension == "png")) {
                        val contentUri: Uri = FileProvider.getUriForFile(
                            context,
                            AUTHORITY,
                            fileImgSound
                        )

                        if (extension == "mp3") {
                            uriSound = contentUri.toString()
                        }
                        if (extension == "png") {
                            uriImg = contentUri.toString()
                        }

                        if (uriImg != null && uriSound != null) {
                            listDataSound.add(DataSound(uriSound!!, "false", uriImg!!))
                        }
                    }
                }
            }
            list.add(Triple(DataImage("0", fileNameParent, imgParentSound), false, listDataSound))
            listDataSound = mutableListOf<DataSound>()
        }
        Log.d("plpppppppppppppppp",list.size.toString())
        Log.d("plpppppppppppppppp",list[0].third.toString())
        return list
    }


    fun saveFavoriteOnl(mDataSound: DataSound, context: Context) {
        saveFavorite(context, mDataSound, Constraints.FAVORITE_ONL)
    }

    fun saveFavoriteOff(mDataSound: DataSound, context: Context) {
        saveFavorite(context, mDataSound, Constraints.FAVORITE_Off)
    }

    private fun saveFavorite(context: Context, mDataSound: DataSound, typeFavorite: String) {
        val shared = context.getSharedPreferences(typeFavorite, Context.MODE_PRIVATE)
        val append = StringBuilder()
        val getFavorite = getFavorite(context, typeFavorite)
        getFavorite.add(mDataSound)
        getFavorite.forEach{
            append.append(it.source + "&&" + it.image + "\n")
        }

        shared.edit().putString(
            typeFavorite,
            append.toString()
            ).apply()
    }

    private fun getFavorite(context: Context, typeFavorite: String): MutableList<DataSound> {
        val shared = context.getSharedPreferences(typeFavorite, Context.MODE_PRIVATE)
        val mutableList = mutableListOf<DataSound>()
        val list = shared.getString(typeFavorite, "")
        val listSplip = list!!.split("\n")

        for (sound in listSplip) {
            val soundDataSplit = sound.split("&&")
            if (soundDataSplit.size == 2) {
                mutableList.add(DataSound(soundDataSplit[0], "false", soundDataSplit[1]))
            }
        }

        return mutableList.toMutableList()
    }

    @SuppressLint("WrongConstant")
    fun getFavoriteOnl(context: Context): List<DataSound> {
        return getFavorite(context, Constraints.FAVORITE_ONL)
    }

    @SuppressLint("WrongConstant")
    fun getFavoriteOff(context: Context): List<DataSound> {
        Log.d("iokokosadasd", getFavorite(context, Constraints.FAVORITE_Off).size.toString())
        return getFavorite(context, Constraints.FAVORITE_Off)
    }

    fun checkFileExists(context: Context, nameParent: String, position: Int): Boolean {
        return File("${context.filesDir}/$nameParent/${nameParent + position}/${nameParent + position}.png").exists()
    }

    private fun getFolderName(context: Context, nameParent: String?): MutableList<String> {
        val list = mutableListOf<String>()
        val directory = context.filesDir
        // Get a list of all files in the directory
        var files = directory.listFiles()

        if (files != null) {
            files.toMutableList()
            if (nameParent != null) files.toMutableList().removeIf { it.name != nameParent }
            Arrays.sort(files) { file1, file2 ->
                val time1 = file1.lastModified()
                val time2 = file2.lastModified()
                time1.compareTo(time2)
            }

            for (file in files) {
                if (file.isDirectory) {
                    list.add(file.name)
                }
            }
        }
        return list
    }

    //nameParent bằng null thì get all
    fun getSoundChildByNameParentFromAppDirectory(
        context: Context,
        nameParent: String?
    ): MutableList<Triple<DataImage, Boolean, List<DataSound>>> {
        val list = mutableListOf<Triple<DataImage, Boolean, List<DataSound>>>()
        val imgParent: MutableList<String> = getImgParentSound(context, nameParent)
        val nameParents: MutableList<String> = getFolderName(context, nameParent)
        val soundChild: MutableList<Triple<DataImage, Boolean, List<DataSound>>> =
            getDataSoundChildFromInternalStorage(context, nameParent)
        var i = 0

        nameParents.forEach { _ ->
            list.add(
                Triple(
                    DataImage("0", nameParents[i], imgParent[i]),
                    false,
                    soundChild[i].third
                )
            )
            i++
        }
        return list
    }


    private fun getImgParentSound(context: Context, nameParent: String?): MutableList<String> {
        val list = mutableListOf<String>()
        val directory = context.filesDir
        // Get a list of all files in the directory
        val files = directory.listFiles()
        if (files != null) {
            if (nameParent != null) files.toMutableList().removeIf { it.name != nameParent }
            Arrays.sort(files) { file1, file2 ->
                val time1 = file1.lastModified()
                val time2 = file2.lastModified()
                time1.compareTo(time2)
            }
            for (file in files) {
                if (file.isFile) {
                    val contentUri: Uri = FileProvider.getUriForFile(
                        context,
                        AUTHORITY,
                        file
                    )
                    list.add(contentUri.toString())
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
            "Baby Sneeze",
            "Breaking",
            "Burp",
            "Car",
            "Fart sound",
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