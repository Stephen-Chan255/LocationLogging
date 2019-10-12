package com.example.locationlogging

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_analysis.*

class AnalysisActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analysis)

        btn_map_in_analysis.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }
    }
}
