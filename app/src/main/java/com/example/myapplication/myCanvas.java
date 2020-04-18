package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

/**
 * last update: 2020/4/12
 * by: Minhao.Jin
 */
public class myCanvas extends View {

    Paint paint;
    ArrayList<DeviceInLAN> devices;

    /**
     * @param context
     */
    public myCanvas(Context context) {
        super(context);
        paint = new Paint();
        devices = lan_managerActivity.devices;
    }

    /**
     * Initiate the canvas
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor("#00CC33"));

        float centerX = canvas.getWidth() / 2;
        float centerY = canvas.getHeight() / 2;
        float radius = canvas.getWidth() / 7 * 2;
        double angle = 2 * Math.PI / (devices.size() - 1);

        int j = 0;
        for (int i = 0; i < devices.size(); i++) {
            float x = (float) (centerX + Math.cos(angle * j) * radius);
            float y = (float) (centerY + Math.sin(angle * j) * radius);
            DeviceInLAN d = devices.get(i);
            if (!d.type.equals("router")) {
                addIcon(d, canvas, x, y);
                j++;
            } else {
                addIcon(d, canvas, centerX, centerY);
            }
        }
    }

    /**
     * Add image to the canvas
     *
     * @param d
     * @param canvas
     * @param x
     * @param y
     */
    private void addIcon(DeviceInLAN d, Canvas canvas, float x, float y) {
        String type = d.type;
        Bitmap bitmap = null;
        switch (type) {
            case "router":
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.router);
                break;
            case "phone":
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.phone);
                break;
            case "pc":
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pc);
                break;
            case "unknown":
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.unknown);
                break;
        }

        float bit_x = x - bitmap.getWidth() / 2;
        float bit_y = y - bitmap.getHeight() / 2;

        canvas.drawBitmap(bitmap, bit_x, bit_y, paint);
        addStampToImage(bitmap, paint, canvas, x, bit_y, d.IP);
        addStampToImage(bitmap, paint, canvas, x, bit_y + 40, d.brand);
    }

    /**
     * Add text to canvas
     *
     * @param originalBitmap
     * @param paint
     * @param canvas
     * @param x
     * @param y
     * @param text
     */
    public void addStampToImage(Bitmap originalBitmap, Paint paint, Canvas canvas, float x, float y, String text) {
        Log.e("", String.valueOf(originalBitmap.getWidth()));
        paint.setTextSize(30);

        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        int length = bounds.width();
        canvas.drawText(text, x - length / 2, y + originalBitmap.getHeight() + 10, paint);
    }

}
