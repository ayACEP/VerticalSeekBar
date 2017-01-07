package org.ls.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;

/**
 * Created by ayACEP on 2016/12/27.
 * srx1982@gmail.com
 */

public class VerticalSeekbar extends View {

    private static final int[] STATE_PRESSED = new int[]{android.R.attr.state_pressed};
    private static final int[] STATE_EMPTY = new int[]{android.R.attr.state_empty};

    private int max = 100;
    private int min = 1;
    private int progress = 1;
    private int secondaryProgress = 1;

    private Drawable thumb;
    private Drawable progressDrawable;
    private Drawable proDrawable;
    private Drawable secProDrawable;

    private RangeMapping pixel2progress = new RangeMapping();
    private RangeMapping pixel2level = new RangeMapping();

    public VerticalSeekbar(Context context) {
        super(context);
        ProgressBar p;
    }

    public VerticalSeekbar(Context context, AttributeSet attrs) throws Exception {
        this(context, attrs, R.attr.VerticalSeekbarStyle);
    }

    public VerticalSeekbar(Context context, AttributeSet attrs, int defStyleAttr) throws Exception {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.VerticalSeekbar, defStyleAttr, R.style.VerticalSeekbarStyle);
        thumb = ta.getDrawable(R.styleable.VerticalSeekbar_vs_thumb);
        progressDrawable = ta.getDrawable(R.styleable.VerticalSeekbar_vs_progressDrawable);
        progress = ta.getInteger(R.styleable.VerticalSeekbar_vs_progress, 1);
        secondaryProgress = ta.getInteger(R.styleable.VerticalSeekbar_vs_secondaryProgress, 1);
        max = ta.getInteger(R.styleable.VerticalSeekbar_vs_max, 100);
        min = ta.getInteger(R.styleable.VerticalSeekbar_vs_min, 1);
        if (max <= min) {
            throw new Exception("max must > min");
        }
        ta.recycle();

        proDrawable = ((LayerDrawable) progressDrawable).findDrawableByLayerId(android.R.id.progress);
        secProDrawable = ((LayerDrawable) progressDrawable).findDrawableByLayerId(android.R.id.secondaryProgress);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                onActionDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                onActionMove(event);
                break;
            case MotionEvent.ACTION_UP:
                onActionUp(event);
                break;
            default:
                return super.onTouchEvent(event);
        }
        return true;
    }

    private void onActionDown(MotionEvent event) {
        thumb.setState(STATE_PRESSED);
        onActionMove(event);
    }

    private void onActionMove(MotionEvent event) {
        float yPos = event.getY();
        moveThumbTo(yPos);
        invalidate();
    }

    private void onActionUp(MotionEvent event) {
        thumb.setState(STATE_EMPTY);
        invalidate();
    }

    private void moveThumbTo(float yPos) {
        if (yPos < progressDrawable.getBounds().top) {
            yPos = progressDrawable.getBounds().top;// move to top
        } else if (yPos > progressDrawable.getBounds().bottom) {
            yPos = progressDrawable.getBounds().bottom;
        }
        Rect rect = thumb.copyBounds();
        rect.offsetTo(thumb.getBounds().left, (int) (yPos - thumb.getBounds().height() / 2));
        thumb.setBounds(rect);

        float level = pixel2level.getValue2(yPos);
        proDrawable.setLevel((int) level);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 设置测量值
        int dw = Math.max(thumb.getIntrinsicWidth(), progressDrawable.getIntrinsicWidth()) + getPaddingLeft() + getPaddingRight();
        int dh = MeasureSpec.getSize(heightMeasureSpec) + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(dw, dh);
        // 计算需要的值
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
        pixel2progress.set(progressDrawable.getBounds().top, progressDrawable.getBounds().bottom, min, max);
        pixel2level.set(progressDrawable.getBounds().top, progressDrawable.getBounds().bottom, 0, 10000);// 0 to 10000 from View.setLevel

        float yPos = pixel2progress.getValue1(progress);
        moveThumbTo(yPos);

        float secYPos = pixel2progress.getValue1(secondaryProgress);
        int level = (int) pixel2level.getValue2(secYPos);
        secProDrawable.setLevel(level);
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

    private static class RangeMapping {

        private float min1;
        private float max1;
        private float min2;
        private float max2;

        public RangeMapping() {
        }

        public RangeMapping(float min1, float max1, float min2, float max2) {
            set(min1, max1, min2, max2);
        }

        public float getValue1(float value2) {
            return (value2 - min2) / (max2 - min2) * (max1 - min1) + min1;
        }

        public float getValue2(float value1) {
            return (value1 - min1) / (max1 - min1) * (max2 - min2) + min2;
        }

        public void set(float min1, float max1, float min2, float max2) {
            this.min1 = min1;
            this.max1 = max1;
            this.min2 = min2;
            this.max2 = max2;
        }

        public float getMin1() {
            return min1;
        }

        public void setMin1(float min1) {
            this.min1 = min1;
        }

        public float getMax1() {
            return max1;
        }

        public void setMax1(float max1) {
            this.max1 = max1;
        }

        public float getMin2() {
            return min2;
        }

        public void setMin2(float min2) {
            this.min2 = min2;
        }

        public float getMax2() {
            return max2;
        }

        public void setMax2(float max2) {
            this.max2 = max2;
        }
    }
}
