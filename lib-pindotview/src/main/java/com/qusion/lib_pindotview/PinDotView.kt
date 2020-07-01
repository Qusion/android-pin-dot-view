package com.qusion.lib_pindotview

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.number_dial_view.view.*
import java.lang.Exception

typealias OnCompletedListener = (String)->Unit

class PinDotView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var mPinLength = 4
    private var mIdleDotSize = 16
    private var mIdleDotColor = 0
    private var mActiveDotColor = 0
    private var mActiveDotSizeModifier = 2f
    private var mNumberDialView: NumberDialView? = null

    private var mWidth = 0
    private var mHeight = 0

    private var mIdlePaint: Paint? = null
    private var mActivePaint: Paint? = null

    private var enteredNums = 0
    private var mEnteredPin = ""

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
            mIdleDotSize = a.getDimensionPixelSize(R.styleable.PinDotView_idleDotSize, 16)
            mIdleDotColor = a.getColor(R.styleable.PinDotView_idleDotColor, context.getColor(R.color.pin_dot_view_default_idle_color))
            mActiveDotColor = a.getColor(R.styleable.PinDotView_activeDotColor, context.getColor(R.color.pin_dot_view_default_active_color))
            mActiveDotSizeModifier = a.getFloat(R.styleable.PinDotView_activeDotSizeModifier, 2f)
        } finally {
            a.recycle()
        }

        mIdlePaint = Paint()
        mIdlePaint!!.isAntiAlias = true
        mIdlePaint!!.color = mIdleDotColor

        mActivePaint = Paint()
        mActivePaint!!.isAntiAlias = true
        mActivePaint!!.color = mActiveDotColor

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

        val startCenterX = mIdleDotSize.toFloat() * mActiveDotSizeModifier
        val endCenterX = width - mIdleDotSize.toFloat() * mActiveDotSizeModifier
        val step = (endCenterX - startCenterX)/(mPinLength - 1)

        for (i in 0 until mPinLength) {
            if(i < enteredNums) {
                canvas.drawCircle(i * step + startCenterX, height.toFloat() / 2, mIdleDotSize * mActiveDotSizeModifier, mActivePaint!!)
            }
            canvas.drawCircle(i * step + startCenterX, height.toFloat() / 2, mIdleDotSize.toFloat(), mIdlePaint!!)
        }
    }

    private fun handleNumberInput() {
        mNumberDialView!!.setOnNumberClickListener { digit ->
            enteredNums += 1
            mEnteredPin = "$mEnteredPin$digit"
            if (enteredNums == mPinLength) {
                mOnCompletedListener?.invoke(mEnteredPin)
            }
            invalidate()
        }
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