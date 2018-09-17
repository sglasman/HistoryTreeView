package com.saulglasman.canvastest

import android.app.Activity
import android.content.Intent
import android.os.ParcelFileDescriptor
import androidx.appcompat.app.AppCompatActivity

open class FilePickerActivity: AppCompatActivity() {
    fun launchFilePicker() {
        val openFileIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                .addCategory(Intent.CATEGORY_OPENABLE)
                .setType("application/pdf")
        startActivityForResult(openFileIntent, REQUEST_CODE_OPENFILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { // used when picking a new fileUri
        if (requestCode == REQUEST_CODE_OPENFILE && resultCode == Activity.RESULT_OK && data?.data != null) {
/*            val filePath = data.data!!.path
            val correctedPath = if (filePath!!.startsWith("/document/raw:"))
            *//* (for some reason this was prepended to the path,
             * causing fileUri not found errors) *//*
                filePath.drop("/document/raw:".length)
            else filePath*/
            FileData.fileUri = data.data
            FileData.setRenderer(contentResolver)
        }
    }
}