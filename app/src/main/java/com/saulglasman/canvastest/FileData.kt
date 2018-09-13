package com.saulglasman.canvastest

import android.os.Environment
import java.io.File

object FileData
{
    val fileHash: String
    get() = file.path.hashCode().toString()

    var file: File = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "sample.pdf")
    var page: Int = 0

    var numPages: Int = 1
}