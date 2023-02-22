package com.romandevyatov.bestfinance.ui.adapters.menu.income

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.annotation.Nullable
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs


class CustomRecyclerView(context: Context, @Nullable attrs: AttributeSet?) :
    RecyclerView(context, attrs) {

    private val mTouchSlop: Int
    private var mPrevX = 0f

    init {
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
    }

    @SuppressLint("Recycle")
    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        when (e.action) {
            MotionEvent.ACTION_DOWN -> mPrevX = MotionEvent.obtain(e).x
            MotionEvent.ACTION_MOVE -> {
                val eventX = e.x
                val xDiff = abs(eventX - mPrevX)
                if (xDiff > mTouchSlop) {
                    return false
                }
            }
            else -> {
                // In general, we don't want to intercept touch events. They should be
                // handled by the child view.
                false
            }
        }
        return super.onInterceptTouchEvent(e)
    }

//    @SuppressLint("ClickableViewAccessibility")
//    override fun onTouchEvent(event: MotionEvent): Boolean {
//        // Here we actually handle the touch event (e.g. if the action is ACTION_MOVE,
//        // scroll this container).
//        // This method will only be called if the touch event was intercepted in
//        // onInterceptTouchEvent
//        return true
//    }
}
