package com.saulglasman.canvastest

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfRenderer
import android.graphics.pdf.PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.os.ParcelFileDescriptor.MODE_READ_ONLY
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.ImageView
import java.io.File
import java.lang.Math.*

const val touchTolerance: Float = 4f

enum class CanvasMode {
    MODE_NAV, MODE_DRAW
}

@SuppressLint("ViewConstructor")
class HistoryTreeView(context: Context, val viewModel: HistoryTreeViewModel) : ImageView(context) {
    val paint = Paint()
    val path = Path()
    private val scaleGestureDetector: ScaleGestureDetector = ScaleGestureDetector(context, ZoomListener())
    private val gestureDetector: GestureDetector = GestureDetector(context, ScrollListener())

    lateinit var pathCanvas: Canvas
    var currX: Float = 0f
    var currY: Float = 0f
    lateinit var frame: Rect
    var mode: CanvasMode = CanvasMode.MODE_NAV

    init {
        with(paint) {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 12f
        }
/*        PDFBoxResourceLoader.init(context);
        val pdfFile = PDDocument.load(File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "sample.pdf"))
        val renderer = PDFRenderer(pdfFile)
        val page = pdfFile.getPage(0)
        pdfBitmap = renderer.renderImageWithDPI(0, DisplayMetrics.DENSITY_DEFAULT.toFloat(), Bitmap.Config.ARGB_8888)*/

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        if (viewModel.frontBitmap == null) viewModel.frontBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        if (viewModel.backBitmap == null) {
            viewModel.backBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            val renderer = PdfRenderer(ParcelFileDescriptor.open(
                    File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                            "sample.pdf"), MODE_READ_ONLY))
            renderer.openPage(0).render(viewModel.backBitmap, null, null, RENDER_MODE_FOR_DISPLAY)
            viewModel.currentNode.bmp = viewModel.backBitmap!!
        }

        pathCanvas = Canvas(viewModel.frontBitmap)
        pathCanvas.drawColor(Color.TRANSPARENT)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas!!.scale(viewModel.transformer.scaleFactor, viewModel.transformer.scaleFactor)
        canvas.translate(viewModel.transformer.translateX, viewModel.transformer.translateY)

        canvas.drawBitmap(viewModel.backBitmap, 0f, 0f, null)
        canvas.drawBitmap(viewModel.frontBitmap, 0f, 0f, null)

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) return false
        if (mode == CanvasMode.MODE_NAV) {
            scaleGestureDetector.onTouchEvent(event)
            if (scaleGestureDetector.isInProgress) return false
            gestureDetector.onTouchEvent(event)
        }
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> touchStart(x, y)
            MotionEvent.ACTION_MOVE -> {
                touchMove(x, y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> touchUp()
            else -> {
            }
        }
        return true
    }

    private fun touchUp() {
        if (mode == CanvasMode.MODE_DRAW) {
            if (viewModel.isCanvasFresh.value!!) {
                viewModel.currentNode = viewModel.tree.addNewNodeAt(viewModel.currentNode)
                viewModel.isCanvasFresh.value = false
            }
            viewModel.currentNode.bmp = viewModel.frontBitmap!!
            path.reset()
        }
    }

    private fun touchMove(x: Float, y: Float) {
        val (transformedX, transformedY) = viewModel.transformer.transform(Coord(x, y))
        val dx = transformedX - currX
        val dy = transformedY - currY
        if (abs(dx) >= touchTolerance || abs(dy) >= touchTolerance) {
            when (mode) {
                CanvasMode.MODE_NAV -> {
/*                    transformer.translateX += dx
                    transformer.translateY += dy
                    Log.d("TAGIT", "Translated by ($dx, $dy)")*/
                }
                CanvasMode.MODE_DRAW -> {
                    path.lineTo(transformedX, transformedY)
                    pathCanvas.drawPath(path, paint)
                }
            }
            currX = transformedX
            currY = transformedY
        }
    }

    private fun touchStart(x: Float, y: Float) {
        val (transformedX, transformedY) = viewModel.transformer.transform(Coord(x, y))
        if (mode == CanvasMode.MODE_DRAW) path.moveTo(transformedX, transformedY)
        currX = transformedX
        currY = transformedY

    }

    fun reinitFrontBitmap() {
        viewModel.frontBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        pathCanvas = Canvas(viewModel.frontBitmap)
    }

    inner class ZoomListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector?): Boolean {
            if (detector == null) return false
            val oldScaleFactor = viewModel.transformer.scaleFactor
            viewModel.transformer.scaleFactor *= smoothing(detector.scaleFactor)
            viewModel.transformer.scaleFactor = max(1f, min(viewModel.transformer.scaleFactor, 5f))
            val adjustedScaleFactor = viewModel.transformer.scaleFactor / oldScaleFactor
            val modifier = (1 / adjustedScaleFactor - 1) / oldScaleFactor // just getting the right center of zoom
            viewModel.transformer.translateX += detector.focusX * modifier
            viewModel.transformer.translateY += detector.focusY * modifier
            Log.d("TAGIT", "Zoomed by ${detector.scaleFactor}")
            invalidate()
            return super.onScale(detector)


        }

        private fun smoothing(x: Float) = if (x > 1) (5f / 4) else (4f / 5)
    }

    inner class ScrollListener : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            viewModel.transformer.translateX -= distanceX / viewModel.transformer.scaleFactor
            viewModel.transformer.translateY -= distanceY / viewModel.transformer.scaleFactor
            Log.d("TAGIT", "Translated by (${distanceX / viewModel.transformer.scaleFactor}, ${distanceY / viewModel.transformer.scaleFactor}")
            return super.onScroll(e1, e2, distanceX, distanceY)
        }
    }

    data class Coord(val x: Float, val y: Float)


}

class CanvasTransformer(var scaleFactor: Float = 1f, var translateX: Float = 0f, var translateY: Float = 0f) {
    fun transform(coord: HistoryTreeView.Coord): HistoryTreeView.Coord {
        val (x, y) = coord
        return HistoryTreeView.Coord(
                affine(scaleFactor, translateX, x),
                affine(scaleFactor, translateY, y)
        )
    }

    private fun affine(a: Float, b: Float, x: Float): Float = ((x / a) - b)
}

