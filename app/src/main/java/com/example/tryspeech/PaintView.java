package com.example.tryspeech;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class PaintView extends View {
    public ViewGroup.LayoutParams params;
    private int currentColor;
    private List<Stroke> allStrokes = new ArrayList<>();
    private Paint brush;
    public float currentX;
    public float currentY;

    // have a path field that can be accessed by MainActivity
    // so that we can change the path Paint
    // also, stop the current path, and make a new path that starts from the end of the
    // previous path

    public PaintView(Context context) {
        super(context);
        currentColor = Color.RED;
        params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void changeBrushColor(String color) {
         currentColor = Color.parseColor(color);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int pointX = (int) event.getX();
        int pointY = (int) event.getY();

        currentX = pointX;
        currentY = pointY;

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Path path = new Path();
                brush = new Paint();
                brush.setAntiAlias(true);
                brush.setColor(currentColor);
                brush.setStyle(Paint.Style.STROKE);
                // brush.setStyle(Paint.Style.FILL_AND_STROKE);
                // brush.setStyle(Paint.Style.FILL);
                brush.setStrokeJoin(Paint.Join.ROUND);
                if (MainActivity.EraserMode) {
                    brush.setStrokeWidth(40f);
                } else {
                    brush.setStrokeWidth(8f);
                }
                path.moveTo(pointX, pointY);
                allStrokes.add(new Stroke(path, brush));
                postInvalidate();
                return true;
            case MotionEvent.ACTION_UP:
                path = allStrokes.get(allStrokes.size() - 1).getPath();
                allStrokes.get(allStrokes.size() - 1).getPaint().setStyle(Paint.Style.FILL);
                path.close();
            case MotionEvent.ACTION_MOVE:
                path = allStrokes.get(allStrokes.size() - 1).getPath();
                path.lineTo(pointX, pointY);
                break;
            default:
                return false;
        }
        postInvalidate();
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (Stroke s : allStrokes) {
            canvas.drawPath(s.getPath(), s.getPaint());
        }
    }
}