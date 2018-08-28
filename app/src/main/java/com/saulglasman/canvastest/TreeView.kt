package com.saulglasman.canvastest

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.GestureDetector
import android.view.KeyEvent.ACTION_DOWN
import android.view.MotionEvent
import android.widget.ImageView
import java.lang.Math.abs



class TreeView(context: Context, val viewModel: HistoryTreeViewModel, val listener: TreeViewListener) : ImageView(context) {

    private val paint = Paint()
    var nodeToCoordMap: Map<BmpTree.TreeNode, Pair<Float, Float>> = mapOf()
    var coordToNodeMap: Map<Pair<Float, Float>, BmpTree.TreeNode> = mapOf()

    private val treeViewGestureDetector: GestureDetector = GestureDetector(context, TreeViewGestureListener())

    inner class TreeViewGestureListener : GestureDetector.SimpleOnGestureListener() {

        private fun findCloseNode(e: MotionEvent?): BmpTree.TreeNode? {
            if (e == null) return null
            val coordMatches = coordToNodeMap.keys.filter { Pair(e.x, e.y).isCloseEnoughTo(it) } // is the touch close to a tree node?
            return if (coordMatches.isNotEmpty()) coordToNodeMap[coordMatches[0]] // it will only have one entry
            else null
        }

        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onLongPress(e: MotionEvent?) {
            val possibleNode = findCloseNode(e)
            if (possibleNode != null) {
                listener.deleteNode(possibleNode)
            }
        }

        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            val possibleNode = findCloseNode(e)
            if (possibleNode != null) {
                listener.changeToNode(possibleNode)
                return true
            }
            return false
        }
    }

    interface TreeViewListener {
        fun deleteNode(node: BmpTree.TreeNode)
        fun changeToNode(node: BmpTree.TreeNode)
    }

    init {
        with(paint) {
            color = android.graphics.Color.BLACK
            strokeWidth = 12f
        }
        refreshNodeCoordMaps()
    }

    private fun refreshNodeCoordMaps() {
        nodeToCoordMap = viewModel.tree.nodes
                .associateBy({ it }, { getCanvasCoords(it) })
        coordToNodeMap = viewModel.tree.nodes
                .associateBy({ getCanvasCoords(it) }, { it })
    }

    override fun onDraw(canvas: Canvas?) {
        if (canvas != null) {
            refreshNodeCoordMaps()
            drawTree(canvas, viewModel.tree)
        }
    }

    fun getCanvasCoords(node: BmpTree.TreeNode): Pair<Float, Float> {
        val layerHeight = viewModel.tree.nodesAtDepth(node.coords.first)
        Log.d("TAGIT", "Drawing node at (${node.coords.first}, ${node.coords.second})")
        return Pair(width.toFloat() * (node.coords.first + 1) / (viewModel.tree.depth + 1),
                height.toFloat() * (node.coords.second + 1) / (layerHeight + 1))
    }

    fun drawTree(canvas: Canvas, tree: BmpTree) {
        val circlePaint = Paint()
        with(circlePaint) {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }
        val linePaint = Paint()
        with(linePaint) {
            style = Paint.Style.STROKE
            strokeWidth = 6f
        }

        tree.nodes.forEach {
            val (xEnd, yEnd) = nodeToCoordMap[it]!!
            canvas.drawCircle(xEnd, yEnd, 12f, circlePaint)
            if (it.parent != null) {
                linePaint.color = it.color ?: viewModel.drawColor.value ?: Color.BLACK
                val (xStart, yStart) = nodeToCoordMap[it.parent]!!
                canvas.drawLine(xStart + 10f, yStart, xEnd - 10f, yEnd, linePaint)
            }
        }

        val (xCurrent, yCurrent) = nodeToCoordMap[viewModel.currentNode]!!
        canvas.drawCircle(xCurrent, yCurrent, 8f, circlePaint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return treeViewGestureDetector.onTouchEvent(event)
    }

    private fun Pair<Float, Float>.isCloseEnoughTo(other: Pair<Float, Float>): Boolean {
        return (abs(this.first - other.first) <= TREEVIEW_CLICK_TOLERANCE && abs(this.second - other.second) <= TREEVIEW_CLICK_TOLERANCE) //use l^1 distance, why not
    }

}
