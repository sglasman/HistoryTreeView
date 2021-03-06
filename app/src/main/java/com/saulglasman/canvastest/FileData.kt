package com.saulglasman.canvastest

import android.content.ContentResolver
import android.graphics.pdf.PdfRenderer
import android.net.Uri

object FileData
{
    val fileHash: String
    get() = fileUri?.hashCode().toString()

    var fileUri: Uri? = null
    var renderer: PdfRenderer? = null
    var page: Int = 0
    var numPages: Int = 1

    fun setRenderer(cr: ContentResolver) {
        renderer = PdfRenderer(cr.openFileDescriptor(fileUri!!, "r")!!)
        numPages = renderer!!.pageCount
    }
}