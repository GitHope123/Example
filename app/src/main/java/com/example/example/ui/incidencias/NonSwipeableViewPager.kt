package com.example.example.ui.incidencias

import android.content.Context

import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class NonSwipeableViewPager @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ViewPager(context, attrs) {

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        // Desactiva el gesto de deslizamiento
        return false
    }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        // Desactiva la interceptación del gesto de deslizamiento
        return false
    }
}