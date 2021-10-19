package com.example.mycanvas;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceControlViewHost;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class canvasexample extends View  {
    private static final int SquareSizeDef = 200;
    private Rect mRectSquare;
    private Paint mPaintSquare;
    private Paint mPaintCircle;
    private int mSquareColor;
    private int mSquareSize;
    private float mCircleX,mCircleY;
    private float mCircleRadius = 100f;
    public canvasexample(Context context){
        super(context);
        init(null);
    }



    public canvasexample(Context context , AttributeSet attrs){
    super(context,attrs);
    init(attrs);

}
public canvasexample(Context context , AttributeSet attrs , int defStyleAttrs){
        super(context,attrs,defStyleAttrs);
        init(attrs);
}
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public canvasexample (Context context , AttributeSet attrs , int defStyleAttrs, int defStyleRes){
    super(context,attrs,defStyleAttrs,defStyleRes);

    init(attrs);
}
    private void init(@Nullable AttributeSet set) {
        mRectSquare = new Rect();
        mPaintSquare = new Paint(Paint.ANTI_ALIAS_FLAG); // for smoother color

        mPaintCircle = new Paint();
        mPaintCircle.setAntiAlias(true);
        mPaintCircle.setColor(Color.parseColor("#00caff"));
        if(set == null) // for null pointer exception
            return;
        TypedArray ta = getContext().obtainStyledAttributes(set,R.styleable.canvasexample); // to access the resource file
        mSquareColor = ta.getColor(R.styleable.canvasexample_square_color,Color.GREEN);
        mSquareSize = ta.getDimensionPixelSize(R.styleable.canvasexample_square_size,SquareSizeDef);
        mPaintSquare.setColor(mSquareColor);
        ta.recycle();
    }
    public void swapColor(){
        mPaintSquare.setColor(mPaintSquare.getColor()== mSquareColor? Color.RED: mSquareColor);
       // invalidate(); // block ui
        postInvalidate(); // doesnot block the ui
    }

@Override
protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    mRectSquare.left = 50;
    mRectSquare.top = 50;
    mRectSquare.right= mRectSquare.left + SquareSizeDef;
    mRectSquare.bottom= mRectSquare.top+ SquareSizeDef;



   canvas.drawRect(mRectSquare,mPaintSquare);
   canvas.drawCircle(mCircleX,mCircleY,mCircleRadius,mPaintCircle);
   if(mCircleX == 0f || mCircleY ==0f){
       mCircleX=getWidth()/2;
       mCircleY=getHeight()/2;
   }
}
@Override
    public boolean onTouchEvent(MotionEvent event){
      boolean value = super.onTouchEvent(event);

      switch ( event.getAction()){
          case MotionEvent.ACTION_DOWN:{
              float x   =event.getX();
              float y = event.getY();
               if (mRectSquare.left< x && mRectSquare.right >x )
                   if(mRectSquare.top <y && mRectSquare.bottom  > y){
                       mCircleRadius +=10f;
                       postInvalidate();
                   }
              return true;
          }
          case MotionEvent.ACTION_MOVE:{
              float x   =event.getX();
              float y = event.getY();

              double dx =Math.pow(x - mCircleX,2);
              double dy = Math.pow(y-mCircleY,2);
              if(dx+dy < Math.pow(mCircleRadius,2)){
                  //touched
                  mCircleX = x;
                  mCircleY = y;
                  postInvalidate();
                  return true;
              }
                return value;
          }
      }

      return value;
}
}
