package com.saulglasman.canvastest

import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream

object FileDataManager {

    val TAG = FileDataManager::class.java.simpleName

    @Throws(Throwable::class)
    fun saveFileData(filesDir: File) {
        try {
            val fileDataFile = File(filesDir, "filedata")
            fileDataFile.writeText(FileData.fileUri.toString())
            Log.d(TAG, "Wrote fileUri data fileUri")
        } catch (error: Throwable) {
            throw error
        }
    }

    @Throws(Throwable::class)
    fun loadFileData(filesDir: File) {
        try {
            val fileDataFile = File(filesDir, "filedata")
            FileData.fileUri = Uri.parse(fileDataFile.readText())
        } catch (error: Throwable) {
            throw error
        }
    }
}