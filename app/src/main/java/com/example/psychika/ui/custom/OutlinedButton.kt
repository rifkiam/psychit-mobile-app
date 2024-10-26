package com.example.psychika.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.example.psychika.R

class OutlinedButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
): AppCompatButton(context, attrs) {
    private var txtColor: Int = 0
    private var backgroundGoogleButton: Drawable

    init {
        txtColor = ContextCompat.getColor(context, R.color.primary_700)
        backgroundGoogleButton = ContextCompat.getDrawable(context, R.drawable.outlined_button) as Drawable
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        background = backgroundGoogleButton
        setTextColor(txtColor)
        isAllCaps = false
    }
}