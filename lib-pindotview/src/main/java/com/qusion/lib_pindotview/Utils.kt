package com.qusion.lib_pindotview

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes

fun Context.themeColor(@AttrRes attrRes: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute (attrRes, typedValue, true)
    return typedValue.data
}