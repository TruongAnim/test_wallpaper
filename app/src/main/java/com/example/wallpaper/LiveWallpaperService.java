package com.example.wallpaper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

public class LiveWallpaperService extends WallpaperService
{
    int x,y;

    public void onCreate()
    {
        super.onCreate();
    }

    public void onDestroy()
    {
        super.onDestroy();
    }

    public Engine onCreateEngine()
    {
        return new MyWallpaperEngine();
    }

    class MyWallpaperEngine extends Engine
    {

        private final Handler handler = new Handler();
        private final Runnable drawRunner = new Runnable() {
            @Override
            public void run() {
                draw();
            }
        };
        private boolean visible = true;
        public Bitmap image1,backgroundImage;

        MyWallpaperEngine()
        {
            // get the fish and background image references
            image1 = BitmapFactory.decodeResource(getResources(),R.drawable.aaa);
            backgroundImage = BitmapFactory.decodeResource(getResources(),R.drawable.aaa);
            x=-130; // initialize x position
            y=200;  // initialize y position

        }


        public void onCreate(SurfaceHolder surfaceHolder)
        {
            super.onCreate(surfaceHolder);
        }

        @Override
        public void onVisibilityChanged(boolean visible)
        {
            this.visible = visible;
            // if screen wallpaper is visible then draw the image otherwise do not draw
            if (visible)
            {
                handler.post(drawRunner);
            }
            else
            {
                handler.removeCallbacks(drawRunner);
            }
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder)
        {
            super.onSurfaceDestroyed(holder);
            this.visible = false;
            handler.removeCallbacks(drawRunner);
        }

        public void onOffsetsChanged(float xOffset, float yOffset, float xStep, float yStep, int xPixels, int yPixels)
        {
            draw();
        }

        void draw()
        {
            final SurfaceHolder holder = getSurfaceHolder();

            Canvas c = null;
            try
            {
                c = holder.lockCanvas();
                // clear the canvas
                c.drawColor(Color.BLACK);
                if (c != null)
                {
                    // draw the background image
                    c.drawBitmap(backgroundImage, 0, 0, null);
                    // draw the fish
                    c.drawBitmap(image1, x,y, null);
                    // get the width of canvas
                    int width=c.getWidth();

                    // if x crosses the width means  x has reached to right edge
                    if(x>width+100)
                    {
                        // assign initial value to start with
                        x=-130;
                    }
                    // change the x position/value by 1 pixel
                    x=x+1;
                }
            }
            finally
            {
                if (c != null)
                    holder.unlockCanvasAndPost(c);
            }

            handler.removeCallbacks(drawRunner);
            if (visible)
            {
                handler.postDelayed(drawRunner, 10); // delay 10 mileseconds
            }

        }
    }
}