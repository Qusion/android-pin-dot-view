package com.qusion.pintextfield

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.qusion.pindotview.R

import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()

        numberDialView.setOnNumberClickListener{ number ->
            output.text = output.text.toString() + number
        }

        //Text size and color
        sizeColor.setOnClickListener {
            numberDialView.apply {
                val rand = Random.nextInt(20, 30)
                textSize = rand
                textColor = context.getColor(if(rand >= 25) R.color.test_color_1 else  R.color.test_color_2)
            }
        }

        //Delimiter size and color
        delimiterSizeColor.setOnClickListener {
            numberDialView.apply {
                val rand = Random.nextInt(4, 16)
                horizontalDelimiterWidth = rand
                verticalDelimiterWidth = rand - 3
                delimiterColor = context.getColor(if(rand >= 10) R.color.test_color_1 else  R.color.test_color_2)
            }
        }

        //Text style
        textStyle.setOnClickListener {
            numberDialView.apply {
                val rand = Random.nextInt(0, 3)
                textStyle = rand
            }
        }
    }

}
