package com.qusion.lib_pindotview

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.animation.Animation
import android.view.animation.Transformation
import kotlinx.android.synthetic.main.number_dial_view.view.*

typealias OnCompletedListener = (String) -> Unit

class PinDotView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BasePinView(context, attrs, defStyleAttr) {

    private var mPinLength = 4

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
            mPinLength = a.getInteger(R.styleable.PinDotView_pin_length, 4)
            mForgotButtonText = a.getString(R.styleable.PinDotView_forgot_button_text)
            mBiometricsButtonSrc = a.getDrawable(R.styleable.PinDotView_biometrics_button_src)
            mBackButtonSrc = a.getDrawable(R.styleable.PinDotView_back_button_src)
        } finally {
            a.recycle()
        }

        applyDialStyles()

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

        mIdlePaint = Paint().apply {
            isAntiAlias = true
            color = context.themeColor(R.attr.colorOnSurface)
        }

        mCurrentGlarePaint = Paint().apply {
            isAntiAlias = true
            color = context.themeColor(R.attr.colorPrimary)
            alpha = 30
        }

        mPassedGlarePaint = Paint().apply {
            isAntiAlias = true
            color = context.themeColor(R.attr.colorPrimary)
            alpha = 5
        }

        mPassedPaint = Paint().apply {
            isAntiAlias = true
            color = context.themeColor(R.attr.colorPrimary)
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)

        val density = resources.displayMetrics.density
        val dotSpacing = ((width - paddingStart - paddingEnd) / (2 * (mPinLength - 1))).toFloat()
        val startX = (width / 2) - (((mPinLength - 1) * dotSpacing)) / 2
        mCurrentGlarePaint?.alpha = animatedAlpha
        for (i in 0 until mPinLength) {
            when {
                i == enteredNums -> {
                    canvas.drawCircle(
                        startX + (i * dotSpacing),
                        GLARE_SIZE * density,
                        GLARE_SIZE * density,
                        mCurrentGlarePaint!!
                    )
                    canvas.drawCircle(
                        startX + (i * dotSpacing),
                        GLARE_SIZE * density,
                        IDLE_DOT_SIZE * density,
                        mIdlePaint!!
                    )
                }
                i > enteredNums -> {
                    canvas.drawCircle(
                        startX + (i * dotSpacing),
                        GLARE_SIZE * density,
                        IDLE_DOT_SIZE * density,
                        mIdlePaint!!
                    )
                }
                else -> {
                    canvas.drawCircle(
                        startX + (i * dotSpacing),
                        GLARE_SIZE * density,
                        GLARE_SIZE * density,
                        mPassedGlarePaint!!
                    )
                    canvas.drawCircle(
                        startX + (i * dotSpacing),
                        GLARE_SIZE * density,
                        IDLE_DOT_SIZE * density,
                        mPassedPaint!!
                    )
                }
            }
        }
    }

    private fun digitAdded(digit: Int) {
        toggleBackButton(true)
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

    private fun digitRemoved() {
        if (backVisible) {
            resetPin()
        } else {
            mOnBiometricsButtonClickedListener?.invoke()
        }
    }

    fun resetPin() {
        enteredNums = 0
        mEnteredPin = ""
        toggleBackButton(false)
        invalidate()
    }

    fun setOnCompletedListener(l: OnCompletedListener) {
        mOnCompletedListener = l
    }

    companion object {
        private const val IDLE_DOT_SIZE = 6f
        private const val GLARE_SIZE = 18f
    }
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