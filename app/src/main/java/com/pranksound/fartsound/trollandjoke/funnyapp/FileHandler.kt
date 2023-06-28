package com.pranksound.fartsound.trollandjoke.funnyapp

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataImage
import com.pranksound.fartsound.trollandjoke.funnyapp.model.DataSound
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.Arrays


object FileHandler {
    private fun saveFileToAppDirectory(
        inputStream: InputStream,
        fileName: String,
        context: Context
    ) {
        val outputFile = checkExistsAndCreateFile(fileName, context, "mp3")
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

    private fun checkExistsAndCreateFile(fileName: String, context: Context, type: String): File {
        val folderParent = File(context.filesDir, fileName)
        if (!folderParent.exists()) {
            folderParent.mkdir()
        }
        val folderChild = File(folderParent, fileName + folderParent.listFiles()!!.size + 1)
        if (!folderChild.exists()) {
            folderChild.mkdir()
        }
        return File(folderChild, "${fileName + folderChild.listFiles()!!.size + 1}.$type")
    }

    private fun saveImgToAppDirectory(context: Context, bitmap: Bitmap, fileName: String) {
        val outputFile = checkExistsAndCreateFile(fileName, context, "png")
        val outputStream = FileOutputStream(outputFile)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
    }

    private fun getDataSoundChildFromInternalStorage(context: Context): MutableMap<String, List<DataSound>> {
        val list = mutableMapOf<String, List<DataSound>>()
        // Get a list of all files in the directory
        val files = getFolderName(context)
        for (file in files) {
            val directory = File(context.filesDir, file)
            val files = directory.listFiles()
            if (files != null) {
                Arrays.sort(files) { file1, file2 ->
                    val time1 = file1.lastModified()
                    val time2 = file2.lastModified()
                    time1.compareTo(time2)
                }
                val listDataSound = mutableListOf<DataSound>()
                var checkName = ""
                for (file1 in files) {
                    if (file1.isFile) {
                        val contentUri: Uri = FileProvider.getUriForFile(
                            context,
                            "com.pranksound.fartsound.trollandjoke.funnyapp",
                            file1
                        )
                        var uriSound: String? = null
                        var uriImg: String? = null
                        if (file1.name.endsWith(".mp3")) {
                            uriSound = contentUri.toString()
                        }

                        if (file1.name.endsWith(".png")) {
                            uriImg = contentUri.toString()
                        }
                        if (checkName != file1.name) {
                            listDataSound.clear()
                        }
                        listDataSound.add(DataSound(uriSound!!, "false", uriImg!!))
                        list[file1.parentFile!!.name] = listDataSound
                        checkName = file1.name
                    }

                }
            }


        }
        return list
    }

    private fun getFolderName(context: Context): MutableList<String> {
        val list = mutableListOf<String>()
        val directory = context.filesDir
        // Get a list of all files in the directory
        val files = directory.listFiles()

        if (files != null) {
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

    fun getAllSoundParentFromAppDirectory(context: Context): MutableList<Triple<DataImage, Boolean, List<DataSound>>> {
        val list = mutableListOf<Triple<DataImage, Boolean, List<DataSound>>>()
        val imgParent: MutableList<String> = getImgParentSound(context)
        val nameParent: MutableList<String> = getFolderName(context)
        val soundChild: MutableMap<String, List<DataSound>> =
            getDataSoundChildFromInternalStorage(context)
        var i = 0

        imgParent.forEach {
            list.add(
                Triple(
                    DataImage("0", nameParent[i], imgParent[i]),
                    false,
                    soundChild[nameParent[i]]!!
                )
            )
            i++
        }
        return list
    }

    private fun getImgParentSound(context: Context): MutableList<String> {
        val list = mutableListOf<String>()
        val directory = context.filesDir
        // Get a list of all files in the directory
        val files = directory.listFiles()
        if (files != null) {
            Arrays.sort(files) { file1, file2 ->
                val time1 = file1.lastModified()
                val time2 = file2.lastModified()
                time1.compareTo(time2)
            }
            for (file in files) {
                if (file.isFile) {
                    val contentUri: Uri = FileProvider.getUriForFile(
                        context,
                        "com.pranksound.fartsound.trollandjoke.funnyapp",
                        file
                    )
                    list.add(contentUri.toString())
                }
            }
        }
        return list
    }

    fun getNameFolderAssets(context: Context): Array<String>? {
        val listName = context.assets
        return listName.list("")
    }

    //fun  Triple<DataImage, Boolean, List<DataSound>>
//    fun getAllFileAsset(
//        context: Context,
//        pathParent: String,
//        pathChild: String,
//        pathChild2: String
//    ): List<DataSound> {
//        val asset=context.assets.list("")
//
//        val list = mutableListOf<Triple<DataImage, Boolean, List<DataSound>>>()
//
//    }
}