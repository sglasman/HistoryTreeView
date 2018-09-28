package com.saulglasman.canvastest

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.os.ParcelFileDescriptor
import androidx.appcompat.app.AppCompatActivity

abstract class FilePickerActivity: AppCompatActivity() {
    fun launchFilePicker() {
        val openFileIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                .addCategory(Intent.CATEGORY_OPENABLE)
                .setType("application/pdf")
        startActivityForResult(openFileIntent, REQUEST_CODE_OPENFILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { // used when picking a new fileUri
        if (requestCode == REQUEST_CODE_OPENFILE && resultCode == Activity.RESULT_OK && data?.data != null) {
            FileData.fileUri = data.data
            contentResolver.takePersistableUriPermission(data.data!!, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            FileData.setRenderer(contentResolver)
        }
    }
}