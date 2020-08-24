package com.training.foodrunner.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.training.foodrunner.R

class OrderConfirmation : AppCompatActivity() {

    lateinit var btnOkay: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_confirmation)

        btnOkay = findViewById(R.id.btnOkay)

        btnOkay.setOnClickListener {
            val homeIntent = Intent(this@OrderConfirmation, Home::class.java)
            startActivity(homeIntent)
            finish()
        }

    }

    override fun onBackPressed() {
        val homeIntent = Intent(this@OrderConfirmation, Home::class.java)
        startActivity(homeIntent)
        finish()
    }
}