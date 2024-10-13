package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonSearch = findViewById<Button>(R.id.search_button)
        val buttonSettings = findViewById<Button>(R.id.settings_button)
        val mediaButton = findViewById<Button>(R.id.media_button)

        val settingsActivity = Intent(this, SettingsActivity::class.java)
        val searchActivity = Intent(this, SearchActivity::class.java)
        val mediaActivity = Intent(this, MediaActivity::class.java)


        buttonSearch.setOnClickListener {
            startActivity(searchActivity)
        }

        buttonSettings.setOnClickListener{
            startActivity(settingsActivity)
        }

        mediaButton.setOnClickListener {
            startActivity(mediaActivity)
        }
    }
}