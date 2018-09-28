package com.saulglasman.canvastest

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.widget.ImageView
import org.jetbrains.anko.dip

class ColorSelectButton(context: Context, val buttonColor: Int, var isCurrentColor: Boolean) : ImageView(context) {
    val paint = Paint()
    val colorSelectButtonSize = 24
    val selectRectMargin = 3

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(dip(colorSelectButtonSize), dip(colorSelectButtonSize))
    }

    override fun onDraw(canvas: Canvas?) {
        with(paint) {
            color = buttonColor
            style = Paint.Style.FILL
        }
        canvas?.drawRect(dip(selectRectMargin).toFloat(), dip(selectRectMargin).toFloat(),
                dip(colorSelectButtonSize - selectRectMargin).toFloat(), dip(colorSelectButtonSize - selectRectMargin).toFloat(), paint)
        with(paint) {
            this.color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 6f
        }
        canvas?.drawRect(dip(selectRectMargin).toFloat(), dip(selectRectMargin).toFloat(),
                dip(colorSelectButtonSize - selectRectMargin).toFloat(), dip(colorSelectButtonSize - selectRectMargin).toFloat(), paint)
        if (isCurrentColor) canvas?.drawRect(0f, 0f,
                dip(colorSelectButtonSize).toFloat(), dip(colorSelectButtonSize).toFloat(), paint)
        super.onDraw(canvas)
    }

    fun redrawForSelection(drawColor: Int) {
        isCurrentColor = (buttonColor == drawColor)
        invalidate()
    }
}