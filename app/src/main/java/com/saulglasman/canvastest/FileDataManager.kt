package com.saulglasman.canvastest

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
            fileDataOos.writeObject(FileData.file)
            Log.d(TAG, "Wrote file data file")
        } catch (error: Throwable) {
            throw error
        } finally {
            fileDataFos?.close()
            fileDataOos?.close()
        }
    }

    @Throws(Throwable::class)
    fun loadFileData(filesDir: File) {
        var fileDataFis: FileInputStream? = null
        var fileDataOis: ObjectInputStream? = null
        try {
            fileDataFis = FileInputStream(File(filesDir, "filedata"))
            fileDataOis = ObjectInputStream(fileDataFis)
            FileData.file = fileDataOis.readObject() as File
        } catch (error: Throwable) {
            throw error
        } finally {
            fileDataFis?.close()
            fileDataOis?.close()
        }
    }
}