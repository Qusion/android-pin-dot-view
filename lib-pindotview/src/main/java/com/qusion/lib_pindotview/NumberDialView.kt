package com.qusion.lib_pindotview

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.number_dial_view.view.*

typealias OnNumberClickListener = (Int) -> Unit
typealias OnNumberRemovedListener = () -> Unit
typealias OnRightButtonClickedListener = () -> Unit
typealias OnLeftButtonClickedListener = () -> Unit

class NumberDialView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var mTextSize = 12
    private var mTextColor = 0
    private var mTextStyle = 0
    private var mBackgroundColor = 0
    private var mDelimiterColor = 0
    private var mVerticalDelimiterWidth = 0
    private var mHorizontalDelimiterWidth = 0
    private var mBottomLeftButtonText: String? = null
    private var mBottomRightButtonSrc: Drawable? = null
    private var mBottomRightButtonTint = 0

    private var mHasGrids = true
    private var mHasForget = true

    private val numberDialView: View
    private val numbers: List<TextView>
    private val verticalDelimiters: List<View>
    private val horizontalDelimiters: List<View>

    private var numbersEntered = 0
    private var backVisible = false

    private var mOnNumberClickListener: OnNumberClickListener? = null
    private var mOnNumberRemovedListener: OnNumberRemovedListener? = null
    private var mOnLeftButtonClickedListener: OnRightButtonClickedListener? = null
    private var mOnRightButtonClickedListener: OnRightButtonClickedListener? = null

    init {
        val a: TypedArray = if (attrs != null) {
            context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.NumberDial,
                0, 0
            )
        } else {
            throw IllegalArgumentException("The attributes need to be passed")
        }
        try {
            mTextSize = a.getDimensionPixelSize(R.styleable.NumberDial_textSize, 12)
            mTextColor = a.getColor(
                R.styleable.NumberDial_textColor,
                context.getColorFromAttr(R.attr.colorOnSurface)
            )
            mTextStyle = a.getInteger(R.styleable.NumberDial_textStyle, 0)
            mBackgroundColor = a.getColor(
                R.styleable.NumberDial_backgroundColor,
                context.getColor(R.color.transparent_color)
            )
            mDelimiterColor = a.getColor(
                R.styleable.NumberDial_delimiterColor,
                context.getColor(R.color.number_dial_view_delimiter_color)
            )
            mVerticalDelimiterWidth =
                a.getDimensionPixelSize(R.styleable.NumberDial_verticalDelimiterWidth, 1)
            mHorizontalDelimiterWidth =
                a.getDimensionPixelSize(R.styleable.NumberDial_horizontalDelimiterWidth, 1)
            mBottomLeftButtonText = a.getString(R.styleable.NumberDial_bottomLeftButtonText)
            mBottomRightButtonSrc = a.getDrawable(R.styleable.NumberDial_bottomRightButtonSrc)
            mBottomRightButtonTint = a.getColor(
                R.styleable.NumberDial_bottomRightButtonTint,
                context.getColorFromAttr(R.attr.colorOnSurface)
            )
            mHasGrids = a.getBoolean(R.styleable.NumberDial_hasGrids, true)
            mHasForget = a.getBoolean(R.styleable.NumberDial_hasForget, true)

        } finally {
            a.recycle()
        }

        numberDialView = View.inflate(context, R.layout.number_dial_view, this) as NumberDialView

        numbers = listOf(
            numberDialView.zero,
            numberDialView.one,
            numberDialView.two,
            numberDialView.three,
            numberDialView.four,
            numberDialView.five,
            numberDialView.six,
            numberDialView.seven,
            numberDialView.eight,
            numberDialView.nine
        )

        verticalDelimiters = listOf(
            numberDialView.vertical_one,
            numberDialView.vertical_two
        )

        horizontalDelimiters = listOf(
            numberDialView.horizontal_one,
            numberDialView.horizontal_two,
            numberDialView.horizontal_three
        )

        updateView()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        invalidate()
    }

    private fun updateView() {
        numberDialView.container.setBackgroundColor(mBackgroundColor)

        numbers.forEach { number ->
            number.apply {
                setTextColor(mTextColor)
                setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize.toFloat())
                typeface = Typeface.defaultFromStyle(mTextStyle)
                setOnClickListener { view ->
                    numbersEntered += 1
                    toggleBackButton(true)
                    mOnNumberClickListener?.invoke(numbers.indexOf(view))
                }
            }
        }

        numberDialView.bottomLeftText.text =
            mBottomLeftButtonText ?: context.getText(R.string.bottom_left_button_string)

        numberDialView.bottomRightBiometricsIcon.apply {
            setImageDrawable(mBottomRightButtonSrc ?: context.getDrawable(R.drawable.ic_biometrics))
            setColorFilter(mBottomRightButtonTint)
        }

        numberDialView.bottomRightBackIcon.apply {
            setColorFilter(mBottomRightButtonTint)
        }

        numberDialView.bottomRightButton.setOnClickListener {
            if (backVisible) {
                numbersEntered -= 1
                if (numbersEntered == 0) toggleBackButton(false)
                mOnNumberRemovedListener?.invoke()
            } else {
                mOnRightButtonClickedListener?.invoke()
            }
        }

        numberDialView.bottomLeftButton.setOnClickListener {
            mOnLeftButtonClickedListener?.invoke()
        }

        verticalDelimiters.forEach { delimiter ->
            delimiter.apply {
                val params = layoutParams
                params.width = mVerticalDelimiterWidth
                layoutParams = params
                setBackgroundColor(mDelimiterColor)
            }
        }

        horizontalDelimiters.forEach { delimiter ->
            delimiter.apply {
                val params = layoutParams
                params.height = mHorizontalDelimiterWidth
                layoutParams = params
                setBackgroundColor(mDelimiterColor)
            }
        }

        if (!mHasForget) {
            numberDialView.bottomLeftButton.visibility = View.GONE
            numberDialView.bottomLeftText.visibility = View.GONE
        }

        if (!mHasGrids) {
            horizontalDelimiters.forEach {
                it.visibility = View.INVISIBLE
            }
            verticalDelimiters.forEach {
                it.visibility = View.INVISIBLE
            }
        }

        invalidate()
    }

    private fun toggleBackButton(visible: Boolean) {
        if (visible) {
            backVisible = true
            bottomRightBiometricsIcon.visibility = View.GONE
            bottomRightBackIcon.visibility = View.VISIBLE
        } else {
            backVisible = false
            bottomRightBiometricsIcon.visibility = View.VISIBLE
            bottomRightBackIcon.visibility = View.GONE
        }
    }

    fun clear() {
        numbersEntered = 0
        toggleBackButton(false)
    }

    //region Setters
    fun setOnNumberClickListener(l: OnNumberClickListener) {
        mOnNumberClickListener = l
        updateView()
    }

    fun setOnNumberRemovedListener(l: OnNumberRemovedListener) {
        mOnNumberRemovedListener = l
        updateView()
    }

    fun setOnLeftButtonClickedListener(l: OnLeftButtonClickedListener) {
        mOnLeftButtonClickedListener = l
        updateView()
    }

    fun setOnRightButtonClickedListener(l: OnRightButtonClickedListener) {
        mOnRightButtonClickedListener = l
        updateView()
    }

    var textSize: Int
        get() = mTextSize
        set(textSize) {
            this.mTextSize = textSize
            updateView()
        }

    var textColor: Int
        get() = mTextColor
        set(textColor) {
            this.mTextColor = textColor
            updateView()
        }

    var textStyle: Int
        get() = mTextStyle
        set(textStyle) {
            this.mTextStyle = textStyle
            updateView()
        }

    override fun setBackgroundColor(mBackgroundColor: Int) {
        this.mBackgroundColor = mBackgroundColor
        updateView()
    }

    fun getBackgroundColor(): Int {
        return mBackgroundColor
    }

    var delimiterColor: Int
        get() = mDelimiterColor
        set(delimiterColor) {
            this.mDelimiterColor = delimiterColor
            updateView()
        }

    var verticalDelimiterWidth: Int
        get() = mVerticalDelimiterWidth
        set(verticalDelimiterWidth) {
            this.mVerticalDelimiterWidth = verticalDelimiterWidth
            updateView()
        }

    var horizontalDelimiterWidth: Int
        get() = mHorizontalDelimiterWidth
        set(horizontalDelimiterWidth) {
            this.mHorizontalDelimiterWidth = horizontalDelimiterWidth
            updateView()
        }

    var bottomLeftButtonText: String
        get() = mBottomLeftButtonText ?: ""
        set(bottomLeftButtonText) {
            this.mBottomLeftButtonText = bottomLeftButtonText
            updateView()
        }

    var bottomRightButtonSrc: Drawable?
        get() = mBottomRightButtonSrc
        set(bottomRightButtonSrc) {
            this.mBottomRightButtonSrc = bottomRightButtonSrc
            updateView()
        }

    var bottomRightButtonTint: Int
        get() = mBottomRightButtonTint
        set(bottomRightButtonTint) {
            this.mBottomRightButtonTint = bottomRightButtonTint
            updateView()
        }
    //endregion


    //region Utils
    @ColorInt
    private fun Context.getColorFromAttr(
        @AttrRes attrColor: Int,
        typedValue: TypedValue = TypedValue(),
        resolveRefs: Boolean = true
    ): Int {
        theme.resolveAttribute(attrColor, typedValue, resolveRefs)
        return typedValue.data
    }
    //endregion
}