package com.saulglasman.canvastest

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log

/*class BmpTree(var nodeBmp: Bitmap? = null, var kids: MutableList<BmpTree> = mutableListOf()) : Serializable {
    fun addKid(kid: BmpTree) {
        kids.add(kid)
    }

    fun layerSizes(): List<Int> = listOf(1).plus(
            kids.map { layerSizes() }.fold(listOf<Int>()) { acc, i -> listAdd(acc, i) }
    )

    companion object {
        fun listAdd(acc: List<Int>, i: List<Int>): List<Int> =
                if (acc.size >= i.size) listAddAsymm(acc, i) else listAddAsymm(i, acc)

        private fun listAddAsymm(longer: List<Int>, shorter: List<Int>): List<Int> {
            val l = longer.size
            val s = shorter.size
            val longerStart = longer.slice(IntRange(0, s - 1))
            val longerEnd = longer.slice(IntRange(s, l - 1))
            return longerStart.zip(shorter) { a, b -> a + b }
                    .plus(longerEnd)
        }
    }
}*/

/*val testTree: BmpTree = BmpTree(mutableListOf(mutableListOf<Bitmap?>(null, null), mutableListOf<Bitmap?>(null, null, null)),
        mutableListOf(mutableListOf(Pair(0, 0), Pair(0, 1), Pair(1, 2))))*/

class BmpTree(val nodes: MutableList<TreeNode> = mutableListOf(TreeNode(null, null, Pair(0, 0)))) {

    /* `nodes` is a list of tree nodes, each of which carries the data of its parent (unless it's the root),
     * its position in the plane and maybe a bitmap.
     *
     * Storing the tree in this non-recursive way is unusual, but convenient when it comes to writing the draw function. */

    val depth: Int
        get() = nodes.map { it.coords.first }.max()!! + 1

    fun nodesAtDepth(i: Int): Int = nodes.filter { it.coords.first == i }.size
    fun nodeAtCoords(coords: Pair<Int, Int>): TreeNode? = nodes.find { it.coords == coords }

    fun getLineageExceptRoot(node: TreeNode): List<TreeNode> {
        if (node.parent == null) return listOf()
        return getLineageExceptRoot(node.parent).plus(node)
    }

    fun addNewNodeAt(currentNode: TreeNode): TreeNode {
        val (i, j) = currentNode.coords
        val nodesAboveNewNode: List<TreeNode> = nodes.filter { it.coords.first == i && it.coords.second <= j }
                .flatMap { getChildren(it) }
        val newNodeYCoord = if (nodesAboveNewNode.isEmpty()) 0 else (nodesAboveNewNode.map { it.coords.second }.max()!!) + 1 //figure out where to slot in the new node in the plane layout of the tree
        nodes.filter { it.coords.first == i + 1 && it.coords.second >= newNodeYCoord }
                .forEach { it.coords = Pair(it.coords.first, it.coords.second + 1) } //shuffle along the remaining nodes in the column
        val newNode = TreeNode(parent = currentNode, coords = Pair(i + 1, newNodeYCoord))
        nodes.add(newNode)
        return newNode
    }

    private fun getChildren(node: TreeNode): List<TreeNode> = nodes.filter { it.parent == node }

    data class TreeNode(var bmp: Bitmap? = null, val parent: TreeNode? = null, var coords: Pair<Int, Int>)
}


