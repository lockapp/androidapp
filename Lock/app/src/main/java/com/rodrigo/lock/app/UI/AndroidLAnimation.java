package com.rodrigo.lock.app.UI;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Region;
import android.graphics.Shader;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;

import com.rodrigo.lock.app.R;

/**
 * Created by Rodrigo on 16/08/2014.
 */
public class AndroidLAnimation extends Button {

    private float mDownX;
    private float mDownY;

    private float mRadius;

    private Paint mPaint;

    public AndroidLAnimation(final Context context) {
        super(context);
        init();
    }

    public AndroidLAnimation(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AndroidLAnimation(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAlpha(100);
    }


    public void animar(float x, float y) {
            mDownX = x;
            mDownY = y;

            ObjectAnimator animator = ObjectAnimator.ofFloat(this, "radius", 0, getWidth() * 4.0f);
            animator.setInterpolator(new AccelerateInterpolator());
            animator.setDuration(400);
            animator.start();

    }

    public void setRadius(final float radius) {
        mRadius = radius;
        if (mRadius > 0) {
            RadialGradient radialGradient = new RadialGradient(
                    mDownX,
                    mDownY,
                    mRadius * 3,
                    Color.TRANSPARENT,
                    R.color.bg_secundario_oscuro,
                    Shader.TileMode.MIRROR
            );
            mPaint.setShader(radialGradient);
        }
        invalidate();
    }

    private Path mPath = new Path();
    private Path mPath2 = new Path();

    @Override
    protected void onDraw(@NonNull final Canvas canvas) {
        super.onDraw(canvas);

        mPath2.reset();
        mPath2.addCircle(mDownX, mDownY, mRadius, Path.Direction.CW);

        canvas.clipPath(mPath2);

        mPath.reset();
        mPath.addCircle(mDownX, mDownY, mRadius / 3, Path.Direction.CW);

        canvas.clipPath(mPath, Region.Op.DIFFERENCE);

        canvas.drawCircle(mDownX, mDownY, mRadius, mPaint);
    }
}