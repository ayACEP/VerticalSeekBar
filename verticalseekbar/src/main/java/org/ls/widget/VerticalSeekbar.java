package org.ls.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;

/**
 * Created by ayACEP on 2016/12/27.
 * srx1982@gmail.com
 */

public class VerticalSeekbar extends View {

    private int max;
    private int min;
    private int progress;

    private Drawable thumb;
    private Drawable progressDrawable;

    public VerticalSeekbar(Context context) {
        super(context);
        ProgressBar p;
    }

    public VerticalSeekbar(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.VerticalSeekbarStyle);
    }

    public VerticalSeekbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.VerticalSeekbar, defStyleAttr, R.style.VerticalSeekbarStyle);
        thumb = ta.getDrawable(R.styleable.VerticalSeekbar_vs_thumb);
        progressDrawable = ta.getDrawable(R.styleable.VerticalSeekbar_vs_progressDrawable);
        Drawable bg = ((LayerDrawable) progressDrawable).findDrawableByLayerId(android.R.id.background);
        Drawable pro = ((LayerDrawable) progressDrawable).findDrawableByLayerId(android.R.id.progress);
        Drawable pro2 = ((LayerDrawable) progressDrawable).findDrawableByLayerId(android.R.id.secondaryProgress);
        pro.setLevel(1000);
        pro2.setLevel(5000);
        ta.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int dw = Math.max(thumb.getIntrinsicWidth(), progressDrawable.getIntrinsicWidth()) + getPaddingLeft() + getPaddingRight();
        int dh = MeasureSpec.getSize(heightMeasureSpec) + getPaddingTop() + getPaddingBottom();
        int progressHeight = dh - thumb.getIntrinsicHeight();
        int progressPadding = thumb.getIntrinsicHeight() / 2;
        thumb.setBounds(0, 0, thumb.getIntrinsicWidth(), thumb.getIntrinsicHeight());
        int off = Math.abs(thumb.getIntrinsicWidth() - progressDrawable.getIntrinsicWidth()) / 2;
        if (progressDrawable.getIntrinsicWidth() > thumb.getIntrinsicWidth()) {
            progressDrawable.setBounds(0, progressPadding, progressDrawable.getIntrinsicWidth(), progressHeight + progressPadding);
            thumb.getBounds().offset(off, 0);
        } else {
            progressDrawable.setBounds(off, progressPadding, progressDrawable.getIntrinsicWidth() + off, progressHeight + progressPadding);
        }
        setMeasuredDimension(dw, dh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        progressDrawable.draw(canvas);
        thumb.draw(canvas);
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
