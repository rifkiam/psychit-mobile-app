package com.example.psychika.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.example.psychika.R

class GoogleButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
): AppCompatButton(context, attrs) {
    private var iconButton: Drawable
    private var txtColor: Int = 0
    private var backgroundGoogleButton: Drawable

    init {
        iconButton = ContextCompat.getDrawable(context, R.drawable.ic_google) as Drawable
        txtColor = ContextCompat.getColor(context, R.color.neutral_400)
        backgroundGoogleButton = ContextCompat.getDrawable(context, R.drawable.custom_google_button) as Drawable

        setButtonDrawables(iconButton, null, null, null)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        background = backgroundGoogleButton
        setTextColor(txtColor)
        isAllCaps = false
    }

    private fun setButtonDrawables(left: Drawable? = null, top:Drawable? = null, end:Drawable? = null, bottom: Drawable? = null){
        setCompoundDrawablesWithIntrinsicBounds(left, top, end, bottom)
    }
}