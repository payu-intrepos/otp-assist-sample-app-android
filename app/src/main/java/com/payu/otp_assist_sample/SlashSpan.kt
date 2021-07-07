package com.payu.otp_assist_sample

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt

import android.text.style.ReplacementSpan


class SlashSpan : ReplacementSpan() {
    override fun getSize(
        paint: Paint,
        text: CharSequence?,
        start: Int,
        end: Int,
        fm: FontMetricsInt?
    ): Int {
        val padding: Float = paint.measureText(" ", 0, 1) * 2
        val slash: Float = paint.measureText("/", 0, 1)
        val textSize: Float = paint.measureText(text, start, end)
        return (padding + slash + textSize).toInt()
    }


    override fun draw(
        canvas: Canvas, text: CharSequence, start: Int, end: Int, x: Float, top: Int, y: Int,
        bottom: Int, paint: Paint
    ) {
        canvas.drawText(text.subSequence(start, end).toString() + " / ", x, y.toFloat(), paint)
    }
}
