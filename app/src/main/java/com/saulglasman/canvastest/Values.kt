package com.saulglasman.canvastest

const val TOKEN_WIDTH_HEIGHT: Int = 1
const val touchTolerance: Float = 4f
const val ID_BUTTONBAR = 101
const val ID_MAINVIEW = 102
const val ID_TREEVIEW = 103
const val ID_UNDOREDOBAR = 104
const val STROKE_WIDTH = 12f

var PERMISSIONS_STORAGE: Array<String> = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

enum class CanvasMode {
    MODE_NAV, MODE_DRAW
}