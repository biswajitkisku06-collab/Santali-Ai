package com.santalijarvis.ai

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor("#030712"))
        }
        
        val text = TextView(this).apply {
            text = "SANTALI JARVIS AI\nSystem Online"
            setTextColor(Color.parseColor("#00F0FF"))
            textSize = 24f
            gravity = Gravity.CENTER
        }
        
        layout.addView(text)
        setContentView(layout)
    }
}
