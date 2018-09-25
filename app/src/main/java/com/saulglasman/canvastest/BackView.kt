package com.saulglasman.canvastest

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfRenderer
import android.util.Log
import android.view.MotionEvent

class BackView(context: Context, viewModel: HistoryTreeViewModel, val listener: BackViewListener) : TransformableView(context, viewModel) {

    val TAG = BackView::class.java.simpleName

    interface BackViewListener {
        fun setPDFRenderer()
    }

    fun initBackBitmapIfNull(width: Int, height: Int) {
        if (viewModel.backBitmap == null) {
            viewModel.backBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            try {
                if (FileData.renderer == null) listener.setPDFRenderer()
                FileData.numPages = FileData.renderer!!.pageCount
                val renderedPage = FileData.renderer!!.openPage(FileData.page)
                renderedPage.render(viewModel.backBitmap!!, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                renderedPage.close()
                viewModel.currentNode.bmp = viewModel.backBitmap!!
            } catch (error: Throwable) {
                Log.d(TAG, "Error rendering PDF", error)
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.d(TAG, "Size changed: $w, $h from $oldw, $oldh")
        initBackBitmapIfNull(w, h)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas!!.drawBitmap(viewModel.backBitmap!!, 0f, 0f, null)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_MOVE) {
            invalidate()
        }
        return super.onTouchEvent(event)
    }
}