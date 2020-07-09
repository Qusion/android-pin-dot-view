package com.qusion.lib_pindotview

import android.content.Context
import android.content.res.TypedArray
import android.graphics.BlendMode
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
    private var mDotSpacing = -1
    private var mIdleDotSize = 8
    private var mIdleDotColor = 0
    private var mCurrentDotGlareColor = 0
    private var mCurrentDotGlareSize = 16

    private var mPassedDotColor = 0

    private var mNumberDialView: NumberDialView? = null

    private var mIdlePaint: Paint? = null
    private var mCurrentGlarePaint: Paint? = null
    private var mPassedGlarePaint: Paint? = null
    private var mPassedPaint: Paint? = null

    private var enteredNums = 0
    private var mEnteredPin = ""

    var animatedAlpha = 20

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
            mDotSpacing = a.getDimensionPixelSize(R.styleable.PinDotView_dotSpacing, -1)
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

        mCurrentGlarePaint = Paint().apply {
            isAntiAlias = true
            color = mCurrentDotGlareColor
            alpha = 30
        }

        mPassedGlarePaint = Paint().apply {
            isAntiAlias = true
            color = mCurrentDotGlareColor
            alpha = 5
        }

        mPassedPaint = Paint().apply {
            isAntiAlias = true
            color = mPassedDotColor
        }

        if (mDotSpacing < 0) mDotSpacing = mCurrentDotGlareSize * 3
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val endCenterX = 2 * mCurrentDotGlareSize.toFloat() + (mDotSpacing * (mPinLength - 1))
        setMeasuredDimension(endCenterX.toInt(), 2 * mCurrentDotGlareSize)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        mCurrentGlarePaint?.alpha = animatedAlpha
        for (i in 0 until mPinLength) {
            when {
                i == enteredNums -> {
                    canvas.drawCircle(
                        i * mDotSpacing + mCurrentDotGlareSize.toFloat(),
                        height.toFloat() / 2,
                        mCurrentDotGlareSize.toFloat(),
                        mCurrentGlarePaint!!
                    )
                    canvas.drawCircle(
                        i * mDotSpacing + mCurrentDotGlareSize.toFloat(),
                        height.toFloat() / 2,
                        mIdleDotSize.toFloat(),
                        mIdlePaint!!
                    )
                }
                i > enteredNums -> {
                    canvas.drawCircle(
                        i * mDotSpacing + mCurrentDotGlareSize.toFloat(),
                        height.toFloat() / 2,
                        mIdleDotSize.toFloat(),
                        mIdlePaint!!
                    )
                }
                else -> {
                    canvas.drawCircle(
                        i * mDotSpacing + mCurrentDotGlareSize.toFloat(),
                        height.toFloat() / 2,
                        mCurrentDotGlareSize.toFloat(),
                        mPassedGlarePaint!!
                    )
                    canvas.drawCircle(
                        i * mDotSpacing + mCurrentDotGlareSize.toFloat(),
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
            invalidate()
            if(enteredNums == 0) {
                mNumberDialView?.clear()
            }
            mEnteredPin = mEnteredPin.dropLast(1)
        }
    }

    fun resetPin() {
        enteredNums = 0
        mEnteredPin = ""
        mNumberDialView?.clear()
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
        pinDotView.animatedAlpha = (interpolatedTime * 30).toInt()
        pinDotView.requestLayout()
    }
}