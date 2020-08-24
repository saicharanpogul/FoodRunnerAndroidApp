package com.training.foodrunner.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.training.foodrunner.R

class MainActivity : AppCompatActivity() {
    lateinit var appLogo: ImageView
    lateinit var appName: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.exitTransition = null
        Handler().postDelayed({
            appLogo = findViewById(R.id.img_app_logo)
            appName = findViewById(R.id.txt_app_name)
            val p1: Pair<View, String> = Pair.create(appLogo, "app_logo")
            val p2: Pair<View, String> = Pair.create(appName, "app_name")
            val imageIntent = Intent(this@MainActivity, LoginActivity::class.java)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this@MainActivity, p1, p2)
            startActivity(imageIntent, options.toBundle())
        }, 1000)
    }

    override fun onRestart() {
        super.onRestart()
        val restartAct = Intent(this@MainActivity, LoginActivity::class.java)
        startActivity(restartAct)
    }

}