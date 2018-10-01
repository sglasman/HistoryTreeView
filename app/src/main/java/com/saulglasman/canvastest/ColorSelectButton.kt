package com.saulglasman.canvastest

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.widget.ImageView
import org.jetbrains.anko.dip

class ColorSelectButton(context: Context, val buttonColor: Int, val viewModel: HistoryTreeViewModel) : ImageView(context) {
    val paint = Paint()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(dip(COLOR_SELECT_BUTTON_SIZE), dip(COLOR_SELECT_BUTTON_SIZE))
    }

    override fun onDraw(canvas: Canvas?) {
        with(paint) {
            color = buttonColor
            style = Paint.Style.FILL
        }
        canvas?.drawRect(dip(SELECT_RECT_MARGIN).toFloat(), dip(SELECT_RECT_MARGIN).toFloat(),
                dip(COLOR_SELECT_BUTTON_SIZE - SELECT_RECT_MARGIN).toFloat(),
                dip(COLOR_SELECT_BUTTON_SIZE - SELECT_RECT_MARGIN).toFloat(), paint)
        with(paint) {
            this.color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = dip(1f).toFloat()
        }
        canvas?.drawRect(dip(SELECT_RECT_MARGIN).toFloat(), dip(SELECT_RECT_MARGIN).toFloat(),
                dip(COLOR_SELECT_BUTTON_SIZE - SELECT_RECT_MARGIN).toFloat(),
                dip(COLOR_SELECT_BUTTON_SIZE - SELECT_RECT_MARGIN).toFloat(), paint)
        if (buttonColor == viewModel.drawColor.value) canvas?.drawRect(0f, 0f,
                dip(COLOR_SELECT_BUTTON_SIZE).toFloat(), dip(COLOR_SELECT_BUTTON_SIZE).toFloat(), paint)
        super.onDraw(canvas)
    }
}