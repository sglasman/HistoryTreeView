package com.saulglasman.canvastest


class CanvasTransformer(var scaleFactor: Float = 1f, var translateX: Float = 0f, var translateY: Float = 0f) {
    fun transform(coord: HistoryTreeView.Coord): HistoryTreeView.Coord {
        val (x, y) = coord
        return HistoryTreeView.Coord(
                affine(scaleFactor, translateX, x),
                affine(scaleFactor, translateY, y)
        )
    }

    private fun affine(a: Float, b: Float, x: Float): Float = ((x / a) - b)
}
