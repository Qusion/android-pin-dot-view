package com.qusion.lib_pindotview

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.AppCompatEditText

class PinView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    AppCompatEditText(context, attrs) {

    private var mPinLength = 5
    private var mTextColor = 0
    private var mIdleColor = 0
    private var mActiveColor = 0
    private var mDigitSpacing = 16

    private var mCharSize = 0
    private var mLineSpacing = 12f //12dp by default, height of the text from our lines

    private var mClickListener: OnClickListener? = null

    private var mIdlePaint: Paint
    private var mActivePaint: Paint

    private var mLastText = ""
    private var mPin = ""
    private var callbackSent = false

    private var mNumberDialView: NumberDialView? = null

    private var mOnCompletedListener: OnCompletedListener? = null

    init {
        val a: TypedArray = if (attrs != null) {
            context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.PinView,
                0, 0
            )
        } else {
            throw IllegalArgumentException("The attributes need to be passed")
        }
        try {
            mPinLength = a.getInteger(R.styleable.PinView_pinLength, 5)
            mTextColor = a.getColor(
                R.styleable.PinView_textColor,
                context.getColor(R.color.pin_view_active_color)
            )
            mIdleColor = a.getColor(
                R.styleable.PinView_idleColor,
                context.getColor(R.color.pin_view_idle_color)
            )
            mActiveColor = a.getColor(
                R.styleable.PinView_activeColor,
                context.getColor(R.color.pin_view_active_color)
            )
            mDigitSpacing = a.getDimensionPixelSize(R.styleable.PinView_digitSpacing, -1)
        } finally {
            a.recycle()
        }

        mIdlePaint = Paint().apply {
            color = mIdleColor
            strokeWidth = LINE_STROKE_WIDTH
        }

        mActivePaint = Paint().apply {
            color = mActiveColor
            strokeWidth = LINE_STROKE_WIDTH
        }

        val multi = context.resources.displayMetrics.density
        setBackgroundResource(0)

        if (mDigitSpacing < 0) mDigitSpacing = (16f * multi).toInt()
        mLineSpacing *= multi //convert to pixels for our density

        paint.color = mTextColor

        //Disable copy paste
        super.setCustomSelectionActionModeCallback(object : ActionMode.Callback {
            override fun onActionItemClicked(p0: ActionMode?, p1: MenuItem?): Boolean = false

            override fun onCreateActionMode(p0: ActionMode?, p1: Menu?): Boolean = false

            override fun onPrepareActionMode(p0: ActionMode?, p1: Menu?): Boolean = false

            override fun onDestroyActionMode(p0: ActionMode?) {
            }
        })

        // When tapped, move cursor to end of text.
        super.setOnClickListener {
            setSelection(getEnteredPin().length)
            if (mClickListener != null) {
                mClickListener!!.onClick(it)
            }
        }
        isCursorVisible = false
        imeOptions = EditorInfo.IME_ACTION_DONE

        super.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setSelection(getEnteredPin().length)
            }
        })

        setText(text.toString().padEnd(mPinLength, '*'))
        mLastText = text.toString()
    }

    override fun setCustomSelectionActionModeCallback(actionModeCallback: ActionMode.Callback) {
        throw RuntimeException("setCustomSelectionActionModeCallback() not supported.")
    }

    override fun onDraw(canvas: Canvas) {
        //Text input handling
        val enteredPin = getEnteredPin()

        when {
            enteredPin.isNotEmpty() -> {
                when {
                    enteredPin.length > mPinLength -> {
                        setText(mLastText)
                    }
                    text.toString().length > mLastText.length -> {
                        setText(enteredPin.padEnd(mPinLength, '*'))
                    }
                    text.toString().length < mLastText.length -> {
                        setText(
                            enteredPin.substring(0, mLastText.replace("*", "").length - 1)
                                .padEnd(mPinLength, '*')
                        )
                        callbackSent = false
                    }
                }
            }
            enteredPin.isEmpty() -> {
                numberDialView?.clear()
                if (text.toString() != "*".repeat(mPinLength)) {
                    setText("*".repeat(mPinLength))
                }
            }
            text.toString().length != mLastText.length -> {
                setText("*".repeat(mPinLength))
            }
        }

        mPin = getEnteredPin()
        if (mPin.length == mPinLength && !callbackSent) {
            callbackSent = true
            mOnCompletedListener?.invoke(getEnteredPin())
        }
        mLastText = text.toString()

        //View rendering
        val availableWidth = width - paddingRight - paddingLeft
        mCharSize = (availableWidth - mDigitSpacing * (mPinLength - 1)) / mPinLength

        var startX = paddingLeft.toFloat()
        val bottom = height - paddingBottom.toFloat()

        val textLength = text.toString().length
        val textWidths = FloatArray(textLength)

        paint.getTextWidths(text, 0, textLength, textWidths)

        for (i in 0..mPinLength) {
            if (getEnteredPin().length >= i) {
                canvas.drawLine(startX, bottom, startX + mCharSize, bottom, mActivePaint)
            } else {
                canvas.drawLine(startX, bottom, startX + mCharSize, bottom, mIdlePaint)
            }

            if (textLength > i) {
                val middle = startX + mCharSize / 2
                canvas.drawText(
                    text as Editable,
                    i,
                    i + 1,
                    middle - textWidths[0] / 2,
                    bottom - mLineSpacing,
                    paint
                )
            }

            startX += mCharSize + mDigitSpacing
        }
    }

    private fun getEnteredPin(): String {
        return text.toString().replace("*", "")
    }

    private fun handleNumberInput() {
        mNumberDialView?.setOnNumberClickListener { digit ->
            setText("${text.toString()}$digit")
            invalidate()
        }
        mNumberDialView?.setOnNumberRemovedListener {
            setText(text.toString().dropLast(1))
            invalidate()
        }
    }

    //region Setters
    override fun setOnClickListener(listener: OnClickListener?) {
        mClickListener = listener
    }

    fun setOnCompletedListener(l: OnCompletedListener) {
        mOnCompletedListener = l
    }

    var numberDialView: NumberDialView?
        get() = mNumberDialView
        set(numberDialView) {
            this.mNumberDialView = numberDialView
            showSoftInputOnFocus = false
            handleNumberInput()
        }

    //endregion

    companion object {
        private const val LINE_STROKE_WIDTH = 4f
    }
}