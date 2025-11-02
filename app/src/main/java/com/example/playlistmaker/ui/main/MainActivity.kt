package com.example.playlistmaker.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.util.Creator
import com.example.playlistmaker.R

class MainActivity : AppCompatActivity() {
    private val mainController = Creator.provideMainController(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainController.onCreate()
    }
}