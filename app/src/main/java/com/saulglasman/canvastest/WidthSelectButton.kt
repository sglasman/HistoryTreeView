package com.saulglasman.canvastest

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.widget.ImageView
import org.jetbrains.anko.dip

class WidthSelectButton(context: Context, val lineWidth: Float, val viewModel: HistoryTreeViewModel): ImageView(context) {
    val paint = Paint()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(dip(COLOR_SELECT_BUTTON_SIZE), dip(COLOR_SELECT_BUTTON_SIZE))
    }

    override fun onDraw(canvas: Canvas?) {
        with(paint) {
            color = viewModel.drawColor.value!!
            strokeWidth = dip(lineWidth).toFloat()
            strokeCap = Paint.Cap.SQUARE
        }
        canvas?.drawLine(dip(SELECT_RECT_MARGIN).toFloat(), dip(COLOR_SELECT_BUTTON_SIZE).toFloat()/2,
                dip(COLOR_SELECT_BUTTON_SIZE - SELECT_RECT_MARGIN).toFloat(), dip(COLOR_SELECT_BUTTON_SIZE).toFloat()/2, paint)
        if (lineWidth == viewModel.drawWidth.value) {
            with(paint) {
                color = Color.BLACK
                strokeWidth = dip(1f).toFloat()
                style = Paint.Style.STROKE
            }
            canvas?.drawRect(0f, 0f,
                    dip(COLOR_SELECT_BUTTON_SIZE).toFloat(), dip(COLOR_SELECT_BUTTON_SIZE).toFloat(), paint)
        }
    }
}