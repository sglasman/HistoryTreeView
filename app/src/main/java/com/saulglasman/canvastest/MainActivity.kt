package com.saulglasman.canvastest

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
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
import java.io.*
import java.lang.Integer.parseInt

@Suppress("EXPERIMENTAL_FEATURE_WARNING", "NestedLambdaShadowedImplicitParameter")
class MainActivity : AppCompatActivity(), TreeView.TreeViewListener, HistoryTreeView.HistoryTreeViewListener {

    lateinit var viewModel: HistoryTreeViewModel

    lateinit var zoomView: HistoryTreeView
    lateinit var editButton: ImageView
    lateinit var commitButton: ImageView
    lateinit var colorChangeButton: ImageView
    lateinit var undoButton: ImageView
    lateinit var redoButton: ImageView
    lateinit var treeView: TreeView
    lateinit var miniTreeView: TreeView
    lateinit var colorSelectDialog: DialogInterface

    var filename = "sample"
    val TAG = this::class.java.simpleName

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 1)
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 2)
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
            miniTreeView = treeView(viewModel, this@MainActivity, false).lparams {
                width = dip(100)
                height = dip(40)
                Log.d(TAG, "($width, $height)")
                alignParentTop()
                alignParentRight()
            }
            relativeLayout {
                id = ID_BUTTONBAR
                backgroundColor = Color.LTGRAY
                editButton = imageView {
                    id = ID_EDITBUTTON
                    image = getDrawable(R.drawable.ic_pencil)
                    padding = dip(4)
                    onClick {
                        viewModel.isEditing.value = !(viewModel.isEditing.value!!)
                    }
                }.lparams {
                    alignParentLeft()
                    padding = dip(12)
                    rightMargin = dip(32)
                }
                commitButton = imageView {
                    id = ID_COMMITBUTTON
                    visibility = GONE
                    image = getDrawable(R.drawable.ic_check_mark)
                    padding = dip(4)
                    onClick {
                        viewModel.currentNode.bmp = overlayBmpList(viewModel.currentNode.undoRedoStack.take(viewModel.currentNode.stackPointer)) //necessary in case there have been some undos
                        if (viewModel.currentNode.bmp == null) {
                            make(zoomView, "Nothing to commit", LENGTH_SHORT).show()
                        } else {
                            viewModel.currentNode.markInactive()
                            viewModel.arrangeBmps()
                            viewModel.isCommitted.value = true
                            viewModel.isEditing.value = false
                            zoomView.resetUndoRedoStack()
                        }
                    }
                }.lparams {
                    rightOf(ID_EDITBUTTON)
                    padding = dip(12)
                    rightMargin = dip(32)
                }
                undoButton = imageView {
                    id = ID_UNDOBUTTON
                    isEnabled = false
                    image = getDrawable(R.drawable.ic_undo_arrow_pale)
                    padding = dip(4)
                    onClick {
                        if (viewModel.currentNode.stackPointer > 0) {
                            viewModel.currentNode.stackPointer--
                            zoomView.invalidate()
                            enableDisableUndoRedoButtons()
                        }
                    }
                }.lparams {
                    rightOf(ID_COMMITBUTTON)
                    padding = dip(12)
                    rightMargin = dip(32)
                }
                redoButton = imageView {
                    id = ID_REDOBUTTON
                    isEnabled = false
                    image = getDrawable(R.drawable.ic_redo_arrow_pale)
                    padding = dip(4)
                    onClick {
                        if (viewModel.currentNode.stackPointer < viewModel.currentNode.undoRedoStack.size) {
                            viewModel.currentNode.stackPointer++
                            zoomView.invalidate()
                            enableDisableUndoRedoButtons()
                        }
                    }
                }.lparams {
                    rightOf(ID_UNDOBUTTON)
                    padding = dip(12)
                    rightMargin = dip(32)
                }
/*                imageView {
                    image = getDrawable(R.drawable.ic_save)
                    padding = dip(4)
                    onClick {
                        viewModel.arrangeBmps()
                        doAsync {
                            writeOutCurrentTree()
                        }
                    }
                }.lparams {
                    rightOf(ID_TREEBUTTON)
                    padding = dip(12)
                    rightMargin = dip(32)
                }*/
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
                                        padding = dip(12)

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
                    height = dip(30)
                    width = dip(30)
                    alignParentRight()
                    padding = dip(12)
                    leftMargin = dip(32)
                    centerVertically()
                }
            }.lparams {
                width = matchParent
                height = wrapContent
                alignParentBottom()
            }
            treeView = treeView(viewModel, this@MainActivity, true) {
                id = ID_TREEVIEW
                visibility = GONE
            }.lparams {
                above(ID_BUTTONBAR)
                width = matchParent
                height = dip(100)
            }
        }


        //Listeners

        viewModel.isEditing.observe(this, Observer
        {
            if (it) {
                editButton.backgroundColor = Color.WHITE
                zoomView.mode = CanvasMode.MODE_DRAW
                undoButton.visibility = VISIBLE
                redoButton.visibility = VISIBLE
                commitButton.visibility = VISIBLE
            } else {
                editButton.backgroundColor = Color.TRANSPARENT
                zoomView.mode = CanvasMode.MODE_NAV
                undoButton.visibility = GONE
                redoButton.visibility = GONE
                commitButton.visibility = GONE
            }
        })
        viewModel.isTreeShown.observe(this, Observer
        {
            if (it) {
                treeView.visibility = VISIBLE
            } else {
                treeView.visibility = GONE
            }
        })
        viewModel.drawColor.observe(this, Observer {
            zoomView.paint.color = viewModel.drawColor.value ?: zoomView.paint.color
            if (viewModel.currentNode.isActive) { // we can only change the color of a node if we're still working on it
                viewModel.currentNode.color = viewModel.drawColor.value
                viewModel.currentNode.undoRedoStack.forEach {
                    it.changeColor(zoomView.paint.color)
                }
                zoomView.invalidate()
                invalidateTreeViews()
                
            }
        })
        viewModel.undoEnabled.observe(this, Observer {
            if (it) {
                undoButton.image = getDrawable(R.drawable.ic_undo_arrow)
                undoButton.isEnabled = true
            }
            else {
                undoButton.image = getDrawable(R.drawable.ic_undo_arrow_pale)
                undoButton.isEnabled = false
            }
        })
        viewModel.redoEnabled.observe(this, Observer {
            if (it) {
                redoButton.image = getDrawable(R.drawable.ic_redo_arrow)
                redoButton.isEnabled = true
            }
            else {
                redoButton.image = getDrawable(R.drawable.ic_redo_arrow_pale)
                redoButton.isEnabled = false
            }
        })
    }

    fun invalidateTreeViews() {
        treeView.invalidate()
        miniTreeView.invalidate()
    }

    // Interface functions from child views

    override fun enableDisableUndoRedoButtons() {
        viewModel.redoEnabled.value = viewModel.currentNode.stackPointer < viewModel.currentNode.undoRedoStack.size
        viewModel.undoEnabled.value = viewModel.currentNode.stackPointer > 0
    }

    override fun deleteNode(node: BmpTree.TreeNode) {
        if (node == viewModel.tree.rootNode) {
            return
        }
        alert("Delete this node and all its descendants? This cannot be undone.") {
            positiveButton("Proceed") {
                if (viewModel.tree.getLineage(viewModel.currentNode).contains(node)) { // would the current node be deleted?
                    changeToNode(node.parent!!) // if so, move it back to the parent of the node to be deleted
                    viewModel.reset()
                }
                viewModel.tree.getDescendantsIncludingSelf(node).forEach {
                    viewModel.tree.nodes.remove(it)
                    invalidateTreeViews()
                    
                }
            }
            negativeButton("Back") {}
        }.show()
    }

    override fun changeToNode(node: BmpTree.TreeNode) {
        viewModel.currentNode = node
        viewModel.arrangeBmps()
        zoomView.invalidate()
        invalidateTreeViews()
        
        if (!node.isActive) viewModel.isEditing.value = false
        if (node.isActive) viewModel.drawColor.value = node.color
        enableDisableUndoRedoButtons()
    }

    override fun addNewNodeAt(node: BmpTree.TreeNode) {
        viewModel.currentNode = viewModel.tree.addNewNodeAt(node, zoomView.paint.color)
        invalidateTreeViews()
        
        enableDisableUndoRedoButtons()
    }

    override fun showHideTreeView() {
        viewModel.isTreeShown.value = !viewModel.isTreeShown.value!!
    }

    // Filesystem related functions

    private fun writeOutCurrentTree() {
        viewModel.tree.nodes.forEach {
            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(File(filesDir, "${filename}_${it.coords.first}_${it.coords.second}"))
                it.bmp?.compress(Bitmap.CompressFormat.PNG, 100, fos)
                Log.d(TAG, "Wrote bitmap (${it.coords.first}, ${it.coords.second})")
            } catch (error: Throwable) {
                Log.e(TAG, "Error writing bitmap file", error)
            } finally {
                fos?.close()
            }
        }
        var dataFos: FileOutputStream? = null
        var dataOos: ObjectOutputStream? = null
        try {
            dataFos = FileOutputStream(File(filesDir, "${filename}_data"))
            dataOos = ObjectOutputStream(dataFos)
            dataOos.writeObject(viewModel.tree)
            Log.d(TAG, "Wrote data file")
        } catch (error: Throwable) {
            Log.e(TAG, "Error writing data file", error)
        } finally {
            dataFos?.close()
            dataOos?.close()
        }
        Log.d(TAG, "Files in data directory: ${
        filesDir.listFiles().map { it.name }
        }")
    }

    private fun loadTree(): BmpTree {
        val regex = Regex(FILE_REGEX_STRING)
        var dataFis: FileInputStream? = null
        var dataOis: ObjectInputStream? = null
        var tree = BmpTree()
        try {
            dataFis = FileInputStream(File(filesDir, "${filename}_data"))
            dataOis = ObjectInputStream(dataFis)
            tree = dataOis.readObject() as BmpTree
            tree.nodes.forEach {
                it.undoRedoStack = mutableListOf()
                it.stackPointer = 0
                it.isActive = false
            }
            Log.d(TAG, "Successfully loaded data file. Nodes: ${viewModel.tree.nodes.size}.")
        } catch (error: Throwable) {
            Log.e(TAG, "Error reading data file", error)
        } finally {
            dataFis?.close()
            dataOis?.close()
        }
        filesDir.listFiles().forEach {
            var fis: FileInputStream? = null
            if (regex.matchEntire(it.name) != null) {
                val (name, coord1, coord2) = regex.matchEntire(it.name)!!.destructured
                Log.d(TAG, "File found: $name, $coord1, $coord2")
                if (name == filename) {
                    try {
                        fis = FileInputStream(it)
                        val bitmap = BitmapFactory.decodeFile(it.path, BitmapFactory.Options().apply { inMutable = true })
                        if (tree.nodeAtCoords(Pair(parseInt(coord1), parseInt(coord2))) != null) Log.d(TAG, "Yes: $coord1, $coord2")
                        tree.nodeAtCoords(Pair(parseInt(coord1), parseInt(coord2)))?.bmp = bitmap
                    } catch (error: Throwable) {
                        Log.e(TAG, "Error reading file ${it.name}", error)
                    } finally {
                        fis?.close()
                    }
                }
            }
        }
        return tree
    }
}