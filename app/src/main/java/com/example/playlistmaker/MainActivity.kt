package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonSearch = findViewById<Button>(R.id.search_button)
        val buttonSettings = findViewById<Button>(R.id.settings_button)
        val mediaButton = findViewById<Button>(R.id.media_button)

        buttonSearch.setOnClickListener {
            val searchActivity = Intent(this, SearchActivity::class.java)
            startActivity(searchActivity)
        }

        buttonSettings.setOnClickListener{
            val settingsActivity = Intent(this, SettingsActivity::class.java)
            startActivity(settingsActivity)
        }

        mediaButton.setOnClickListener {
            val mediaActivity = Intent(this, MediaActivity::class.java)
            startActivity(mediaActivity)
        }
    }
}