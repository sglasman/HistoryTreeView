package com.saulglasman.canvastest

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
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
class MainActivity : AppCompatActivity() {

    lateinit var viewModel: HistoryTreeViewModel
    lateinit var zoomView: HistoryTreeView
    lateinit var editButton: Button
    lateinit var commitButton: Button
    lateinit var branchButton: Button
    lateinit var showHideTreeButton: Button
    lateinit var undoButton: Button
    lateinit var redoButton: Button
    lateinit var undoRedoButtonBar: LinearLayout
    lateinit var treeView: TreeView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 1)
        }
        viewModel = ViewModelProviders.of(this).get(HistoryTreeViewModel::class.java)
        relativeLayout {
            zoomView = historyTreeView(viewModel) {
                id = ID_MAINVIEW
            }.lparams {
                alignParentTop()
                width = matchParent
                above(ID_TREEVIEW)
            }
            linearLayout {
                id = ID_BUTTONBAR
                editButton = button {
                    onClick {
                        viewModel.isEditing.value = !(viewModel.isEditing.value!!)
                    }
                }
                commitButton = button("Commit") {
                    onClick {
                        viewModel.currentNode.bmp = overlayBmpList(viewModel.undoRedoStack.take(viewModel.stackPointer)) //necessary in case there have been some undos
                        if (viewModel.currentNode.bmp == null) {
                            make(zoomView, "Nothing to commit", LENGTH_SHORT).show()
                        } else {
                            viewModel.arrangeBmps()
                            viewModel.isCanvasFresh.value = true
                            viewModel.isCommitted.value = true
                            viewModel.isEditing.value = false
                            zoomView.reinitFrontBitmap()
                        }
                    }
                }
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
                    onClick { viewModel.isTreeShown.value = !viewModel.isTreeShown.value!! }
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
                        if (viewModel.stackPointer > 0) {
                            viewModel.stackPointer--
                            zoomView.invalidate()
                        }
                    }
                }
                redoButton = button("Redo") {
                    onClick {
                        if (viewModel.stackPointer < viewModel.undoRedoStack.size) {
                            viewModel.stackPointer++
                            zoomView.invalidate()
                        }
                    }
                }
            }.lparams {
                width = matchParent
                height = wrapContent
                above(ID_BUTTONBAR)
            }
            treeView = treeView(viewModel) {
                id = ID_TREEVIEW
                visibility = GONE
            }.lparams {
                above(ID_UNDOREDOBAR)
                width = matchParent
                height = dip(100)
            }
        }
        viewModel.isEditing.observe(this, Observer {
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
        viewModel.isTreeShown.observe(this, Observer {
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
        viewModel.isCanvasFresh.observe(this, Observer {
            if (it) {
                if (zoomView.height > 0) { // make sure the view has actually been inflated before trying to reset the bitmap
                    zoomView.reinitFrontBitmap()
                }
                zoomView.invalidate()
            }
            treeView.invalidate()
        })
    }
}

