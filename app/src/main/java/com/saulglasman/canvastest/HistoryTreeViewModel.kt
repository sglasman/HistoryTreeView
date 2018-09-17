package com.saulglasman.canvastest

import android.graphics.Bitmap
import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HistoryTreeViewModel(var backBitmap: Bitmap? = null,
                           var transformer: CanvasTransformer = CanvasTransformer(),
                           var tree: BmpTree = BmpTree(),
                           var currentNode: BmpTree.TreeNode = tree.rootNode,

                           var isTreeShown: MutableLiveData<Boolean> = MutableLiveData(),
                           var isEditing: MutableLiveData<Boolean> = MutableLiveData(),
                           var drawColor: MutableLiveData<Int> = MutableLiveData(),
                           var isUndoEnabled: MutableLiveData<Boolean> = MutableLiveData(),
                           var isRedoEnabled: MutableLiveData<Boolean> = MutableLiveData(),
                           var isDeleteButtonEnabled: MutableLiveData<Boolean> = MutableLiveData(),
                           var isCommitButtonEnabled: MutableLiveData<Boolean> = MutableLiveData(),
                           var isSecondaryButtonBarShown: MutableLiveData<Boolean> = MutableLiveData(),
                           var isPgUpButtonEnabled: MutableLiveData<Boolean> = MutableLiveData(),
                           var isPgDownButtonEnabled: MutableLiveData<Boolean> = MutableLiveData()) : ViewModel() {

    fun reset() {
        backBitmap = null
        transformer = CanvasTransformer()
        tree = BmpTree()
        currentNode = tree.rootNode

        isEditing.value = false
        isTreeShown.value = false
        isUndoEnabled.value = false
        isRedoEnabled.value = false
        isDeleteButtonEnabled.value = false
        isCommitButtonEnabled.value = false
        isSecondaryButtonBarShown.value = false
        drawColor.value = Color.BLACK
    }

    fun arrangeBmps() {
        val nodeList = if (currentNode.isActive) (tree.getLineage(currentNode).dropLast(1)) else tree.getLineage(currentNode)
        backBitmap = overlayBmpList(nodeList.map { node ->
            node.bmp
                    ?: Bitmap.createBitmap(TOKEN_WIDTH_HEIGHT, TOKEN_WIDTH_HEIGHT, Bitmap.Config.ARGB_8888)
        })
    }

    fun enableDisablePgButtons() {
        isPgUpButtonEnabled.value = FileData.page > 0
        isPgDownButtonEnabled.value = FileData.page < FileData.numPages - 1
    }

    fun enableDisableUndoRedoButtons() {
        isRedoEnabled.value = currentNode.stackPointer < currentNode.undoRedoStack.size
        isUndoEnabled.value = currentNode.stackPointer > 0
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