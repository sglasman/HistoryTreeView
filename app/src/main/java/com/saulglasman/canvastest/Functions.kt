package com.saulglasman.canvastest

import android.graphics.*
import android.view.ViewManager
import org.jetbrains.anko.custom.ankoView

fun overlayBmpList(bmpList: List<Bitmap>): Bitmap =
        if (bmpList.isEmpty()) {
            Bitmap.createBitmap(TOKEN_WIDTH_HEIGHT, TOKEN_WIDTH_HEIGHT, Bitmap.Config.ARGB_8888)
        } else {
            bmpList.fold(
                    Bitmap.createBitmap(bmpList[0])
            ) { acc: Bitmap, i: Bitmap ->
                val canvas = Canvas(acc)
                canvas.drawBitmap(i, 0f, 0f, Paint())
                acc
            }
        }

fun Bitmap.changeColor(newColor: Int) {
    val paint = Paint().apply { colorFilter = PorterDuffColorFilter(newColor, PorterDuff.Mode.SRC_IN) }
    Canvas(this).drawBitmap(this, 0f, 0f, paint)
}

fun ViewManager.historyTreeView(viewModel: HistoryTreeViewModel, listener: HistoryTreeView.HistoryTreeViewListener, init: HistoryTreeView.() -> Unit = {}) = ankoView({ HistoryTreeView(it, viewModel, listener) }, 0, init)
fun ViewManager.backView(viewModel: HistoryTreeViewModel, listener: BackView.BackViewListener, init: BackView.() -> Unit = {}) = ankoView({ BackView(it, viewModel, listener) }, 0, init)
fun ViewManager.treeView(viewModel: HistoryTreeViewModel, listener: TreeView.TreeViewListener, isLarge: Boolean, init: TreeView.() -> Unit = {}) = ankoView({ TreeView(it, viewModel, listener, isLarge) }, 0, init)
fun ViewManager.colorSelectButton(color: Int, viewModel: HistoryTreeViewModel, init: ColorSelectButton.() -> Unit = {}) = ankoView({ ColorSelectButton(it, color, viewModel) }, 0, init)
fun ViewManager.widthSelectButton(width: Float, viewModel: HistoryTreeViewModel, init: WidthSelectButton.() -> Unit = {}) = ankoView({ WidthSelectButton(it, width, viewModel)}, 0, init)