package com.saulglasman.canvastest

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
import com.google.android.material.snackbar.Snackbar.make
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick

@Suppress("EXPERIMENTAL_FEATURE_WARNING", "NestedLambdaShadowedImplicitParameter")
class MainActivity : AppCompatActivity(), TreeView.TreeViewListener, HistoryTreeView.HistoryTreeViewListener {


    lateinit var viewModel: HistoryTreeViewModel

    lateinit var zoomView: HistoryTreeView
    lateinit var editButton: Button
    lateinit var commitButton: Button
    lateinit var branchButton: Button
    lateinit var showHideTreeButton: Button
    lateinit var colorChangeButton: ImageView
    lateinit var undoButton: Button
    lateinit var redoButton: Button
    lateinit var undoRedoButtonBar: LinearLayout
    lateinit var treeView: TreeView
    lateinit var colorSelectDialog: DialogInterface

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 1)
        }
        viewModel = ViewModelProviders.of(this).get(HistoryTreeViewModel::class.java)
        relativeLayout {
            zoomView = historyTreeView(viewModel, this@MainActivity) {
                id = ID_MAINVIEW
            }.lparams {
                alignParentTop()
                width = matchParent
                above(ID_TREEVIEW)
            }
            relativeLayout {
                id = ID_BUTTONBAR
                editButton = button {
                    id = ID_EDITBUTTON
                    onClick {
                        viewModel.isEditing.value = !(viewModel.isEditing.value!!)
                    }
                }.lparams { alignParentLeft() }
                commitButton = button("Commit") {
                    id = ID_COMMITBUTTON
                    onClick {
                        viewModel.currentNode.bmp = overlayBmpList(viewModel.currentNode.undoRedoStack.take(viewModel.currentNode.stackPointer)) //necessary in case there have been some undos
                        if (viewModel.currentNode.bmp == null) {
                            make(zoomView, "Nothing to commit", LENGTH_SHORT).show()
                        } else {
                            viewModel.arrangeBmps()
                            viewModel.currentNode.markInactive()
                            viewModel.isCommitted.value = true
                            viewModel.isEditing.value = false
                            zoomView.resetUndoRedoStack()
                        }
                    }
                }.lparams { rightOf(ID_EDITBUTTON) }
/*                branchButton = button("New branch") {
                    onClick {
                        var colorToSet: Int = zoomView.paint.color
                        alert("Select color:") {
                            customView {
                                linearLayout {
                                    padding = dip(50)
                                    button {
                                        setBackgroundColor(Color.BLACK)
                                        onClick {
                                            colorToSet = Color.BLACK
                                        }
                                    }.lparams {
                                        height = matchParent
                                        width = dip(50)
                                    }
                                    button {
                                        setBackgroundColor(Color.RED)
                                        onClick {
                                            colorToSet = Color.RED
                                        }
                                    }.lparams {
                                        height = matchParent
                                        width = dip(50)
                                    }
                                }
                            }
                            yesButton {
                                zoomView.paint.color = colorToSet
                            }
                            noButton {
                            }
                        }.show()
                    }
                }*/
                showHideTreeButton = button {
                    id = ID_TREEBUTTON
                    onClick { viewModel.isTreeShown.value = !viewModel.isTreeShown.value!! }
                }.lparams { rightOf(ID_COMMITBUTTON) }
                colorChangeButton = imageView {
                    id = ID_COLORCHANGEBUTTON
                    image = getDrawable(R.drawable.square)
                    padding = dip(4)
                    onClick {
                        colorSelectDialog = alert {
                            customView {
                                verticalLayout {
                                    textView("Select color:") {
                                        id = ID_ALERTSELECTCOLORTEXT
                                    }.lparams {
                                        width = matchParent
                                        height = wrapContent
                                        padding = dip(16)
                                    }
                                    linearLayout {
                                        COLORS.forEach { color ->
                                            colorSelectButton(color) {
                                                onClick {
                                                    viewModel.drawColor.value = color
                                                    colorSelectDialog.dismiss()
                                                }
                                            }.lparams {
                                                padding = dip(4)
                                                rightMargin = dip(36)
                                            }
                                        }
                                    }.lparams {
                                        width = matchParent
                                        height = wrapContent
                                        margin = dip(16)
                                    }
                                }
                            }
                        }.show() // show color change dialog
                    }
                }.lparams {
                    height = dip(36)
                    width = dip(36)
                    alignParentRight()
                    centerVertically()
                    rightMargin = dip(4)
                }
            }.lparams {
                width = matchParent
                height = wrapContent
                alignParentBottom()
            }
            undoRedoButtonBar = linearLayout {
                id = ID_UNDOREDOBAR
                visibility = GONE
                undoButton = button("Undo") {
                    onClick {
                        if (viewModel.currentNode.stackPointer > 0) {
                            viewModel.currentNode.stackPointer--
                            zoomView.invalidate()
                        }
                    }
                }
                redoButton = button("Redo") {
                    onClick {
                        if (viewModel.currentNode.stackPointer < viewModel.currentNode.undoRedoStack.size) {
                            viewModel.currentNode.stackPointer++
                            zoomView.invalidate()
                        }
                    }
                }
            }.lparams {
                width = matchParent
                height = wrapContent
                above(ID_BUTTONBAR)
            }
            treeView = treeView(viewModel, this@MainActivity) {
                id = ID_TREEVIEW
                visibility = GONE
            }.lparams {
                above(ID_UNDOREDOBAR)
                width = matchParent
                height = dip(100)
            }
        }

        //Listeners

        viewModel.isEditing.observe(this, Observer
        {
            if (it) {
                editButton.text = "Stop editing"
                zoomView.mode = CanvasMode.MODE_DRAW
                undoRedoButtonBar.visibility = VISIBLE
            } else {
                editButton.text = "Edit"
                zoomView.mode = CanvasMode.MODE_NAV
                undoRedoButtonBar.visibility = GONE
            }
        })
        viewModel.isTreeShown.observe(this, Observer
        {
            if (it) {
                showHideTreeButton.text = "Hide tree"
                treeView.visibility = VISIBLE
            } else {
                showHideTreeButton.text = "Show tree"
                treeView.visibility = GONE
            }
        })
