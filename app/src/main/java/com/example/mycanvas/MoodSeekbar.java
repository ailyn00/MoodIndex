package com.example.mycanvas;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.SeekBar;

@SuppressLint("AppCompatCustomView")
public class MoodSeekbar extends SeekBar {
    private Rect rect;
    private Paint paint ;
    private int seekbar_height;

    public MoodSeekbar(Context context) {
        super(context);
    }

    public MoodSeekbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        rect = new Rect();
        paint = new Paint();
        seekbar_height = 6;
    }

    public MoodSeekbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {

        rect.set(0 + getThumbOffset(),
                (getHeight() / 2) - (seekbar_height),
                getWidth()- getThumbOffset(),
                (getHeight() / 2) + (seekbar_height));

        paint.setColor(Color.BLACK);

        canvas.drawRect(rect, paint);



        if (this.getProgress() > 100) {


            rect.set(getWidth() / 2,
                    (getHeight() / 2) - 60,
                    getWidth() / 2 + (getWidth() / 200) * (getProgress() - 100),
                    getHeight() / 2 + 60);

            paint.setColor(Color.GREEN);
            canvas.drawRect(rect, paint);

        }

        if (this.getProgress() < 100) {

            rect.set(getWidth() / 2 - ((getWidth() / 200) * (100 - getProgress())),
                    (getHeight() / 2) - 60,
                    getWidth() / 2,
                    getHeight() / 2 + 60);

            paint.setColor(Color.RED);
            canvas.drawRect(rect, paint);

        }

        super.onDraw(canvas);
    }

}
