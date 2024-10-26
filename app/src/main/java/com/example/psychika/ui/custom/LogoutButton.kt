package com.example.psychika.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.example.psychika.R

class LogoutButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatButton(context, attrs) {
    private var backgroundLogoutButton: Drawable
    private var txtColor: Int = 0

    init {
        backgroundLogoutButton = ContextCompat.getDrawable(context, R.drawable.custom_logout_button) as Drawable
        txtColor = ContextCompat.getColor(context, R.color.error_700)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        background = backgroundLogoutButton

        setTextColor(txtColor)

        isAllCaps = false
    }
}