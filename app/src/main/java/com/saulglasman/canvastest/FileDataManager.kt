package com.saulglasman.canvastest

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.*

object FileDataManager {

    val TAG = FileDataManager::class.java.simpleName

    @Throws(Throwable::class)
    fun saveFileData(filesDir: File) {
        var fileDataFos: FileOutputStream? = null
        var fileDataOos: ObjectOutputStream? = null
        try {
            fileDataFos = FileOutputStream(File(filesDir, "filedata"))
            fileDataOos = ObjectOutputStream(fileDataFos)
            fileDataOos.writeObject(FileData.fileUri)
            Log.d(TAG, "Wrote fileUri data fileUri")
        } catch (error: Throwable) {
            throw error
        } finally {
            fileDataFos?.close()
            fileDataOos?.close()
        }
    }

    @Throws(Throwable::class)
    fun loadFileData(filesDir: File, contentResolver: ContentResolver) {
        var fileDataFis: FileInputStream? = null
        var fileDataOis: ObjectInputStream? = null
        try {
            fileDataFis = FileInputStream(File(filesDir, "filedata"))
            fileDataOis = ObjectInputStream(fileDataFis)
            FileData.fileUri = fileDataOis.readObject() as Uri
        } catch (error: Throwable) {
            throw error
        } finally {
            fileDataFis?.close()
            fileDataOis?.close()
        }
    }
}