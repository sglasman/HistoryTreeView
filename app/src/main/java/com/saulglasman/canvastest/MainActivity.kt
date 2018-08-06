package com.saulglasman.canvastest

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
import com.google.android.material.snackbar.Snackbar.make
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.sdk25.coroutines.onClick

const val ID_BUTTONBAR = 101
const val ID_MAINVIEW = 102
const val ID_TREEVIEW = 103

var PERMISSIONS_STORAGE: Array<String> = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

@Suppress("EXPERIMENTAL_FEATURE_WARNING", "NestedLambdaShadowedImplicitParameter")
class MainActivity : AppCompatActivity() {

    lateinit var viewModel: HistoryTreeViewModel
    lateinit var zoomView: HistoryTreeView
    lateinit var editButton: Button
    lateinit var commitButton: Button
    lateinit var branchButton: Button
    lateinit var showHideTreeButton: Button
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
                above(ID_BUTTONBAR)
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
                        if (viewModel.currentNode.bmp == null) {
                            make(zoomView, "Nothing to commit", LENGTH_SHORT).show()
                        } else {
                            viewModel.arrangeBmps()
                            viewModel.isCanvasFresh.value = true
                            viewModel.isCommitted.value = true
                            viewModel.isEditing.value = false
                            viewModel.frontBitmap = Bitmap.createBitmap(zoomView.width, zoomView.height, Bitmap.Config.ARGB_8888)
                            zoomView.pathCanvas = Canvas(viewModel.frontBitmap)
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
            treeView = treeView(viewModel) {
                id = ID_TREEVIEW
                visibility = GONE
            }.lparams {
                above(ID_BUTTONBAR)
                width = matchParent
                height = dip(100)
            }
        }
        viewModel.isEditing.observe(this, Observer {
            if (it) {
                editButton.text = "Stop editing"
                zoomView.mode = CanvasMode.MODE_DRAW
            } else {
                editButton.text = "Edit"
                zoomView.mode = CanvasMode.MODE_NAV
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

fun ViewManager.historyTreeView(viewModel: HistoryTreeViewModel, init: HistoryTreeView.() -> Unit = {}) = ankoView({ HistoryTreeView(it, viewModel) }, 0, init)
fun ViewManager.treeView(viewModel: HistoryTreeViewModel, init: TreeView.() -> Unit = {}) = ankoView({ TreeView(it, viewModel) }, 0, init)