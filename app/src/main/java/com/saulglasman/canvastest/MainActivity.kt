package com.saulglasman.canvastest

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
import com.google.android.material.snackbar.Snackbar.make
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.io.*
import java.lang.Integer.parseInt

@Suppress("EXPERIMENTAL_FEATURE_WARNING", "NestedLambdaShadowedImplicitParameter")
class MainActivity : FilePickerActivity(), TreeView.TreeViewListener, HistoryTreeView.HistoryTreeViewListener, BackView.BackViewListener {

    lateinit var viewModel: HistoryTreeViewModel

    lateinit var mainView: HistoryTreeView
    lateinit var editButton: ImageView
    lateinit var commitButton: ImageView
    lateinit var colorChangeButton: ImageView
    lateinit var widthChangeButton: ImageView
    lateinit var depressedColorChangeButton: ImageView
    lateinit var depressedWidthChangeButton: ImageView
    lateinit var undoButton: ImageView
    lateinit var redoButton: ImageView
    lateinit var moreButton: ImageView
    lateinit var deleteButton: ImageView
    lateinit var pgUpButton: ImageView
    lateinit var pgDownButton: ImageView
    lateinit var openFileButton: ImageView
    lateinit var primaryButtonBar: RelativeLayout
    lateinit var secondaryButtonBar: RelativeLayout
    lateinit var colorSelectionBar: RelativeLayout
    lateinit var widthSelectionBar: RelativeLayout
    lateinit var colorButtons: LinearLayout
    lateinit var widthButtons: LinearLayout
    lateinit var treeView: TreeView
    lateinit var miniTreeView: TreeView
    lateinit var backView: BackView

