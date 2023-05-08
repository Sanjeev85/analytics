package com.example.analytics

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.analytics.databinding.ActivitySplashScreenBinding

class SplashScreen : AppCompatActivity() {
    lateinit var top: Animation
    lateinit var bottom: Animation
    lateinit var binding: ActivitySplashScreenBinding
    lateinit var slide_in: Animation
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        top = AnimationUtils.loadAnimation(this, R.anim.up)
        bottom = AnimationUtils.loadAnimation(this, R.anim.bottom)
        slide_in = AnimationUtils.loadAnimation(this, R.anim.slide_in)

        findViewById<TextView>(R.id.idd).animation = top
        findViewById<TextView>(R.id.tv1).animation = bottom
        findViewById<TextView>(R.id.tv2).animation = slide_in

        Handler().postDelayed(object : Runnable {
            override fun run() {
                startActivity(Intent(this@SplashScreen, Login::class.java))
                finish()
            }
        }, 5000)


    }
}