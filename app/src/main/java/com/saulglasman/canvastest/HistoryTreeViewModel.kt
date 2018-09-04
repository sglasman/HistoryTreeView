package com.saulglasman.canvastest

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HistoryTreeViewModel(var backBitmap: Bitmap? = null,
                           var transformer: CanvasTransformer = CanvasTransformer(),
                           var tree: BmpTree = BmpTree(),
                           var currentNode: BmpTree.TreeNode = tree.nodeAtCoords(Pair(0, 0))!!,
                           var isTreeShown: MutableLiveData<Boolean> = MutableLiveData(),
                           var isEditing: MutableLiveData<Boolean> = MutableLiveData(),
                           var isCommitted: MutableLiveData<Boolean> = MutableLiveData(),
                           var drawColor: MutableLiveData<Int> = MutableLiveData()) : ViewModel() {

    fun reset() {
        arrangeBmps()
        isEditing.value = false
    }

    fun setStackPointers(value: Int) {
        currentNode.stackPointer = value
    }

    fun arrangeBmps() {
        val nodeList = if (currentNode.isActive) (tree.getLineage(currentNode).dropLast(1)) else tree.getLineage(currentNode)
        backBitmap = overlayBmpList(nodeList.map { node ->
            node.bmp
                    ?: Bitmap.createBitmap(TOKEN_WIDTH_HEIGHT, TOKEN_WIDTH_HEIGHT, Bitmap.Config.ARGB_8888)
        })
    }

    init {
        isEditing.value = false
        isTreeShown.value = false
        isCommitted.value = false
        drawColor.value = Color.BLACK
    }
}