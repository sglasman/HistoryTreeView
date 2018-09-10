package com.saulglasman.canvastest

import android.graphics.Bitmap
import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HistoryTreeViewModel(var backBitmap: Bitmap? = null,
                           var transformer: CanvasTransformer = CanvasTransformer(),
                           var tree: BmpTree = BmpTree(),
                           var currentNode: BmpTree.TreeNode = tree.nodeAtCoords(Pair(0, 0))!!,
                           var isTreeShown: MutableLiveData<Boolean> = MutableLiveData(),
                           var isEditing: MutableLiveData<Boolean> = MutableLiveData(),
                           var drawColor: MutableLiveData<Int> = MutableLiveData(),
                           var isUndoEnabled: MutableLiveData<Boolean> = MutableLiveData(),
                           var isRedoEnabled: MutableLiveData<Boolean> = MutableLiveData(),
                           var isDeleteButtonEnabled: MutableLiveData<Boolean> = MutableLiveData(),
                           var isCommitButtonEnabled: MutableLiveData<Boolean> = MutableLiveData(),
                           var isSecondaryButtonBarShown: MutableLiveData<Boolean> = MutableLiveData()) : ViewModel() {

    fun reset() {
        arrangeBmps()
        isEditing.value = false
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
        isUndoEnabled.value = false
        isRedoEnabled.value = false
        isDeleteButtonEnabled.value = false
        isCommitButtonEnabled.value = false
        isSecondaryButtonBarShown.value = false
        drawColor.value = Color.BLACK
    }
}