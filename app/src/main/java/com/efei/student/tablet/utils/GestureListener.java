package com.efei.student.tablet.utils;

import android.view.GestureDetector;
import android.view.MotionEvent;

public class GestureListener extends GestureDetector.SimpleOnGestureListener
{
    public static String gesture;
    public static float distanceX;
    public static float distanceY;

    // Override s all the callback methods of GestureDetector.SimpleOnGestureListener
    @Override
    public boolean onSingleTapUp(MotionEvent ev) {
        return true;
    }
    @Override
    public void onShowPress(MotionEvent ev) {
    }
    @Override
    public void onLongPress(MotionEvent ev) {
    }
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        gesture = "SCROLL";
        this.distanceX = distanceX;
        this.distanceY = distanceY;
        return true;
    }

    @Override
    public boolean onDown(MotionEvent ev) {
        gesture = "DOWN";
        return true;
    }
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return true;
    }
}