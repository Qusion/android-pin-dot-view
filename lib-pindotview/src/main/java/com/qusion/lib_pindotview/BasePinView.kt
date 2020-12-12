package com.qusion.lib_pindotview

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.number_dial_view.view.*

typealias OnBiometricsButtonClickedListener = () -> Unit
typealias OnForgotButtonCLickedListener = () -> Unit

open class BasePinView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    var mForgotButtonText: String? = null
    var mBiometricsButtonSrc: Drawable? = null
    var mBackButtonSrc: Drawable? = null

    var mHasGrids = true
    var mHasForget = true

    val numberDialView: View
    val numbers: List<TextView>
    val horizontalDelimiters: List<View>
    val verticalDelimiters: List<View>

    var backVisible = false

    var mOnForgotButtonClickedListener: OnForgotButtonCLickedListener? = null
    var mOnBiometricsButtonClickedListener: OnBiometricsButtonClickedListener? = null

    init {
        numberDialView = View.inflate(context, R.layout.number_dial_view, this) as BasePinView

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

        horizontalDelimiters = listOf(
            numberDialView.horizontal_one,
            numberDialView.horizontal_two,
            numberDialView.horizontal_three
        )

        verticalDelimiters = listOf(
            numberDialView.vertical_one,
            numberDialView.vertical_two
        )
    }

    fun applyDialStyles() {
        numberDialView.bottomLeftText.text =
            mForgotButtonText ?: context.getText(R.string.bottom_left_button_string)

        numberDialView.bottomRightBiometricsIcon.setImageDrawable(
            mBiometricsButtonSrc ?: context.getDrawable(R.drawable.ic_biometrics)
        )

        numberDialView.bottomRightBackIcon.setImageDrawable(
            mBackButtonSrc ?: context.getDrawable(R.drawable.ic_back)
        )
    }


    fun toggleBackButton(visible: Boolean) {
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

    fun setOnForgotButtonClickedListener(l: OnForgotButtonCLickedListener) {
        mOnForgotButtonClickedListener = l
    }

    fun setOnBiometricsButtonClickedListener(l: OnBiometricsButtonClickedListener) {
        mOnBiometricsButtonClickedListener = l
    }
}