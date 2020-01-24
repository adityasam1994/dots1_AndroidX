package com.cyfoes.aditya.dots1;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class CustomScrollView extends ScrollView {

    /* renamed from: a */
    private boolean f4528a = true;

    public CustomScrollView(Context context) {
        super(context);
    }

    public CustomScrollView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public CustomScrollView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public CustomScrollView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    /* renamed from: a */
    public boolean mo7257a() {
        return this.f4528a;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (mo7257a()) {
            return super.onInterceptTouchEvent(motionEvent);
        }
        return false;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (mo7257a()) {
            return super.onTouchEvent(motionEvent);
        }
        return false;
    }

    public void setEnableScrolling(boolean z) {
        this.f4528a = z;
    }
}
