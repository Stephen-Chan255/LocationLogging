package com.example.locationlogging

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btn_login.setOnClickListener {
            startActivity(Intent(this, TabbedActivity::class.java))
        }

        text_login_anonymous.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }
    }
}
