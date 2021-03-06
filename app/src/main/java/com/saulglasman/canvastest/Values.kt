package com.saulglasman.canvastest

import android.graphics.Color

const val TOKEN_WIDTH_HEIGHT: Int = 1
const val touchTolerance: Float = 4f
const val ID_BUTTONBAR = 101
const val ID_MAINVIEW = 102
const val ID_TREEVIEW = 103
const val ID_EDITBUTTON = 105
const val ID_COMMITBUTTON = 106
const val ID_TREEBUTTON = 107
const val ID_COLORCHANGEBUTTON = 108
const val ID_UNDOBUTTON = 110
const val ID_REDOBUTTON = 111
const val ID_DELETEBUTTON = 112
const val ID_MOREBUTTON = 113
const val ID_SECONDARYBUTTONBAR = 114
const val ID_PGUPBUTTON = 115
const val ID_PGDOWNBUTTON = 116
const val ID_OPENFILEBUTTON = 117
const val ID_BUTTONFRAME = 118
const val ID_WIDTHCHANGEBUTTON = 119

const val REQUEST_CODE_OPENFILE = 200

const val DEFAULT_STROKE_WIDTH = 2f
const val TREEVIEW_CLICK_TOLERANCE = 24f
const val COLOR_SELECT_BUTTON_SIZE = 24
const val SELECT_RECT_MARGIN = 3

const val FILE_REGEX_STRING = "(\\d+)_(\\d+)"

val COLORS: List<Int> = listOf(Color.BLACK, Color.RED, Color.parseColor("#009874") /*emerald green*/, Color.BLUE)
val WIDTHS: List<Float> = listOf(1f, 2f, 4f, 6f, 8f)

var PERMISSIONS_STORAGE: Array<String> = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

enum class CanvasMode {
    MODE_NAV, MODE_DRAW
}

enum class ButtonBarMode {
    MODE_DEFAULT, MODE_COLORSELECT, MODE_WIDTHSELECT
}