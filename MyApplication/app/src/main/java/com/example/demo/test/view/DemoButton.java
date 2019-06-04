package com.example.demo.test.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.FloatProperty;
import android.view.MotionEvent;
import android.widget.Button;

import com.example.demo.test.R;

public class DemoButton extends Button {
    public static final String TAG = "HtcDemoButton";

    public static final int STATE_START_BEGIN = 1;
    public static final int STATE_START_END = 2;
    public static final int STATE_STOP_BEGIN = 3;
    public static final int STATE_STOP_END = 4;
    private int mState = STATE_STOP_END;

    private Paint mPaint;
    private RectF mPresentRectF;

    private static final int DEFAULT_PADDING = 20;
    private int mAnimationPadding;

    private static final int DEFAULT_STROKE_WIDTH = 8;
    private int mStrokeWidth;

    private float scale = 1;
    private boolean isAnimating = false;
    private ObjectAnimator mScaleStartAnimator;
    private ObjectAnimator mScaleDoneAnimator;
    private static final int DEFAULT_ANIMATION_DURATION = 500;
    private int animationDuration;
    private int mAnimationTint;
    private static final int DEFAULT_ANIMATION_TINT = 0xFF63B8FF;//default blue

    private int mTextColorNormal;
    private int mTextColorPressed;
    private boolean hasPressedColor = false;

    private Drawable mBackgroundNormal;
    private Drawable mBackgroundPressed;
    private boolean hasPressedBackground = false;

    public DemoButton(Context context) {
        super(context);
        init(null);
    }

    public DemoButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public DemoButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    public void init(AttributeSet attrs) {
        TypedArray ta = attrs == null ? null : getContext().obtainStyledAttributes(attrs, R.styleable.DemoButtonStyle);
        if (ta != null) {
            animationDuration = ta.getInt(R.styleable.DemoButtonStyle_animationDuration, DEFAULT_ANIMATION_DURATION);
            mAnimationPadding = ta.getDimensionPixelSize(R.styleable.DemoButtonStyle_animationPadding, DEFAULT_PADDING);
            mAnimationTint = ta.getColor(R.styleable.DemoButtonStyle_animationTint, DEFAULT_ANIMATION_TINT);
            mBackgroundNormal = ta.getDrawable(R.styleable.DemoButtonStyle_backgroundNormal);
            mBackgroundPressed = ta.getDrawable(R.styleable.DemoButtonStyle_backgroundPressed);
            mTextColorNormal = ta.getColor(R.styleable.DemoButtonStyle_textColorNormal, 0);
            mTextColorPressed = ta.getColor(R.styleable.DemoButtonStyle_textColorPressed, 0);
            mStrokeWidth = ta.getDimensionPixelSize(R.styleable.DemoButtonStyle_strokeWidth, DEFAULT_STROKE_WIDTH);
            ta.recycle();
        }
        if (mBackgroundNormal != null && mBackgroundPressed != null) {
            hasPressedBackground = true;
        }

        if (hasPressedBackground) {
            setBackground(mBackgroundNormal);
        }

        if (mTextColorNormal != 0 && mTextColorPressed != 0) {
            hasPressedColor = true;
        }
        if (hasPressedColor) {
            setTextColor(mTextColorNormal);
        }

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPresentRectF = new RectF();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (!isAnimating && mState == STATE_STOP_END) {
                    startClickAnimation();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                if (!isAnimating && mState == STATE_START_END) {
                    stopClickAnimation();
                }

                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isAnimating || mState != STATE_STOP_END) {
            mPaint.setStrokeWidth(mStrokeWidth);
            mPaint.setStyle(Paint.Style.STROKE);
            mPresentRectF.set(mAnimationPadding - scale, mAnimationPadding - scale, getMeasuredWidth() - mAnimationPadding + scale, getMeasuredHeight() - mAnimationPadding + scale);
            mPaint.setColor(mAnimationTint);
            canvas.drawRoundRect(mPresentRectF, mAnimationPadding, mAnimationPadding, mPaint);
        }

    }

    private void setScale(float position) {
        scale = position;
        invalidate();
    }

    private void startClickAnimation() {
        mScaleStartAnimator = ObjectAnimator.ofFloat(this, Scale, 0, mAnimationPadding - mStrokeWidth / 2);
        mScaleStartAnimator.setDuration(animationDuration / 2);
        mScaleStartAnimator.setAutoCancel(true);
        mScaleStartAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAnimating = true;
                mState = STATE_START_BEGIN;
                if (hasPressedColor) {
                    setTextColor(mTextColorPressed);
                }

                if (hasPressedBackground) {
                    setBackground(mBackgroundPressed);
                }

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimating = false;
                mState = STATE_START_END;
                if (!isPressed()) {
                    stopClickAnimation();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mScaleStartAnimator.start();
    }

    private void stopClickAnimation() {

        mScaleDoneAnimator = ObjectAnimator.ofFloat(this, Scale, mAnimationPadding - mStrokeWidth / 2, 0);
        mScaleDoneAnimator.setDuration(animationDuration / 2);
        mScaleDoneAnimator.setAutoCancel(true);
        mScaleDoneAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAnimating = true;
                mState = STATE_STOP_BEGIN;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimating = false;
                mState = STATE_STOP_END;

                if (hasPressedColor) {
                    setTextColor(isPressed() ? mTextColorPressed : mTextColorNormal);
                }

                if (hasPressedBackground) {
                    setBackground(isPressed() ? mBackgroundPressed : mBackgroundNormal);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mScaleDoneAnimator.start();
    }

    private static final FloatProperty<DemoButton> Scale = new FloatProperty<DemoButton>("scale") {
        @Override
        public Float get(DemoButton object) {
            return object.scale;
        }

        @Override
        public void setValue(DemoButton object, float value) {
            object.setScale(value);
        }
    };
}

