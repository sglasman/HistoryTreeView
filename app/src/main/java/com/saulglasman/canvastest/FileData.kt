package com.saulglasman.canvastest

import java.io.File

data class FileData(var file: File, var page: Int)
{
    val fileHash: String
    get() = file.path.hashCode().toString()
}