    val TAG = MainActivity::class.java.simpleName

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
            frameLayout {
                backView = backView(viewModel, this@MainActivity)
                mainView = historyTreeView(viewModel, this@MainActivity) {
                    id = ID_MAINVIEW
                }
            }.lparams {
                alignParentTop()
                width = matchParent
                above(ID_TREEVIEW)
            }
            miniTreeView = treeView(viewModel, this@MainActivity, false).lparams {
                width = dip(100)
                height = dip(40)
                alignParentTop()
                alignParentRight()
            }
            frameLayout {
                id = ID_BUTTONFRAME
                primaryButtonBar = relativeLayout {
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
                        visibility = VISIBLE
                        image = getDrawable(R.drawable.ic_check_mark_pale)
                        padding = dip(4)
                        onClick {
                            viewModel.currentNode.bmp = overlayBmpList(viewModel.currentNode.undoRedoStack.take(viewModel.currentNode.stackPointer)) //necessary in case there have been some undos
                            if (viewModel.currentNode.bmp == null) {
                                make(mainView, "Nothing to commit", LENGTH_SHORT).show()
                            } else {
                                viewModel.currentNode.markInactive()
                                viewModel.arrangeBmps()
                                viewModel.isEditing.value = false
                                viewModel.isCommitButtonEnabled.value = false
                                mainView.resetUndoRedoStack()
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
                                mainView.invalidate()
                                viewModel.enableDisableUndoRedoButtons()
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
                                mainView.invalidate()
                                viewModel.enableDisableUndoRedoButtons()
                            }
                        }
                    }.lparams {
                        rightOf(ID_UNDOBUTTON)
                        padding = dip(12)
                        rightMargin = dip(32)
                    }
                    moreButton = imageView {
                        id = ID_MOREBUTTON
                        image = getDrawable(R.drawable.ic_ellipsis_h)
                        padding = dip(4)
                        onClick {
                            viewModel.isSecondaryButtonBarShown.value = !viewModel.isSecondaryButtonBarShown.value!!
                        }
                    }.lparams {
                        rightOf(ID_REDOBUTTON)
                        padding = dip(12)
                        rightMargin = dip(32)
                    }
                    widthChangeButton = imageView {
                        id = ID_WIDTHCHANGEBUTTON
                        image = getDrawable(R.drawable.ic_widths)
                        padding = dip(4)
                        onClick {
                            viewModel.buttonBarMode.value = ButtonBarMode.MODE_WIDTHSELECT
                        }
                    }.lparams {
                        height = dip(30)
                        width = dip(30)
                        leftOf(ID_COLORCHANGEBUTTON)
                        padding = dip(12)
                        leftMargin = dip(32)
                        centerVertically()
                    }
                    colorChangeButton = imageView {
                        id = ID_COLORCHANGEBUTTON
                        image = getDrawable(R.drawable.square)
                        padding = dip(4)
                        onClick {
                            viewModel.buttonBarMode.value = ButtonBarMode.MODE_COLORSELECT
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
                }
                colorSelectionBar = relativeLayout {
                    visibility = GONE
                    backgroundColor = Color.LTGRAY
                    colorButtons = linearLayout {
                        COLORS.forEach { color ->
                            colorSelectButton(color, viewModel) {
                                onClick {
                                    viewModel.drawColor.value = color
                                }
                            }.lparams {
                                padding = dip(4)
                                rightMargin = dip(36)
                            }
                        }
                    }.lparams {
                        width = wrapContent
                        height = wrapContent
                        alignParentLeft()
                    }
                    depressedColorChangeButton = imageView {
                        image = getDrawable(R.drawable.square)
                        backgroundColor = Color.WHITE
                        padding = dip(4)
                        onClick {
                            viewModel.buttonBarMode.value = ButtonBarMode.MODE_DEFAULT
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
                }
                widthSelectionBar = relativeLayout {
                    visibility = GONE
                    backgroundColor = Color.LTGRAY
                    widthButtons = linearLayout {
                        WIDTHS.forEach { width ->
                            widthSelectButton(width, viewModel) {
                                onClick {
                                    viewModel.drawWidth.value = width
                                }
                            }.lparams {
                                padding = dip(4)
                                rightMargin = dip(36)
                            }
                        }
                    }
                    depressedWidthChangeButton = imageView {
                        image = getDrawable(R.drawable.ic_widths)
                        backgroundColor = Color.WHITE
                        padding = dip(4)
                        onClick {
                            viewModel.buttonBarMode.value = ButtonBarMode.MODE_DEFAULT
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
                }
            }.lparams {
                width = matchParent
                height = wrapContent
                alignParentBottom()
            }

            secondaryButtonBar = relativeLayout {
                id = ID_SECONDARYBUTTONBAR
                backgroundColor = Color.LTGRAY
                visibility = GONE
                deleteButton = imageView {
                    id = ID_DELETEBUTTON
                    image = getDrawable(R.drawable.ic_recycle_bin_pale)
                    isEnabled = false
                    padding = dip(4)
                    onClick {
                        alert("Delete everything except base PDF? This cannot be undone.") {
                            positiveButton("Proceed") {
                                changeToNode(viewModel.tree.rootNode)
                                viewModel.tree.nodes = mutableListOf(viewModel.tree.rootNode)
                                viewModel.isDeleteButtonEnabled.value = false
                            }
                            negativeButton("Back") {}
                        }.show()
                    }
                }.lparams {
                    alignParentLeft()
                    padding = dip(12)
                    rightMargin = dip(32)
                }
                pgUpButton = imageView {
                    id = ID_PGUPBUTTON
                    image = getDrawable(R.drawable.ic_line_angle_up_pale)
                    isEnabled = false
                    padding = dip(4)
                    onClick {
                        saveTreeSync()
                        FileData.page--
                        loadTreeSync()
                        onPageChanged()
                    }
                }.lparams {
                    rightOf(ID_DELETEBUTTON)
                    padding = dip(12)
                    rightMargin = dip(32)
                }
                pgDownButton = imageView {
                    id = ID_PGDOWNBUTTON
                    image = getDrawable(R.drawable.ic_line_angle_down_pale)
                    isEnabled = false
                    padding = dip(4)
                    onClick {
                        saveTreeSync()
                        FileData.page++
                        loadTreeSync()
                        onPageChanged()
                    }
                }.lparams {
                    rightOf(ID_PGUPBUTTON)
                    padding = dip(12)
                    rightMargin = dip(32)
                }
                openFileButton = imageView {
                    id = ID_OPENFILEBUTTON
                    image = getDrawable(R.drawable.ic_pdf_file)
                    padding = dip(4)
                    onClick {
                        launchFilePicker()
                    }
                }.lparams {
                    rightOf(ID_PGDOWNBUTTON)
                    padding = dip(12)
                    rightMargin = dip(32)
                }
/*                button("Debug") {
                    onClick {
                        myDebug()
                    }
                }.lparams { rightOf(ID_PGDOWNBUTTON) }*/
            }.lparams {
                above(ID_BUTTONFRAME)
                width = matchParent
                height = wrapContent
            }
            treeView = treeView(viewModel, this@MainActivity, true) {
                id = ID_TREEVIEW
                visibility = GONE
            }.lparams {
                above(ID_SECONDARYBUTTONBAR)
                width = matchParent
                height = dip(100)
            }
        }
        loadTreeAsync()
        invalidateTreeViews()

        //Listeners

        viewModel.isEditing.observe(this, Observer
        {
            if (it) {
                editButton.backgroundColor = Color.WHITE
                viewModel.mode = CanvasMode.MODE_DRAW
                undoButton.visibility = VISIBLE
                redoButton.visibility = VISIBLE
                deleteButton.visibility = GONE
                moreButton.visibility = GONE
                viewModel.isSecondaryButtonBarShown.value = false
            } else {
                editButton.backgroundColor = Color.TRANSPARENT
                viewModel.mode = CanvasMode.MODE_NAV
                undoButton.visibility = GONE
                redoButton.visibility = GONE
                deleteButton.visibility = VISIBLE
                moreButton.visibility = VISIBLE
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
            mainView.paint.color = viewModel.drawColor.value ?: mainView.paint.color
            colorButtons.children.forEach { it.invalidate() }
            if (viewModel.currentNode.isActive) { // we can only change the color of a node if we're still working on it
                viewModel.currentNode.color = viewModel.drawColor.value
                viewModel.currentNode.undoRedoStack.forEach {
                    it.changeColor(mainView.paint.color)
                }
                mainView.invalidate()
                invalidateTreeViews()
            }
        })
        viewModel.drawWidth.observe(this, Observer {
            mainView.paint.strokeWidth = dip(viewModel.drawWidth.value!!).toFloat()
            widthButtons.children.forEach { it.invalidate() }
            mainView.invalidate()
        })
        viewModel.isUndoEnabled.observe(this, Observer {
            if (it) {
                undoButton.image = getDrawable(R.drawable.ic_undo_arrow)
                undoButton.isEnabled = true
            } else {
                undoButton.image = getDrawable(R.drawable.ic_undo_arrow_pale)
                undoButton.isEnabled = false
            }
        })
        viewModel.isRedoEnabled.observe(this, Observer {
            if (it) {
                redoButton.image = getDrawable(R.drawable.ic_redo_arrow)
                redoButton.isEnabled = true
            } else {
                redoButton.image = getDrawable(R.drawable.ic_redo_arrow_pale)
                redoButton.isEnabled = false
            }
        })
        viewModel.isDeleteButtonEnabled.observe(this, Observer {
            if (it) {
                deleteButton.image = getDrawable(R.drawable.ic_recycle_bin)
                deleteButton.isEnabled = true
            } else {
                deleteButton.image = getDrawable(R.drawable.ic_recycle_bin_pale)
                deleteButton.isEnabled = false
            }
        })
        viewModel.isCommitButtonEnabled.observe(this, Observer {
            if (it) {
                commitButton.image = getDrawable(R.drawable.ic_check_mark)
                commitButton.isEnabled = true
            } else {
                commitButton.image = getDrawable(R.drawable.ic_check_mark_pale)
                commitButton.isEnabled = false
            }
        })
        viewModel.isSecondaryButtonBarShown.observe(this, Observer {
            if (it) {
                viewModel.enableDisablePgButtons()
                moreButton.backgroundColor = Color.WHITE
                secondaryButtonBar.visibility = VISIBLE
            } else {
                moreButton.backgroundColor = Color.TRANSPARENT
                secondaryButtonBar.visibility = GONE
            }
        })
        viewModel.isPgDownButtonEnabled.observe(this, Observer {
            if (it) {
                pgDownButton.image = getDrawable(R.drawable.ic_line_angle_down)
                pgDownButton.isEnabled = true
            } else {
                pgDownButton.image = getDrawable(R.drawable.ic_line_angle_down_pale)
                pgDownButton.isEnabled = false
            }
        })
        viewModel.isPgUpButtonEnabled.observe(this, Observer {
            if (it) {
                pgUpButton.image = getDrawable(R.drawable.ic_line_angle_up)
                pgUpButton.isEnabled = true
            } else {
                pgUpButton.image = getDrawable(R.drawable.ic_line_angle_up_pale)
                pgUpButton.isEnabled = false
            }
        })
        viewModel.buttonBarMode.observe(this, Observer {
            if (it == ButtonBarMode.MODE_DEFAULT) {
                primaryButtonBar.visibility = VISIBLE
                colorSelectionBar.visibility = GONE
                widthSelectionBar.visibility = GONE
            } else if (it == ButtonBarMode.MODE_COLORSELECT) {
                primaryButtonBar.visibility = GONE
                colorSelectionBar.visibility = VISIBLE
                widthSelectionBar.visibility = GONE
            } else if (it == ButtonBarMode.MODE_WIDTHSELECT) {
                primaryButtonBar.visibility = GONE
                colorSelectionBar.visibility = GONE
                widthSelectionBar.visibility = VISIBLE
            }
        })
    }

    fun onPageChanged() {
        viewModel.enableDisablePgButtons()
        viewModel.backBitmap = viewModel.currentNode.bmp
        /* above: either the RHS is null, in which case either we haven't
                         * loaded this page before or there was an error, or we have loaded the page before, in
                         * which case it's the base PDF.
                         */
        backView.initBackBitmapIfNull(mainView.width, mainView.height)
        viewModel.isSecondaryButtonBarShown.value = true
    }

    override fun onStop() {
        saveTreeAndFileAsync()
        super.onStop()
    }

    private fun myDebug() {
        Log.d(TAG, "Debug")
    }

    override fun onBackPressed() {
        if (viewModel.buttonBarMode.value != ButtonBarMode.MODE_DEFAULT) {
            viewModel.buttonBarMode.value = ButtonBarMode.MODE_DEFAULT
        } else if (viewModel.isEditing.value!!) {
            viewModel.isEditing.value = false
        } else {
            super.onBackPressed()
        }
    }

    fun invalidateTreeViews() {
        treeView.invalidate()
        miniTreeView.invalidate()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { // used when picking a new fileUri
        if (requestCode == REQUEST_CODE_OPENFILE && resultCode == Activity.RESULT_OK && data?.data != null) {
            saveTreeSync()
            super.onActivityResult(requestCode, resultCode, data)
            loadTreeSync()
            onPageChanged()
        }
    }

// Interface functions from child views

    override fun deleteNodeAndDescendants(node: BmpTree.TreeNode) {
        if (node == viewModel.tree.rootNode) {
            return
        }
        alert("Delete this node and all its descendants? This cannot be undone.") {
            positiveButton("Proceed") {
                if (viewModel.tree.getLineage(viewModel.currentNode).contains(node)) { // would the current node be deleted?
                    changeToNode(node.parent!!) // if so, move it back to the parent of the node to be deleted
                    viewModel.arrangeBmps()
                    viewModel.isEditing.value = false
                }
                viewModel.tree.getDescendantsIncludingSelf(node).forEach {
                    viewModel.tree.deleteNode(it)
                    invalidateTreeViews()
                }
                if (viewModel.tree.nodes.size == 1) viewModel.isDeleteButtonEnabled.value = false
            }
            negativeButton("Back") {}
        }.show()
    }

    override fun changeToNode(node: BmpTree.TreeNode) {
        viewModel.currentNode = node
        viewModel.arrangeBmps()
        mainView.invalidate()
        invalidateTreeViews()

        if (!node.isActive) viewModel.isEditing.value = false
        if (node.isActive) viewModel.drawColor.value = node.color
        viewModel.enableDisableUndoRedoButtons()
        viewModel.isCommitButtonEnabled.value = viewModel.currentNode.isActive
    }

    override fun addNewNodeAt(node: BmpTree.TreeNode) {
        viewModel.currentNode = viewModel.tree.addNewNodeAt(node, mainView.paint.color)
        invalidateTreeViews()
        viewModel.enableDisableUndoRedoButtons()
        viewModel.isDeleteButtonEnabled.value = true
        viewModel.isCommitButtonEnabled.value = true
    }

    override fun showHideTreeView() {
        viewModel.isTreeShown.value = !viewModel.isTreeShown.value!!
    }

    override fun setPDFRenderer() {
        FileData.setRenderer(contentResolver)
    }

    override fun invalidateBackView() {
        backView.invalidate()
    }

// Filesystem-related functions

    fun getPageDir(fileData: FileData): File {
        val pdfDir = File(filesDir, "${fileData.fileHash}/")
        if (!pdfDir.exists()) pdfDir.mkdir()
        val pageDir = File(pdfDir, "${fileData.page}/")
        if (!pageDir.exists()) pageDir.mkdir()
        return pageDir
    }

    fun saveTreeSync() {
        try {
            writeTree()
        } catch (error: Exception) {
            Log.e(TAG, "Caught exception while writing tree", error)
        }
    }

    fun saveTreeAndFileAsync() {
        try {
            //doAsync {
            writeTree()
            FileDataManager.saveFileData(filesDir)
            //}
        } catch (error: Exception) {
            Log.e(TAG, "Caught exception while writing tree or fileUri data", error)
        }
    }

    fun loadTreeAsync() {
        doAsync {
            try {
                val tree = readTree()
                uiThread {
                    viewModel.tree = tree
                    viewModel.currentNode = viewModel.tree.rootNode
                }
            } catch (error: Exception) {
                Log.e(TAG, "Caught exception when loading data", error)
                viewModel.reset()
            }
            backView.initBackBitmapIfNull(mainView.width, mainView.height)
            backView.invalidate()
            mainView.invalidate()
            invalidateTreeViews()
        }
    }

    fun loadTreeSync() {
        try {
            val tree = readTree()
            viewModel.tree = tree
            viewModel.currentNode = viewModel.tree.rootNode
        } catch (error: Exception) {
            Log.e(TAG, "Caught exception when loading data", error)
            viewModel.reset()
        }
        backView.initBackBitmapIfNull(mainView.width, mainView.height)
        backView.invalidate()
        mainView.invalidate()
        invalidateTreeViews()
    }

    @Throws(Throwable::class)
    private fun writeTree() {
        val pageDir = getPageDir(FileData)
        viewModel.tree.nodes.filter { it.altered }.forEach {
            //only save nodes where editing has taken place
            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(File(pageDir, "${it.coords.first}_${it.coords.second}"))
                it.bmp?.compress(Bitmap.CompressFormat.WEBP, 20, fos)
                Log.d(TAG, "Wrote bitmap (${it.coords.first}, ${it.coords.second})")
            } catch (error: Throwable) {
                Log.e(TAG, "Error writing bitmap fileUri", error)
            } finally {
                fos?.close()
            }
        }
        var dataFos: FileOutputStream? = null
        var dataOos: ObjectOutputStream? = null
        try {
            dataFos = FileOutputStream(File(pageDir, "data"))
            dataOos = ObjectOutputStream(dataFos)
            dataOos.writeObject(viewModel.tree)
            Log.d(TAG, "Wrote data fileUri")
        } catch (error: Throwable) {
            throw error
        } finally {
            dataFos?.close()
            dataOos?.close()
        }
        Log.d(TAG, "Files in data directory: ${
        pageDir.listFiles().map { it.name }
        }")
    }

    @Throws(Throwable::class)
    private fun readTree(): BmpTree {
        val regex = Regex(FILE_REGEX_STRING)
        var dataFis: FileInputStream? = null
        var dataOis: ObjectInputStream? = null
        var tree = BmpTree()
        val pageDir = getPageDir(FileData)
        try {
            dataFis = FileInputStream(File(pageDir, "data"))
            dataOis = ObjectInputStream(dataFis)
            tree = dataOis.readObject() as BmpTree
            tree.nodes.forEach {
                it.undoRedoStack = mutableListOf()
                it.stackPointer = 0
                it.isActive = false
            }
            Log.d(TAG, "Successfully loaded data fileUri. Nodes: ${tree.nodes.size}.")
        } catch (error: Throwable) {
            throw error
        } finally {
            dataFis?.close()
            dataOis?.close()
        }
        pageDir.listFiles().forEach {
            if (regex.matchEntire(it.name) != null) {
                val (coord1, coord2) = regex.matchEntire(it.name)!!.destructured
                Log.d(TAG, "File found: $coord1, $coord2")
                val bitmap = BitmapFactory.decodeFile(it.path, BitmapFactory.Options().apply { inMutable = true })
                if (tree.nodeAtCoords(Pair(parseInt(coord1), parseInt(coord2))) != null) Log.d(TAG, "Yes: $coord1, $coord2")
                tree.nodeAtCoords(Pair(parseInt(coord1), parseInt(coord2)))?.bmp = bitmap
            }
        }
        tree.nodes.forEach { it.altered = false } // we just loaded, so nothing has been altered since the last save
        return tree
    }
}