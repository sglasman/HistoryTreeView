package com.saulglasman.canvastest

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import java.lang.Math.abs

@SuppressLint("ViewConstructor")
class HistoryTreeView(context: Context, viewModel: HistoryTreeViewModel, val listener: HistoryTreeViewListener) : TransformableView(context, viewModel) {

    val TAG = HistoryTreeView::class.java.simpleName
    val paint = Paint()
    val path = Path()
    lateinit var pathCanvas: Canvas
    var currX: Float = 0f
    var currY: Float = 0f
    lateinit var frame: Rect

    init {
        with(paint) {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = STROKE_WIDTH
            strokeCap = Paint.Cap.ROUND
        }
        viewModel.enableDisablePgButtons()
    }

    interface HistoryTreeViewListener {
        fun addNewNodeAt(node: BmpTree.TreeNode)
        fun invalidateBackView()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas!!.drawBitmap(overlayBmpList(viewModel.currentNode.undoRedoStack.take(viewModel.currentNode.stackPointer)), 0f, 0f, paint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) return false
        if (viewModel.mode == CanvasMode.MODE_NAV) {
            scaleGestureDetector.onTouchEvent(event)
            if (scaleGestureDetector.isInProgress) return false
            gestureDetector.onTouchEvent(event)
            listener.invalidateBackView()
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
        if (viewModel.mode == CanvasMode.MODE_DRAW) {
            viewModel.currentNode.bmp = overlayBmpList(viewModel.currentNode.undoRedoStack)
            path.reset()
        }
    }

    private fun touchMove(x: Float, y: Float) {
        val (transformedX, transformedY) = viewModel.transformer.transform(Coord(x, y))
        val dx = transformedX - currX
        val dy = transformedY - currY
        if (abs(dx) >= touchTolerance || abs(dy) >= touchTolerance) {
            when (viewModel.mode) {
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
        if (viewModel.mode == CanvasMode.MODE_DRAW) {

            if (!viewModel.currentNode.isActive) {
                listener.addNewNodeAt(viewModel.currentNode)
            }
            if (viewModel.currentNode.undoRedoStack.isNotEmpty()) {
                viewModel.currentNode.undoRedoStack = viewModel.currentNode.undoRedoStack.take(viewModel.currentNode.stackPointer).toMutableList()
            }
            viewModel.currentNode.undoRedoStack.add(Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888))
            pathCanvas = Canvas(viewModel.currentNode.undoRedoStack[viewModel.currentNode.stackPointer])
            viewModel.currentNode.stackPointer++
            viewModel.enableDisableUndoRedoButtons()

            pathCanvas.drawPoint(transformedX, transformedY, paint)

            path.moveTo(transformedX, transformedY)
            currX = transformedX
            currY = transformedY

            viewModel.currentNode.altered = true // node will be saved out when navigating away from page

            invalidate()
        }
    }

    fun resetUndoRedoStack() {
        viewModel.currentNode.undoRedoStack = mutableListOf()
        viewModel.currentNode.stackPointer = 0
    }
}
