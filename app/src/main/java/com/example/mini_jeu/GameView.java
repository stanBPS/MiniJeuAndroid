package com.example.mini_jeu;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Chronometer;

import java.util.Random;

import androidx.constraintlayout.solver.widgets.Rectangle;

import static android.content.Context.SENSOR_SERVICE;


public class GameView extends SurfaceView implements SurfaceHolder.Callback{
    private GameThread thread;
    private int xMin=0;
    private int xMax;
    private int yMin=0;
    private int yMax;
    private int i = 0;
    private float circleRadius = 50;
    private float moveX = 5;
    private float moveY = 6;
    private float circleX;
    private float circleY ;
    private float previousX;
    private float previousY;
    Paint text = new Paint();
    Canvas c =null;
    private Handler mHandler;
    private RectF r1;
    private RectF r2;
    private RectF r3;
    private RectF r4;
    private RectF r5;
    private RectF r6;
    private RectF r7;
    private RectF r8;
    private RectF circle;
    private int x = 0;
    private int moveR =1;
    private int score = 0;
    private float xMaxTemp ;



    public GameView(Context context) {
        super(context);
        thread = new GameThread(getHolder(), this);
        setFocusable(true);
        getHolder().addCallback(this);
        mHandler = new Handler();
        mHandler.postDelayed(mUpdateTimeTask, 1000);
        this.setFocusableInTouchMode(true);

    }

    private Runnable mUpdateTimeTask = new Runnable() {
        @Override
        public void run() {
                i++;
                mHandler.postDelayed(this, 1000);
        }
    };
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                thread.setRunning(false);
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retry = false;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas != null) {

            canvas.drawColor(Color.WHITE);
            Paint paint = new Paint();
            Paint paint2 = new Paint();
            paint.setColor(Color.rgb(250, 0, 0));

            circle = new RectF();
            circle.set(circleX-circleRadius , circleY-circleRadius , circleX+circleRadius,circleY+circleRadius);
            canvas.drawOval(circle, paint);

            r1 = new RectF(xMax-300,(yMax/2)-440,xMin,(yMax/2)-460);
            canvas.drawRect(r1,paint);

            r2 = new RectF(xMax/2,(yMax/2)-240,1200,(yMax/2)-260);
            canvas.drawRect(r2,paint);

            r3 = new RectF(xMax-300,(yMax/2)-40,xMin,(yMax/2)-60);
            canvas.drawRect(r3,paint);

            r4 = new RectF(xMax/2,(yMax/2)+140,1200,(yMax/2)+160);
            canvas.drawRect(r4,paint);


            //r5 = new RectF(500,950,0,970);
            //canvas.drawRect(r5,paint);
            //r6 = new RectF(400,1150,1200,1170);
            r6 = new RectF(x,yMax-15,x+120,yMax-2);
            r7 = new RectF(x,yMax-70,x+15,yMax-2);
            r8 = new RectF(x+120,yMax-70,x+135,yMax-2);
            canvas.drawRect(r6,paint);
            canvas.drawRect(r7,paint);
            canvas.drawRect(r8,paint);


           if(circle.contains(xMax-302,(yMax/2)-440,xMin,(yMax/2)-460)  || circle.contains(xMax-302,(yMax/2)-40,xMin,(yMax/2)-60) /*||   circle.contains(500,950,0,950)*/){
               paint.setColor(Color.BLACK);
               paint.setTextSize(50);
               canvas.drawText("PERDU",(xMax/2)-55,(yMax/2)-90,paint);
              thread.setRunning(false);
           }
            if(circle.intersect(r2) || circle.intersect(r4) /*|| circle.intersect(r6)*/){
                paint.setColor(Color.BLACK);
                paint.setTextSize(50);
                canvas.drawText("PERDU",(xMax/2)-55,(yMax/2)-90,paint);
                thread.setRunning(false);
            }

            if (circle.intersect(r6)){
                xMaxTemp = yMax-2;
                score++;
            }

            if (yMax-2 == xMaxTemp){
                paint.setColor(Color.BLACK);
                paint.setTextSize(50);
                canvas.drawText("GAGNER",(xMax/2)-70,(yMax/2)-90,paint);
                thread.setRunning(false);
            }

            if (circleY + circleRadius > yMax || circleY - circleRadius < yMin || circleX + circleRadius > xMax ||circleX - circleRadius < xMin){
                paint.setColor(Color.BLACK);
                paint.setTextSize(50);
                canvas.drawText("PERDU",(xMax/2)-55,(yMax/2)-90,paint);
            }

            paint.setColor(Color.BLACK);
            paint.setTextSize(30);
            canvas.drawText("Temps: "+i,(xMax-180),yMin+45,paint);

        }
    }

    public void update() {


        circleY += 0.8;
        x += moveR;
        if (x+120 > xMax){
            moveR= -moveR;
            x = xMax-120;
        }else if (x < xMin){
            moveR= -moveR;
            x = xMin;
        }

        if (circleX + circleRadius > xMax) {
            Log.d("Touché", "droite");
            thread.setRunning(false);
        } else if (circleX - circleRadius < xMin) {
            Log.d("Touché", "gauche");
            thread.setRunning(false);
        }

        if (circleY + circleRadius > yMax) {
            Log.d("Touché", "bas");
            thread.setRunning(false);
        } else if (circleY - circleRadius < yMin) {
            Log.d("Touché", "haut");
            thread.setRunning(false);
        }
    }


    @Override
    public boolean onTouchEvent( MotionEvent event) {
        int currentX = (int)event.getRawX();
        int currentY = (int)event.getRawY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                        circleX = (currentX ) ;
                break;
        }
        return true;
    }

    @Override
    public void onSizeChanged(int w, int h, int oldW, int oldH) {
        xMax = w-1;
        yMax = h-1;
        circleX = xMax/2 ;
        circleY = circleRadius;
    }
}
