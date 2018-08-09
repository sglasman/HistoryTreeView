# HistoryTreeView
This is an activity supporting a custom View which can be drawn on and whose state can be tracked and modified using a version history tree.

<img src = "https://github.com/sglasman/HistoryTreeView/blob/master/screenshot0.png"/>

* To start a new branch, commit and then start editing
* Click a node to return to the state at that node, automatically committing the current node
* While actively editing, you can undo and redo individual strokes

To do:

* Different colors for different branches
* Any kind of persistence after exiting the app
* Node deletion
* Clean up the UI

Incorporates code inspired by https://github.com/Miserlou/Android-SDK-Samples/blob/master/ApiDemos/src/com/example/android/apis/graphics/FingerPaint.java.
