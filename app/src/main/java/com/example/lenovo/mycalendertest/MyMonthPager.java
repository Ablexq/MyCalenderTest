package com.example.lenovo.mycalendertest;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.ldf.calendar.view.MonthPager;

/**
 * Created by lenovo on 2017/9/7.
 */

public class MyMonthPager extends MonthPager {
    private int beforeX;
    private boolean isCanScroll = true;

    private int orientation = 0;
    // 0,左右可滑，
    // -1，禁止左滑，
    // 1，禁止右滑

    public MyMonthPager(Context context) {
        super(context);
    }

    public MyMonthPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isCanScroll) {
            return super.dispatchTouchEvent(ev);
        } else {

            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    beforeX = (int) ev.getX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int currentX = ((int) ev.getX());
                    int moveValue = currentX - beforeX;
                    Log.e("666", "moveValue===" + moveValue);

                    if ((moveValue > 0 && orientation == 1) || (moveValue < 0 && orientation == (-1))) {
                        return false;
                    }
                    break;
            }
            return super.dispatchTouchEvent(ev);
        }
    }

    public int getOrientation() {
        return orientation;
    }

    public void setScrollble(int orientation, boolean isCanScroll) {
        this.isCanScroll = isCanScroll;
        this.orientation = orientation;
    }
}