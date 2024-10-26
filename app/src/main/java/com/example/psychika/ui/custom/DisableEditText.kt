package com.example.psychika.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.InputType
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.example.psychika.R

class DisableEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {
    private var iconEditText: Drawable?

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.IconEditText,
            0, 0
        ).apply {
            iconEditText = getDrawable(R.styleable.IconEditText_icon)
        }

        isEnabled = false
        isFocusable = false
        isFocusableInTouchMode = false
        inputType = InputType.TYPE_NULL
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        iconEditText?.let {
            setEdiTextDrawables(startOfTheText = it)
        }
    }

    private fun setEdiTextDrawables(startOfTheText: Drawable? = null, topOfTheText:Drawable? = null, endOfTheText:Drawable? = null, bottomOfTheText: Drawable? = null){
        setCompoundDrawablesWithIntrinsicBounds(startOfTheText, topOfTheText, endOfTheText, bottomOfTheText)
        compoundDrawablePadding = 20
    }
}