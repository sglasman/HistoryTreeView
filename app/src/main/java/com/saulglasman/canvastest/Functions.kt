package com.saulglasman.canvastest

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.view.ViewManager
import org.jetbrains.anko.custom.ankoView

fun overlayBmpList(bmpList: List<Bitmap>): Bitmap =
        if (bmpList.isEmpty()) {
            Bitmap.createBitmap(TOKEN_WIDTH_HEIGHT, TOKEN_WIDTH_HEIGHT, Bitmap.Config.ARGB_8888)
        } else {
            bmpList.drop(1).fold(
                    Bitmap.createBitmap(bmpList[0])
            ) { acc: Bitmap, i: Bitmap ->
                val canvas = Canvas(acc)
                canvas.drawBitmap(i, 0f, 0f, Paint())
                acc
            }
        }

fun ViewManager.historyTreeView(viewModel: HistoryTreeViewModel, init: HistoryTreeView.() -> Unit = {}) = ankoView({ HistoryTreeView(it, viewModel) }, 0, init)
fun ViewManager.treeView(viewModel: HistoryTreeViewModel, listener: TreeView.TreeViewListener, init: TreeView.() -> Unit = {}) = ankoView({ TreeView(it, viewModel, listener) }, 0, init)