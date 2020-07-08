package com.qusion.pintextfield

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.qusion.pindotview.R

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()

        pinDotView.numberDialView = numberDialView
        pinDotView.setOnCompletedListener { pin ->
            Snackbar.make(pinDotView, pin, Snackbar.LENGTH_SHORT).show()
            pinDotView.resetPin()
        }

        numberDialView.setOnBiometricsClickedListener {
            Snackbar.make(pinDotView, "BIOMETRICS", Snackbar.LENGTH_SHORT).show()
        }
    }
}
