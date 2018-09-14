# HistoryTreeView
This is an activity supporting a custom View which can be zoomed, scrolled and drawn on and whose state can be tracked and modified using a version history tree.

<img src = "https://github.com/sglasman/HistoryTreeView/blob/master/screenshot2.png"/>

* To start a new branch, commit and then start editing
* Click a node to return to the state at that node, automatically committing the current node
* While actively editing, you can undo and redo individual strokes
* To delete a branch, long press on the node
* You can select colors for individual branches, and these will appear as colors of edges of the history tree
* All data will automatically save and load when the app is started or exited
* You can select PDF files from local storage

Incorporates code inspired by https://github.com/Miserlou/Android-SDK-Samples/blob/master/ApiDemos/src/com/example/android/apis/graphics/FingerPaint.java.