/*        viewModel.isCommitted.observe(this, Observer {
            if (it) {
                commitButton.visibility = GONE
                branchButton.visibility = VISIBLE
            } else {
                commitButton.visibility = VISIBLE
                branchButton.visibility = GONE
            }
        })*/
/*
        viewModel.isCanvasFresh.observe(this, Observer
        {
            if (it) {
                if (zoomView.height > 0) { // make sure the view has actually been inflated before trying to reset the bitmap
                    zoomView.resetUndoRedoStack()
                }
                zoomView.invalidate()
            }
            treeView.invalidate()
            if (viewModel.currentNode.color == null) viewModel.currentNode.color = viewModel.drawColor.value!!
        })
*/
        viewModel.drawColor.observe(this, Observer {
            zoomView.paint.color = viewModel.drawColor.value ?: zoomView.paint.color
            if (viewModel.currentNode.isActive) { // we can only change the color of a node if we're still working on it
                viewModel.currentNode.color = viewModel.drawColor.value
                viewModel.currentNode.undoRedoStack.forEach {
                    it.changeColor(zoomView.paint.color)
                }
                /* there's a problem here: if we return to an active node from elsewhere, we can't change the color any more. I need to implement
                 * preservation of the undo/redo stack under change of node.
                 */
                zoomView.invalidate()
                treeView.invalidate()
            }
        })
    }

    // Interface functions from child views

    override fun deleteNode(node: BmpTree.TreeNode) {
        if (node == viewModel.tree.rootNode) {
            return
        }
        alert("Delete this node and all its descendants? This cannot be undone.") {
            positiveButton("Proceed") {
                if (viewModel.tree.getLineage(viewModel.currentNode).contains(node)) { // would the current node be deleted?
                    viewModel.currentNode = node.parent!! // if so, move it back to the parent of the node to be deleted
                    viewModel.reset()
                }
                viewModel.tree.getDescendantsIncludingSelf(node).forEach {
                    viewModel.tree.nodes.remove(it)
                    treeView.invalidate()
                }
            }
            negativeButton("Back") {}
        }.show()
    }

    override fun changeToNode(node: BmpTree.TreeNode) {
        viewModel.currentNode = node
        zoomView.invalidate()
        treeView.invalidate()
        viewModel.isEditing.value = false
        if (node.isActive) viewModel.drawColor.value = node.color
    }

    override fun addNewNodeAt(node: BmpTree.TreeNode) {
        viewModel.currentNode = viewModel.tree.addNewNodeAt(node, zoomView.paint.color)
        treeView.invalidate()
    }
}

