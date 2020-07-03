package com.qusion.lib_pindotview

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation

typealias OnCompletedListener = (String) -> Unit

class PinDotView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var mPinLength = 4
    private var mIdleDotSize = 8
    private var mIdleDotColor = 0
    private var mCurrentDotGlareColor = 0
    private var mCurrentDotGlareSize = 16
    private var mPassedDotColor = 0

    private var mNumberDialView: NumberDialView? = null

    private var mWidth = 0
    private var mHeight = 0

    private var mIdlePaint: Paint? = null
    private var mCurrentPaint: Paint? = null
    private var mPassedPaint: Paint? = null

    private var enteredNums = 0
    private var mEnteredPin = ""

    var animatedAlpha = 100

    private var mOnCompletedListener: OnCompletedListener? = null

    init {
        val a: TypedArray = if (attrs != null) {
            context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.PinDotView,
                0, 0
            )
        } else {
            throw IllegalArgumentException("The attributes need to be passed")
        }
        try {
            mPinLength = a.getInteger(R.styleable.PinDotView_pinLength, 4)
            mIdleDotSize = a.getDimensionPixelSize(R.styleable.PinDotView_idleDotSize, 8)
            mIdleDotColor = a.getColor(
                R.styleable.PinDotView_idleDotColor,
                context.getColor(R.color.pin_dot_view_default_idle_color)
            )
            mCurrentDotGlareColor = a.getColor(
                R.styleable.PinDotView_currentDotGlareColor,
                context.getColor(R.color.pin_dot_view_default_current_color)
            )
            mCurrentDotGlareSize =
                a.getDimensionPixelSize(R.styleable.PinDotView_currentDotGlareSize, 16)
            mPassedDotColor = a.getColor(
                R.styleable.PinDotView_passedDotColor,
                context.getColor(R.color.pin_dot_view_default_passed_color)
            )
        } finally {
            a.recycle()
        }

        mIdlePaint = Paint().apply {
            isAntiAlias = true
            color = mIdleDotColor
        }

        mCurrentPaint = Paint().apply {
            isAntiAlias = true
            color = mCurrentDotGlareColor
        }

        mPassedPaint = Paint().apply {
            isAntiAlias = true
            color = mPassedDotColor
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val startCenterX = mCurrentDotGlareSize.toFloat()
        val endCenterX = width - mCurrentDotGlareSize.toFloat()
        val step = (endCenterX - startCenterX) / (mPinLength - 1)

        mCurrentPaint?.alpha = animatedAlpha
        for (i in 0 until mPinLength) {
            when {
                i == enteredNums -> {
                    canvas.drawCircle(
                        i * step + startCenterX,
                        height.toFloat() / 2,
                        mCurrentDotGlareSize.toFloat(),
                        mCurrentPaint!!
                    )
                    canvas.drawCircle(
                        i * step + startCenterX,
                        height.toFloat() / 2,
                        mIdleDotSize.toFloat(),
                        mIdlePaint!!
                    )
                }
                i > enteredNums -> {
                    canvas.drawCircle(
                        i * step + startCenterX,
                        height.toFloat() / 2,
                        mIdleDotSize.toFloat(),
                        mIdlePaint!!
                    )
                }
                else -> {
                    canvas.drawCircle(
                        i * step + startCenterX,
                        height.toFloat() / 2,
                        mIdleDotSize.toFloat(),
                        mPassedPaint!!
                    )
                }
            }
        }
    }

    private fun handleNumberInput() {
        mNumberDialView?.setOnNumberClickListener { digit ->
            enteredNums += 1
            mEnteredPin = "$mEnteredPin$digit"
            if (enteredNums == mPinLength) {
                mOnCompletedListener?.invoke(mEnteredPin)
            }

            animatedAlpha = 0
            val animation = PinDotViewAnimation(this)
            animation.duration = 500
            this.startAnimation(animation)

            invalidate()
        }
        mNumberDialView?.setOnNumberRemovedListener {
            enteredNums -= 1
            mEnteredPin = mEnteredPin.dropLast(1)
            invalidate()
        }
    }

    fun resetPin() {
        enteredNums = 0
        mEnteredPin = ""
        invalidate()
    }

    //region Setters
    fun setOnCompletedListener(l: OnCompletedListener) {
        mOnCompletedListener = l
    }

    var idleDotSize: Int
        get() = mIdleDotSize
        set(idleDotSize) {
            this.mIdleDotSize = idleDotSize
            invalidate()
        }

    var idleDotColor: Int
        get() = mIdleDotColor
        set(idleDotColor) {
            this.mIdleDotColor = idleDotColor
            invalidate()
        }

    var numberDialView: NumberDialView?
        get() = mNumberDialView
        set(numberDialView) {
            this.mNumberDialView = numberDialView
            handleNumberInput()
        }
    //endregion
}

class PinDotViewAnimation(private val pinDotView: PinDotView) : Animation() {
    override fun applyTransformation(
        interpolatedTime: Float,
        transformation: Transformation?
    ) {
        pinDotView.animatedAlpha = (interpolatedTime * 100).toInt()
        pinDotView.requestLayout()
    }
}