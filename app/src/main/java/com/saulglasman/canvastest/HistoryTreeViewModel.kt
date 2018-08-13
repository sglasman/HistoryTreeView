package com.saulglasman.canvastest

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HistoryTreeViewModel(var backBitmap: Bitmap? = null,
                           var undoRedoStack: MutableList<Bitmap> = mutableListOf(),
                           var transformer: CanvasTransformer = CanvasTransformer(),
                           val tree: BmpTree = BmpTree(),
                           var currentNode: BmpTree.TreeNode = tree.nodeAtCoords(Pair(0, 0))!!,
                           var stackPointer: Int = 0,
                           var isCanvasFresh: MutableLiveData<Boolean> = MutableLiveData(),
                           var isTreeShown: MutableLiveData<Boolean> = MutableLiveData(),
                           var isEditing: MutableLiveData<Boolean> = MutableLiveData(),
                           var isCommitted: MutableLiveData<Boolean> = MutableLiveData()) : ViewModel() {

    fun reset() {
        arrangeBmps()
        isCanvasFresh.value = true
        isEditing.value = false
    }

    fun arrangeBmps() {
        val nodeList = tree.getLineage(currentNode)
        backBitmap = overlayBmpList(nodeList.map { node ->
            node.bmp
                    ?: Bitmap.createBitmap(TOKEN_WIDTH_HEIGHT, TOKEN_WIDTH_HEIGHT, Bitmap.Config.ARGB_8888)
        })
    }

    init {
        isEditing.value = false
        isTreeShown.value = false
        isCommitted.value = false
        isCanvasFresh.value = true
    }
}