package com.example.snakegame;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

public class SnakeGame {
    public int width;
    public int height;
    public Snake snake;
    public Bitmap bitmap;
    public int score;
    public int speed;
    private Path mhead=new Path();
    private Path mtail=new Path();
    private Path mbody=new Path();
    public DrawDrawable board;
    public CountDownTimer timerMain;
    public boolean paused=false;
    public mGameOverListener gameOverListener;
   public Apple apple;
    SnakeGame(int w, int h) {
    width=w;
    height=h;
    board=new DrawDrawable();
    }

    public void start(){
        snake=new Snake(10,10,4,width, height);
        apple= new Apple(width,height);
        timerMain= new CountDownTimer(20000000,2000/snake.tail.size()) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (!paused) snake.move();
                checkForTriggers();
            }
            @Override
            public void onFinish() {
                stop();
            }
        };
        timerMain.start();
        paused=false;
    }

    public void pause(){

    }
    public void stop(){
        timerMain.cancel();
        paused=true;
        if (gameOverListener != null)
            gameOverListener.raiseEvent("game over"); // event result object :)
    }

    public void setGameOverListener(mGameOverListener ml){
        gameOverListener=ml;
    }
    public void checkForTriggers(){
        if (snake.tail.get(0).x==apple.x && snake.tail.get(0).y==apple.y) {
            snake.tail.add(new Tail(apple.x,apple.y));
            apple.collect();
            timerMain.cancel();
            timerMain= new CountDownTimer(20000000,2000/snake.tail.size()) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (!paused) snake.move();
                    checkForTriggers();
                }
                @Override
                public void onFinish() {
                    stop();
                }
            };
            timerMain.start();
        }
        for (int i = 1; i < snake.tail.size()-1; i++) {
            if (snake.tail.get(0).x==snake.tail.get(i).x && snake.tail.get(0).y==snake.tail.get(i).y){
                stop();
            }
        }
    }
    class DrawDrawable extends Drawable{
        private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private Path mPath = new Path();
        private Paint p = new Paint();
        private Rect rect = new Rect();

        @Override
        public void draw(Canvas canvas) {
            canvas.drawColor(Color.GRAY);
            // настройка кисти
            // красный цвет
            p.setColor(Color.BLACK);
            // толщина линии = 10
            p.setStrokeWidth(0);
            for (int i = snake.tail.size()-1; i >=0; i--){
                rect.set(50*snake.tail.get(i).x+5, 50*snake.tail.get(i).y+5, 50*snake.tail.get(i).x+50-5, 50*snake.tail.get(i).y+50-5);
                canvas.drawRect(rect, p);
            }
            rect.set(50*apple.x+5,50*apple.y+5,50*apple.x+50-5,50*apple.y+50-5);
            canvas.drawRect(rect, p);
            p.setTextSize(50);
            p.setFakeBoldText(true);
            canvas.drawText("Длина: "+ String.valueOf(snake.tail.size()),10,100,p);
            this.invalidateSelf();
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }

        @Override
        public void setAlpha(int alpha) {
            mPaint.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            mPaint.setColorFilter(colorFilter);
        }

        @Override
        protected void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);
        }

    }
}
class Snake{
    public int size;
    public List<Tail> tail=  new ArrayList<Tail>();
    public int x;
    public int y;
    public int maxx;
    public int maxy;
    public int direction;
    public boolean turninglocked=false;
    Snake(int mx,int my, int ms, int xmax, int ymax){
        x=mx;
        y=my;
        size=ms;
        maxx=xmax;
        maxy=ymax;
        for (int i = 0; i < size; i++) {
            tail.add(new Tail(mx-i,my));
        }
    }
    public void move() {
            for (int i = tail.size()-1; i >=0; i--)
            {
                if (i>0) {
                    tail.get(i).x = tail.get(i - 1).x;
                    tail.get(i).y = tail.get(i - 1).y;
                }
                else{
                    if (direction==0){tail.get(0).y-=1;};
                    if (direction==1){tail.get(0).x+=1;};
                    if (direction==2){tail.get(0).y+=1;};
                    if (direction==3){tail.get(0).x-=1;};
                    if (tail.get(0).x<0) tail.get(0).x=maxx;
                    if (tail.get(0).y<0) tail.get(0).y=maxy;
                    if (tail.get(0).x>maxx) tail.get(0).x=0;
                    if (tail.get(0).y>maxy) tail.get(0).y=0;
                }
            }
    }
    public void turnRight(){
        direction+=1;
        if (direction>3) direction=0;
    }
    public void turnLeft(){
        direction-=1;
        if (direction<0) direction=3;
    }
}
class Tail {
    public int x;
    public int y;
    Tail(int mx,int my){
        x=mx;
        y=my;
    }
}
class Apple{
    public int x;
    public int y;
    int maxx;
    int maxy;
    final Random random = new Random();
    Apple(int mx, int my){
        maxx=mx;
        maxy=my;
        collect();
    }
    public void collect(){
        x=random.nextInt(maxx);
        y=random.nextInt(maxy);
    }

}
interface mGameOverListener {
    // you can define any parameter as per your requirement
    void raiseEvent(String result);

}