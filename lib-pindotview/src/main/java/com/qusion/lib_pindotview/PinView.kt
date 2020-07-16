package com.qusion.lib_pindotview

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import kotlinx.android.synthetic.main.number_dial_view.view.*


class PinView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BasePinView(context, attrs, defStyleAttr) {

    private var mPinLength = 4

    private var mIdlePaint: Paint
    private var mActivePaint: Paint

    private var mEnteredPin = ""
    private var mEnteredNums = 0
    private var mText = ""
    private var callbackSent = false

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
            mPinLength = a.getInteger(R.styleable.PinView_pin_length, 4)
            mForgotButtonText = a.getString(R.styleable.PinView_forgot_button_text)
            mBiometricsButtonSrc = a.getDrawable(R.styleable.PinView_biometrics_button_src)
            mBackButtonSrc = a.getDrawable(R.styleable.PinView_back_button_src)
        } finally {
            a.recycle()
        }

        numbers.forEach { number ->
            number.setOnClickListener { view ->
                digitAdded(numbers.indexOf(view))
            }
        }

        numberDialView.bottomRightButton.setOnClickListener {
            digitRemoved()
        }

        numberDialView.bottomLeftButton.setOnClickListener {
            mOnForgotButtonClickedListener?.invoke()
        }

        applyDialStyles()

        mIdlePaint = Paint().apply {
            isAntiAlias = true
            color = context.themeColor(R.attr.colorOnSurface)
            strokeWidth = LINE_STROKE_WIDTH
        }

        mActivePaint = Paint().apply {
            isAntiAlias = true
            color = context.themeColor(R.attr.colorPrimary)
            strokeWidth = LINE_STROKE_WIDTH
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        when {
            mEnteredPin.length <= mPinLength -> {
                mText = mEnteredPin.padEnd(mPinLength, '*')
            }
            mEnteredPin.length == mPinLength -> {
                if(!callbackSent) {
                    callbackSent = true
                    mOnCompletedListener?.invoke(mEnteredPin)
                }
            }
        }

        //View rendering
        val density = resources.displayMetrics.density
        val availableWidth = width - paddingRight - paddingLeft

        val mCharSize = CHAR_SIZE * density
        val mDigitSpacing = DIGIT_SPACING * density
        val mLineSpacing = LINE_SPACING * density

        var startX =
            (availableWidth / 2) - (mPinLength * mCharSize + (mPinLength - 1) * mDigitSpacing) / 2

        for (i in 0 until mPinLength) {
            if (mEnteredPin.length >= i) {
                canvas.drawLine(
                    startX,
                    mCharSize + mLineSpacing,
                    startX + mCharSize,
                    mCharSize + mLineSpacing,
                    mActivePaint
                )
            } else {
                canvas.drawLine(
                    startX,
                    mCharSize + mLineSpacing,
                    startX + mCharSize,
                    mCharSize + mLineSpacing,
                    mIdlePaint
                )
            }

            val middle = startX + (mCharSize / 4)
            canvas.drawText(
                mText,
                i,
                i + 1,
                middle,
                mCharSize,
                Paint().apply {
                    isAntiAlias = true
                    color = context.themeColor(R.attr.colorOnSurface)
                    textSize = CHAR_SIZE * density
                }
            )

            startX += (CHAR_SIZE + DIGIT_SPACING) * resources.displayMetrics.density
        }
    }

    private fun digitAdded(digit: Int) {
        toggleBackButton(true)
        mEnteredNums += 1
        mEnteredPin = "$mEnteredPin$digit"
        if (mEnteredNums == mPinLength) {
            mOnCompletedListener?.invoke(mEnteredPin)
        }
        invalidate()
    }

    private fun digitRemoved() {
        if (backVisible) {
            mEnteredNums -= 1
            invalidate()
            if (mEnteredNums == 0) {
                toggleBackButton(false)
            }
            mEnteredPin = mEnteredPin.dropLast(1)
            callbackSent = false
        } else {
            mOnBiometricsButtonClickedListener?.invoke()
        }
    }

    fun setOnCompletedListener(l: OnCompletedListener) {
        mOnCompletedListener = l
    }

    companion object {
        private const val LINE_STROKE_WIDTH = 4f
        private const val DIGIT_SPACING = 16f
        private const val CHAR_SIZE = 24f
        private const val LINE_SPACING = 12f
    }
}
