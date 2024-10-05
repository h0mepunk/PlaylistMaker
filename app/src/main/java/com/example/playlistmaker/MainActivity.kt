package com.example.playlistmaker

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

        val searchButtonClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Нажали на кнопку поиска (не лямбда)!", Toast.LENGTH_SHORT).show()
            }
        }

        val settingsButtonClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Нажали на кнопку настроек (не лямбда)!", Toast.LENGTH_SHORT).show()
            }
        }

        val mediaButtonClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Нажали на кнопку медиа (не лямбда)!", Toast.LENGTH_SHORT).show()
            }
        }

        buttonSearch.setOnClickListener {
            //searchButtonClickListener
            Toast.makeText(this@MainActivity, "Нажали на кнопку поиска!", Toast.LENGTH_SHORT).show()
        }

        buttonSettings.setOnClickListener{
            //settingsButtonClickListener
            Toast.makeText(this@MainActivity, "Нажали на кнопку настроек!", Toast.LENGTH_SHORT).show()
        }

        mediaButton.setOnClickListener {
            //mediaButtonClickListener
            Toast.makeText(this@MainActivity, "Нажали на кнопку медиа!", Toast.LENGTH_SHORT).show()
        }
    }
}