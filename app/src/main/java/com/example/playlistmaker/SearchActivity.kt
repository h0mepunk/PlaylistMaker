package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible

class SearchActivity : AppCompatActivity() {

    var textDump: CharSequence? = ""

    private val inputEditText: EditText by lazy { findViewById(R.id.inputEditText) }
    private val toolbar by lazy { findViewById<Toolbar>(R.id.search_toolbar)}

    val clearButton: ImageView by lazy { findViewById(R.id.clearIcon)}


    override fun onCreate(savedInstanceState: Bundle?) {

        val mainActivity = Intent(this, MainActivity::class.java)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        clearButton.setOnClickListener {
            inputEditText.setText(EMPTY_SEARCH_TEXT)
        }

        toolbar.setNavigationOnClickListener {
            startActivity(mainActivity)
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
                textDump = s
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)

        if (savedInstanceState != null) {
            textDump = savedInstanceState.getCharSequence(
                SEARCH_TEXT,
                EMPTY_SEARCH_TEXT as CharSequence
            )
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putCharSequence(SEARCH_TEXT, textDump)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        textDump = savedInstanceState.getCharSequence(
            SEARCH_TEXT,
            EMPTY_SEARCH_TEXT as CharSequence
        )
        inputEditText.setText(textDump)
    }

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val EMPTY_SEARCH_TEXT = ""
    }
}