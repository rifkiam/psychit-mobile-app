package com.example.psychika.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.psychika.R

class PasswordEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs), View.OnTouchListener {
    private var iconPassword: Drawable
    private var hidePassword : Drawable
    private var showPassword: Drawable
    private var isPasswordVisible: Boolean = false

    init {
        iconPassword = ContextCompat.getDrawable(context, R.drawable.ic_pass) as Drawable
        hidePassword = ContextCompat.getDrawable(context, R.drawable.ic_hide_pass) as Drawable
        showPassword = ContextCompat.getDrawable(context, R.drawable.ic_unhide_pass) as Drawable

        setOnTouchListener(this)

        hidePassword()

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length < 8) {
                    setError(resources.getString(R.string.invalid_password), null)
                }
            }

            override fun afterTextChanged(s: Editable?) { }
        })

        maxLines = 1
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        setEditTextDrawables(startOfTheText = iconPassword,endOfTheText = if (isPasswordVisible) showPassword else hidePassword)
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            val drawableEnd = compoundDrawables[2]
            if (drawableEnd != null && event.rawX >= (right - drawableEnd.bounds.width() - paddingEnd)) {
                isPasswordVisible = !isPasswordVisible
                if (!isPasswordVisible) {
                    hidePassword()
                } else {
                    showPassword()
                }

                setSelection(text?.length ?: 0)
                return true
            }
        }
        return false
    }

    private fun setEditTextDrawables(startOfTheText: Drawable? = null, topOfTheText:Drawable? = null, endOfTheText:Drawable? = null, bottomOfTheText: Drawable? = null){
        setCompoundDrawablesWithIntrinsicBounds(startOfTheText, topOfTheText, endOfTheText, bottomOfTheText)
        compoundDrawablePadding = 20
    }

    private fun hidePassword() {
        val currentTypeface: Typeface? = typeface
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        typeface = currentTypeface
    }

    private fun showPassword() {
        val currentTypeface: Typeface? = typeface
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        typeface = currentTypeface
    }
}