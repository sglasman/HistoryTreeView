package com.saulglasman.canvastest

import android.app.Activity
import android.content.Intent
import android.os.Bundle

class FileLoaderActivity : FilePickerActivity()

// The purpose of this activity is to make sure we have a fileUri to load before launching MainActivity

{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            FileDataManager.loadFileData(filesDir)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } catch (error: Throwable) {
            launchFilePicker()
        }
    }

   override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
       if (requestCode == REQUEST_CODE_OPENFILE && resultCode == Activity.RESULT_OK && data?.data != null) {
           super.onActivityResult(requestCode, resultCode, data)
           startActivity(Intent(this, MainActivity::class.java))
           finish()
       }
   }
}