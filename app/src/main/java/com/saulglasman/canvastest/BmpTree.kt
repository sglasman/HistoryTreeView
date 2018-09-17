package com.saulglasman.canvastest

import android.graphics.Bitmap
import android.graphics.Color
import java.io.Serializable

class BmpTree(var nodes: MutableList<TreeNode> = mutableListOf(TreeNode(coords = Pair(0, 0), isActive = false, color = Color.BLACK))): Serializable {

    /* `nodes` is a list of tree nodes, each of which carries the data of its parent (unless it's the root),
     * its position in the plane and maybe a bitmap.
     *
     * Storing the tree in this non-recursive way is unusual, but convenient when it comes to writing the draw function. */
    val rootNode = nodes[0]
    val depth: Int
        get() = nodes.map { it.coords.first }.max()!! + 1

    fun nodesAtDepth(i: Int): Int = nodes.filter { it.coords.first == i }.size
    fun nodeAtCoords(coords: Pair<Int, Int>): TreeNode? = nodes.find { it.coords == coords }

    fun getLineage(node: TreeNode): List<TreeNode> {
        if (node.parent == null) return listOf(node)
        return getLineage(node.parent).plus(node)
    }

    fun addNewNodeAt(currentNode: TreeNode, newNodeColor: Int): TreeNode {
        val (i, j) = currentNode.coords
        val nodesAboveNewNode: List<TreeNode> = nodes.filter { it.coords.first == i && it.coords.second <= j }
                .flatMap { getChildren(it) }
        val newNodeYCoord = if (nodesAboveNewNode.isEmpty()) 0 else (nodesAboveNewNode.map { it.coords.second }.max()!!) + 1 //figure out where to slot in the new node in the plane layout of the tree
        nodes.filter { it.coords.first == i + 1 && it.coords.second >= newNodeYCoord }
                .forEach { it.coords = Pair(it.coords.first, it.coords.second + 1) } //shuffle along the remaining nodes in the column
        val newNode = TreeNode(parent = currentNode, coords = Pair(i + 1, newNodeYCoord), color = newNodeColor)
        nodes.add(newNode)
        currentNode.markInactive()
        return newNode
    }

    fun deleteNode(node: TreeNode) {
        val (i, j) = node.coords
        nodes.remove(node)
        nodes.filter {it.coords.first == i && it.coords.second > j}.forEach { it.coords = Pair(i, it.coords.second - 1) }
    }

    private fun getChildren(node: TreeNode): List<TreeNode> = nodes.filter { it.parent == node }

    fun getDescendantsIncludingSelf(node: TreeNode): List<TreeNode> = if (getChildren(node).isEmpty()) listOf(node)
    else getChildren(node).flatMap { getDescendantsIncludingSelf(it) }.plus(node)

    data class TreeNode(@Transient var bmp: Bitmap? = null, val parent: TreeNode? = null, var coords: Pair<Int, Int>,
                        var color: Int? = null, var isActive: Boolean = true, @Transient var undoRedoStack: MutableList<Bitmap> = mutableListOf(),
                        var stackPointer: Int = 0, var altered: Boolean = true): Serializable {

        fun markInactive() {
            this.isActive = false
            this.undoRedoStack.clear()
            this.stackPointer = 0
        }
    }
}