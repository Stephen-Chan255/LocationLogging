package com.example.locationlogging

import android.app.AlertDialog
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

        fab.setOnClickListener { showDialog() }
    }

    private fun showDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("AlertDialog")
        builder.setMessage("Are you sure to logout?")

        // add the buttons
        builder.setPositiveButton("Logout") { _, _ ->
            startActivity(Intent(this, LoginActivity::class.java))
        }

        builder.setNegativeButton("Cancel", null)

        // create and show the alert dialog
        val dialog = builder.create()
        dialog.show()
    }
}
