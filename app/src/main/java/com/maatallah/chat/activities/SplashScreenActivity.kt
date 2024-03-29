package com.maatallah.chat.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.maatallah.chat.R
import kotlinx.coroutines.delay

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler(Looper.getMainLooper()).postDelayed({

            val  auth= Firebase.auth
            val currentUser = auth.currentUser
            if (currentUser !=null){
                Intent(this,HomeActivity::class.java).also {
                    startActivity(it)
                }
            }else{
                Intent(this,AuthentificationActivity::class.java).also {
                    startActivity(it)
                }
            }


            finish()

        },3000)




    }
}