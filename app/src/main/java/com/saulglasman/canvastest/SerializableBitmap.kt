package com.saulglasman.canvastest

import android.graphics.Bitmap
import java.io.ObjectOutputStream
import java.io.Serializable

class SerializableBitmap(val bitmap: Bitmap) : Serializable {
    fun writeObject(oos: ObjectOutputStream) {

    }
}