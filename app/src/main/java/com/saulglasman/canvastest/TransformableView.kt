package com.saulglasman.canvastest

import android.content.Context
import android.graphics.Canvas
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.ImageView
import kotlin.math.pow

abstract class TransformableView(context: Context, val viewModel: HistoryTreeViewModel): ImageView(context) {

    val scaleGestureDetector: ScaleGestureDetector = ScaleGestureDetector(context, ZoomListener())
    val gestureDetector: GestureDetector = GestureDetector(context, ScrollListener())

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas!!.scale(viewModel.transformer.scaleFactor, viewModel.transformer.scaleFactor)
        canvas.translate(viewModel.transformer.translateX, viewModel.transformer.translateY)
    }

    inner class ZoomListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector?): Boolean {
            if (detector == null) return false
            val oldScaleFactor = viewModel.transformer.scaleFactor
            viewModel.transformer.scaleFactor *= smoothing(detector.scaleFactor)
            viewModel.transformer.scaleFactor = Math.max(1f, Math.min(viewModel.transformer.scaleFactor, 5f))
            val adjustedScaleFactor = viewModel.transformer.scaleFactor / oldScaleFactor
            val modifier = (1 / adjustedScaleFactor - 1) / oldScaleFactor // just getting the right center of zoom
            viewModel.transformer.translateX += detector.focusX * modifier
            viewModel.transformer.translateY += detector.focusY * modifier
            Log.d("TAGIT", "Zoomed by ${detector.scaleFactor}")
            invalidate()
            return false
        }

        private fun smoothing(x: Float) = x.pow(0.8f) // trial and error
    }

    inner class ScrollListener : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            viewModel.transformer.translateX -= distanceX / viewModel.transformer.scaleFactor
            viewModel.transformer.translateY -= distanceY / viewModel.transformer.scaleFactor
            Log.d("TAGIT", "Translated by (${distanceX / viewModel.transformer.scaleFactor}, ${distanceY / viewModel.transformer.scaleFactor}")
            invalidate()
            return super.onScroll(e1, e2, distanceX, distanceY)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (viewModel.mode == CanvasMode.MODE_NAV) {
            scaleGestureDetector.onTouchEvent(event)
            if (scaleGestureDetector.isInProgress) return false
            gestureDetector.onTouchEvent(event)
        }
        return true
    }

    data class Coord(val x: Float, val y: Float)
}