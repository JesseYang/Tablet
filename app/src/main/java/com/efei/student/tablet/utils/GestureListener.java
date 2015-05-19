package com.efei.student.tablet.utils;

import android.view.GestureDetector;
import android.view.MotionEvent;

public class GestureListener extends GestureDetector.SimpleOnGestureListener
{

    public static String currentGestureDetected;
    public static String gesture;
    public static float distanceX;
    public static float distanceY;

    // Override s all the callback methods of GestureDetector.SimpleOnGestureListener
    @Override
    public boolean onSingleTapUp(MotionEvent ev) {
        currentGestureDetected = "SINGLE TAB UP \n" + ev.toString();
        return true;
    }
    @Override
    public void onShowPress(MotionEvent ev) {
        currentGestureDetected = "SHOW PRESS \n" + ev.toString();
    }
    @Override
    public void onLongPress(MotionEvent ev) {
        currentGestureDetected = "LONG PRESS \n" + ev.toString();
    }
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        gesture = "SCROLL";
        this.distanceX = distanceX;
        this.distanceY = distanceY;
        currentGestureDetected = "SCROLL \n" + e1.toString() + "  " + e2.toString();
        return true;
    }
    @Override
    public boolean onDown(MotionEvent ev) {
        gesture = "DOWN";
        currentGestureDetected = "DOWN \n" + ev.toString();
        return true;
    }
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        currentGestureDetected = "FLING \n" + e1.toString() + "  " + e2.toString();
        return true;
    }
}