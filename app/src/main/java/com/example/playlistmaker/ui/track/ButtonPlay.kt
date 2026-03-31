package com.example.playlistmaker.ui.track

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.graphics.drawable.toBitmap
import com.example.playlistmaker.R
import kotlin.math.min

internal class ButtonPlay @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {
    private var state: Boolean = true
    private var isClicked: Boolean = false

    private var imageBitmap: Bitmap? = null

    private var imageRect: RectF = RectF()

    private val viewSize = resources.getDimensionPixelSize(
        R.dimen.media_round_button_big_button_size
    )

    private val gestureDetector = GestureDetector(
        context,
        object : GestureDetector.SimpleOnGestureListener() {
            override fun onContextClick(e: MotionEvent): Boolean {
                return super.onContextClick(e)
            }
        }
    )

    private var imageResIdPlay: Bitmap? = null
    private var imageResIdPause: Bitmap? = null

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.ButtonPlay,
            defStyleAttr,
            defStyleRes
        ).apply {
            try {
                imageResIdPlay = getDrawable(R.styleable.ButtonPlay_imageResIdPlay)?.toBitmap()
                imageResIdPause = getDrawable(R.styleable.ButtonPlay_imageResIdPause)?.toBitmap()
            } finally {
                recycle()
            }
        }
        setImage(state)
        isEnabled = false
    }

    override fun onDraw(canvas: Canvas) {
        imageBitmap?.let {
            canvas.drawBitmap(imageBitmap as Bitmap, null, imageRect, null)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        imageRect = RectF(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())
    }

    fun switchState() {
        state = !state
        setImage(state)
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isClicked = true
                return true
            }

            MotionEvent.ACTION_UP -> {
                if (isClicked) {
                    isClicked = false
                    performClick()
                    return true
                }
            }
        }
        return gestureDetector.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        if (isEnabled) {
            updateState()
            invalidate()
            return super.performClick()
        } else {
            return super.performClick()
        }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
    }

    private fun updateState() {
        state = !state
        setImage(state)
    }

    private fun setImage(state: Boolean) {
        imageBitmap = if (state) imageResIdPlay else imageResIdPause
        invalidate()
    }
}