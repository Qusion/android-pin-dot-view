package com.qusion.pintextfield

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.qusion.pindotview.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()

        pinDotView.setOnCompletedListener { pin ->
            GlobalScope.launch {
                delay(1000)
                withContext(Dispatchers.Main) {
                    pinDotView.showErrorAnimation(clearPin = true)
                }
            }
            //Snackbar.make(pinDotView, pin, Snackbar.LENGTH_SHORT).show()
        }

        pinDotView.setOnBiometricsButtonClickedListener {
            Snackbar.make(pinDotView, "BIOMETRICS", Snackbar.LENGTH_SHORT).show()
        }

        pinDotView.setOnForgotButtonClickedListener {
            Snackbar.make(pinDotView, "FORGOT", Snackbar.LENGTH_SHORT).show()
        }
    }
}
