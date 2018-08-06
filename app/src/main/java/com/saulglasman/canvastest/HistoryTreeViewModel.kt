package com.saulglasman.canvastest

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HistoryTreeViewModel(var backBitmap: Bitmap? = null,
                           var frontBitmap: Bitmap? = null, //This will eventually be replaced with an undo/redo stack
                           var transformer: CanvasTransformer = CanvasTransformer(),
                           val tree: BmpTree = BmpTree(),
                           var currentNode: BmpTree.TreeNode = tree.nodeAtCoords(Pair(0, 0))!!,

                           var isCanvasFresh: MutableLiveData<Boolean> = MutableLiveData(),
                           var isTreeShown: MutableLiveData<Boolean> = MutableLiveData(),
                           var isEditing: MutableLiveData<Boolean> = MutableLiveData(),
                           var isCommitted: MutableLiveData<Boolean> = MutableLiveData()) : ViewModel() {
    fun arrangeBmps() {
        val nodeList = tree.getLineageExceptRoot(currentNode)
        backBitmap = nodeList.fold(
                Bitmap.createBitmap(tree.nodeAtCoords(Pair(0, 0))!!.bmp!!)
        ) { acc: Bitmap, node: BmpTree.TreeNode ->
            val canvas = Canvas(acc)
            canvas.drawBitmap(node.bmp!!, 0f, 0f, Paint())
            acc
        }
    }

    init {
        isEditing.value = false
        isTreeShown.value = false
        isCommitted.value = false
        isCanvasFresh.value = true
    }
}