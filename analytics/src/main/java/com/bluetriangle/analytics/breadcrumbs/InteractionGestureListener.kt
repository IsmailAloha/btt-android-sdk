package com.bluetriangle.analytics.breadcrumbs

import android.app.Activity
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import com.bluetriangle.analytics.Tracker
import kotlin.math.roundToInt

class InteractionGestureListener(
    val activity: Activity,
) : SimpleOnGestureListener() {
    override fun onSingleTapUp(event: MotionEvent): Boolean {
        val x = event.x.roundToInt()
        val y = event.y.roundToInt()
        recordTouchEvent(TouchEventType.TAP, activity, x, y)
        Tracker.instance?.configuration?.logger?.debug("User Interaction -> onSingleTap($event)")
        return super.onSingleTapUp(event)
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        val x = e.x.roundToInt()
        val y = e.y.roundToInt()
        recordTouchEvent(TouchEventType.DOUBLE_TAP, activity, x, y)
        Tracker.instance?.configuration?.logger?.debug("User Interaction -> onDoubleTap($e)")
        return super.onDoubleTap(e)
    }
}