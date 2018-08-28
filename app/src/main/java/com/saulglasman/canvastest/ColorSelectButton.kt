package com.saulglasman.canvastest

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.widget.ImageView
import org.jetbrains.anko.dip

class ColorSelectButton(context: Context, val buttonColor: Int) : ImageView(context) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(dip(36), dip(36))
    }

    override fun onDraw(canvas: Canvas?) {
        val paint = Paint()
        with(paint) {
            color = buttonColor
            style = Paint.Style.FILL
        }
        canvas?.drawRect(0f, 0f, dip(36).toFloat(), dip(36).toFloat(), paint)
        with(paint) {
            this.color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 6f
        }
        canvas?.drawRect(0f, 0f, dip(36).toFloat(), dip(36).toFloat(), paint)
        super.onDraw(canvas)
    }
}