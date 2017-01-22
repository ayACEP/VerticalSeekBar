package org.ls.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.DrawableUtils;
import android.support.v7.widget.TintTypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;

import java.lang.reflect.Field;

/**
 * Created by ayACEP on 2016/12/27.
 * srx1982@gmail.com
 */

public class VerticalSeekBar extends View {

    private static final int[] STATE_PRESSED = new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled};
    private static final int[] STATE_NORMAL = new int[]{android.R.attr.state_enabled};

    private static final int DIRECTION_BOTTOM_TO_TOP = 0;
    private static final int DIRECTION_TOP_TO_BOTTOM = 1;

    private static final int[] TEMP_ARRAY = new int[1];
    private static final PorterDuff.Mode DEFAULT_MODE = PorterDuff.Mode.SRC_IN;

    private int max = 100;
    private int min = 1;
    private int progress = 1;
    private int secondaryProgress = 1;
    private int direction = 0;

    private Drawable thumb;
    private Drawable progressDrawable;
    private Drawable bgDrawable;// progressDrawable's background
    private Drawable proDrawable;
    private Drawable secProDrawable;

    private RangeMapping pixel2progress = new RangeMapping();
    private RangeMapping pixel2level = new RangeMapping();

    private OnVerticalSeekBarChangeListener onVerticalSeekBarChangeListener;

    private boolean isTracking = false;

    public VerticalSeekBar(Context context) {
        super(context);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs) throws Exception {
        this(context, attrs, 0);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs, int defStyleAttr) throws Exception {
        super(context, attrs, defStyleAttr);
        SeekBar d;

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.VerticalSeekBar, defStyleAttr, R.style.VerticalSeekbarStyle);
        thumb = ta.getDrawable(R.styleable.VerticalSeekBar_vs_thumb);
        progressDrawable = ta.getDrawable(R.styleable.VerticalSeekBar_vs_progressDrawable);
        progress = ta.getInteger(R.styleable.VerticalSeekBar_vs_progress, 1);
        secondaryProgress = ta.getInteger(R.styleable.VerticalSeekBar_vs_secondaryProgress, 1);
        max = ta.getInteger(R.styleable.VerticalSeekBar_vs_max, 100);
        min = ta.getInteger(R.styleable.VerticalSeekBar_vs_min, 1);
        direction = ta.getInt(R.styleable.VerticalSeekBar_vs_direction, 0);// default 0 is bottom_to_top
        boolean isMaterial = ta.getBoolean(R.styleable.VerticalSeekBar_vs_material, true);
        if (max <= min) {
            throw new Exception("max must > min");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && getBackground() == null && isMaterial) {
            setBackground(getResources().getDrawable(R.drawable.item_background_borderless_material, context.getTheme()));
        }
        ta.recycle();

        bgDrawable = ((LayerDrawable) progressDrawable).findDrawableByLayerId(android.R.id.background);
        proDrawable = ((LayerDrawable) progressDrawable).findDrawableByLayerId(android.R.id.progress);
        secProDrawable = ((LayerDrawable) progressDrawable).findDrawableByLayerId(android.R.id.secondaryProgress);

        // If there is more than 2 VerticalSeekBar and they use different direction in a app, must use different progressDrawable(just copy and change name).
        // Because system will reuse drawable cause all drawable actually are the same one, you change one, another also will be changed.
        if (direction == DIRECTION_TOP_TO_BOTTOM) {
            setGravity(proDrawable.getConstantState(), Gravity.TOP);
            setGravity(secProDrawable.getConstantState(), Gravity.TOP);
        } else if (direction == DIRECTION_BOTTOM_TO_TOP) {
            setGravity(proDrawable.getConstantState(), Gravity.BOTTOM);
            setGravity(secProDrawable.getConstantState(), Gravity.BOTTOM);
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && isMaterial) {
            thumb = tintThumbDrawable(context, thumb);
            setPorterDuffColorFilter(bgDrawable, getThemeAttrColor(context, android.support.v7.appcompat.R.attr.colorControlNormal), DEFAULT_MODE);
            setPorterDuffColorFilter(secProDrawable, getThemeAttrColor(context, android.support.v7.appcompat.R.attr.colorControlNormal), DEFAULT_MODE);
            setPorterDuffColorFilter(proDrawable, getThemeAttrColor(context, android.support.v7.appcompat.R.attr.colorControlActivated), DEFAULT_MODE);
        }

        thumb.setState(STATE_NORMAL);
        setBackgroundState(STATE_NORMAL);
    }

    public int getThemeAttrColor(Context context, int attr) {
        TEMP_ARRAY[0] = attr;
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, null, TEMP_ARRAY);
        try {
            return a.getColor(0, 0);
        } finally {
            a.recycle();
        }
    }

    private void setPorterDuffColorFilter(Drawable d, int color, PorterDuff.Mode mode) {
        if (DrawableUtils.canSafelyMutateDrawable(d)) {
            d = d.mutate();
        }
        d.setColorFilter(AppCompatDrawableManager.getPorterDuffColorFilter(color, mode == null ? DEFAULT_MODE : mode));
    }

    private Drawable tintThumbDrawable(@NonNull Context context, @NonNull Drawable drawable) {
        ColorStateList tintList = AppCompatResources.getColorStateList(context, R.color.tint_seek_thumb);
        if (DrawableUtils.canSafelyMutateDrawable(drawable)) {
            drawable = drawable.mutate();
        }
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTintList(drawable, tintList);
        return drawable;
    }

    private void setRippleRect(Rect rect) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (getBackground() != null && getBackground() instanceof RippleDrawable) {
                getBackground().setHotspot(rect.centerX(), rect.centerY());
                getBackground().setHotspotBounds(rect.left, rect.top, rect.right, rect.bottom);
            }
        }
    }

    private void setBackgroundState(int[] stateSet) {
        if (getBackground() != null) {
            getBackground().setState(stateSet);
        }
    }

    private void setGravity(Drawable.ConstantState state, int gravity) {
        try {
            Field field = state.getClass().getDeclaredField("mGravity");
            field.setAccessible(true);
            field.setInt(state, gravity);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
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
            case MotionEvent.ACTION_CANCEL:
                onActionUp(event);
                break;
            default:
                return super.onTouchEvent(event);
        }
        return true;
    }

    private void onActionDown(MotionEvent event) {
        removeCallbacks(noTracking);
        isTracking = true;
        onVerticalSeekBarChangeListener.onStartTrackingTouch(this);
        thumb.setState(STATE_PRESSED);
        setBackgroundState(STATE_PRESSED);
        onActionMove(event);
    }

    private void onActionMove(MotionEvent event) {
        float yPos = event.getY();
        moveThumbTo(yPos, true);
        invalidate();
    }

    private void onActionUp(MotionEvent event) {
        postDelayed(noTracking, 15 * 6);
        onVerticalSeekBarChangeListener.onStopTrackingTouch(this);
        thumb.setState(STATE_NORMAL);
        setBackgroundState(STATE_NORMAL);
        invalidate();
    }

    Runnable noTracking = new Runnable() {
        @Override
        public void run() {
            isTracking = false;
        }
    };

    private void moveThumbTo(float yPos, boolean fromUser) {
        if (yPos < progressDrawable.getBounds().top) {
            yPos = progressDrawable.getBounds().top;// move to top
        } else if (yPos > progressDrawable.getBounds().bottom) {
            yPos = progressDrawable.getBounds().bottom;
        }
        Rect rect = thumb.copyBounds();
        rect.offsetTo(thumb.getBounds().left, (int) (yPos - thumb.getBounds().height() / 2));
        thumb.setBounds(rect);

        float level = pixel2level.getValue2(yPos);
        progress = (int) pixel2progress.getValue2(yPos);
        proDrawable.setLevel((int) level);

        if (onVerticalSeekBarChangeListener != null) {
            onVerticalSeekBarChangeListener.onProgressChanged(this, progress, fromUser);
        }
        setRippleRect(thumb.getBounds());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int dw = Math.max(thumb.getIntrinsicWidth(), progressDrawable.getIntrinsicWidth()) + getPaddingLeft() + getPaddingRight();
        int dh = MeasureSpec.getSize(heightMeasureSpec) + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(dw, dh);

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
        setRippleRect(thumb.getBounds());

        if (direction == DIRECTION_BOTTOM_TO_TOP) {
            pixel2progress.set(progressDrawable.getBounds().top, progressDrawable.getBounds().bottom, max, min);
            pixel2level.set(progressDrawable.getBounds().top, progressDrawable.getBounds().bottom, 10000, 0);// 0 to 10000 from View.setLevel
        } else {
            pixel2progress.set(progressDrawable.getBounds().top, progressDrawable.getBounds().bottom, min, max);
            pixel2level.set(progressDrawable.getBounds().top, progressDrawable.getBounds().bottom, 0, 10000);// 0 to 10000 from View.setLevel
        }

        float yPos = pixel2progress.getValue1(progress);
        moveThumbTo(yPos, false);

        float secYPos = pixel2progress.getValue1(secondaryProgress);
        int level = (int) pixel2level.getValue2(secYPos);
        secProDrawable.setLevel(level);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        progressDrawable.draw(canvas);
        thumb.draw(canvas);
        if (isTracking) {
            postInvalidate();
        }
    }

    public OnVerticalSeekBarChangeListener getOnVerticalSeekBarChangeListener() {
        return onVerticalSeekBarChangeListener;
    }

    public void setOnVerticalSeekBarChangeListener(OnVerticalSeekBarChangeListener onVerticalSeekBarChangeListener) {
        this.onVerticalSeekBarChangeListener = onVerticalSeekBarChangeListener;
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

    public static interface OnVerticalSeekBarChangeListener {
        public void onStartTrackingTouch(VerticalSeekBar seekbar);
        public void onStopTrackingTouch(VerticalSeekBar seekbar);
        public void onProgressChanged(VerticalSeekBar seekbar, int progress, boolean fromUser);
    }
}
