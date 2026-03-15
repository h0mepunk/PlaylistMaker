package com.example.playlistmaker.ui.track

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import com.example.playlistmaker.R
import kotlin.math.min

internal class ButtonPlay @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {

    val buttonImage: String
    private var state: Boolean = true
    private var isClicked: Boolean = false

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


    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.ButtonPlay,
            defStyleAttr,
            defStyleRes
        ).apply {
            try {
                buttonImage = getString(R.styleable.ButtonPlay_playButtonState) ?: ""
                setImage(state)
            } finally {
                recycle()
            }
        }

        isEnabled = false
    }

    fun switchState() {
        state = !state
        setImage(state)
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Расчёт ширины
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val contentWidth = when (widthMode) {
            // Если ограничений на ширину нет —
            // берём минимальное значение
            MeasureSpec.UNSPECIFIED -> viewSize

            // Если нужно указать точное значение ширины —
            // берём это значение
            MeasureSpec.EXACTLY -> viewSize

            // Если можно указать не более widthSize —
            // берём максимальную возможную ширину
            MeasureSpec.AT_MOST -> widthSize

            else -> error("Неизвестный режим ширины ($widthMode)")
        }

        // Расчёт высоты
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val contentHeight = when (heightMode) {
            // Если ограничений на высоту нет —
            // берём минимальное значение
            MeasureSpec.UNSPECIFIED -> viewSize

            // Если нужно указать точное значение высоты —
            // берём это значение
            MeasureSpec.EXACTLY -> heightSize

            // Если можно указать не более heightSize —
            // берём максимальную возможную высоту
            MeasureSpec.AT_MOST -> heightSize

            else -> error("Неизвестный режим высоты ($heightMode)")
        }

        // Берём минимальное значение — либо ширину, либо высоту,
        // чтобы сформировать квадрат.
        val size = min(contentWidth, contentHeight)

        // Устанавливаем посчитанные размеры
        setMeasuredDimension(size, size)
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
        setBackgroundResource(
            when (state) {
                true -> R.drawable.media_play
                false -> R.drawable.media_stop
            }
        )
    }

}