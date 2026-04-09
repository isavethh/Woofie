package com.example.woofie

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val startButton: MaterialButton = findViewById(R.id.buttonStart)
        startButton.setOnClickListener {
            Toast.makeText(this, getString(R.string.welcome_cta_feedback), Toast.LENGTH_SHORT).show()
        }
    }
}

