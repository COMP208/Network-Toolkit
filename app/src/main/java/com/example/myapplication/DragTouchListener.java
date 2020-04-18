package com.example.myapplication;



import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

/**
 * implement View with dragging and zooming function
 * usage：
 *  1. initialize DragTouchListener object ；
 *  2. set listener: view.setOnTouchListener(DragTouchListener);
 *
 * last update: 2020/4/12
 * by: Weihao.Jin
 */
public class DragTouchListener implements View.OnTouchListener {

    private DisplayMetrics dm;
    private int maxWidth;
    private int maxHeight;
    private int lastX;
    private int lastY;
    private int oriLeft;
    private int oriRight;
    private int oriTop;
    private int oriBottom;
    private float baseValue;
    private DragListener dragListener;
    float originalScale;
    private static final int TOUCH_NONE = 0x00;
    private static final int TOUCH_ONE = 0x20;
    private static final int TOUCH_TWO = 0x21;
    /**
     * current touching mode
     */
    private int currentTouchMode = TOUCH_NONE ;

    private boolean touchTwoZoomEnable = true;

    private boolean isCancleTouchDrag = false;

    /**
     * the dragging and zooming effect
     */
    private View mEffectView ;

    /**
     * Controls whether to turn on two-finger touch zooming
     * @param touchTwoZoomEnable
     */
    public DragTouchListener setTouchTwoZoomEnable(boolean touchTwoZoomEnable) {
        this.touchTwoZoomEnable = touchTwoZoomEnable;
        return this;
    }

    /**
     * Settings: cancel drag and drop movement
     * @param cancleTouchDrag
     */
    public DragTouchListener setCancleTouchDrag(boolean cancleTouchDrag) {
        isCancleTouchDrag = cancleTouchDrag;
        return this;
    }

    public interface DragListener {
        void actionDown(View v);

        void actionUp(View v);

        void dragging(View listenerView, int left, int top, int right, int bottom);

        void zooming(float scale);
    }


    public DragTouchListener(final ViewGroup limitParent,DragListener dragListener) {
        this(limitParent);
        this.dragListener = dragListener;
    }

    public DragTouchListener(){
        this(null);
    }

    /**
     *
     * @param limitParent
     */
    public DragTouchListener(final ViewGroup limitParent) {
        if (limitParent !=null) {
            ViewTreeObserver vto = limitParent.getViewTreeObserver();
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    maxHeight = limitParent.getMeasuredHeight();
                    maxWidth = limitParent.getMeasuredWidth();

                    return true;
                }

            });
        }
        dragListener = new DragListener() {
            @Override
            public void actionDown(View v) {

            }
            @Override
            public void actionUp(View v) {
            }
            @Override
            public void dragging(View listenerView, int left, int top, int right, int bottom) {

            }
            @Override
            public void zooming(float scale) {
            }
        };
    }

    private boolean moveFlag ;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction()& MotionEvent.ACTION_MASK;
        v.getParent().requestDisallowInterceptTouchEvent(true);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                dragListener.actionDown(v);
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                oriLeft = v.getLeft();
                oriRight = v.getRight();
                oriTop = v.getTop();
                oriBottom = v.getBottom();
                currentTouchMode = TOUCH_ONE;
                baseValue = 0;
                lastScale = 1;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oriLeft = v.getLeft();
                oriRight = v.getRight();
                oriTop = v.getTop();
                oriBottom = v.getBottom();
                currentTouchMode = TOUCH_TWO;
                baseValue = 0;
                lastScale = 1 ;
                break;
            /**
             * layout(l,t,r,b)
             * l  Left position, relative to parent
             t  Top position, relative to parent
             r  Right position, relative to parent
             b  Bottom position, relative to parent
             * */
            case MotionEvent.ACTION_MOVE:

                moveFlag = !moveFlag;
                if (event.getPointerCount() == 2) {

                    if (touchTwoZoomEnable) {
                        float x = event.getX(0) - event.getX(1);
                        float y = event.getY(0) - event.getY(1);


                        float value = (float) Math.sqrt(x * x + y * y);
                        if (baseValue == 0) {
                            baseValue = value;

                        } else {
                            if ((value - baseValue) >= 10 || value - baseValue <= -10) {

                                float scale = value / baseValue;

                                touchZoom(v,scale);

                                this.dragListener.zooming(scale);

                            }
                        }
                    }
                } else if (currentTouchMode == TOUCH_ONE) {

                    if(isCancleTouchDrag){
                        return false;
                    }

                    touchDrag(v, event);
                }

                break;
            case MotionEvent.ACTION_UP:
                baseValue = 0;
                dragListener.actionUp(v);
                break;
            default:
                currentTouchMode = TOUCH_NONE;
                break;
        }
        return true;
    }


    private float lastScale = 1;
    /**
     * zooming action
     * @param v
     * @param scale
     */
    private void touchZoom(View v,float scale){
        int oriWidth = Math.abs(oriRight - oriLeft);
        int oriHeight = Math.abs(oriBottom - oriTop);
        float  zoomScale = (lastScale - scale);

        int dx = (int) (oriWidth*zoomScale/2f);
        int dy = (int) (oriHeight*zoomScale/2f);


        int left = v.getLeft() + dx;
        int top = v.getTop() + dy;
        int right = v.getRight() - dx;
        int bottom = v.getBottom() - dy;

        v.layout(left, top, right, bottom);
        if(v instanceof myCanvas){
            Log.e("zoom","一句话");
            myCanvas bmView = (myCanvas)v;
        }

        lastScale = scale;

    }

    private void touchDrag(View v, MotionEvent event) {

        int dx = (int) event.getRawX() - lastX;
        int dy = (int) event.getRawY() - lastY;

        int left = v.getLeft() + dx;
        int top = v.getTop() + dy;
        int right = v.getRight() + dx;
        int bottom = v.getBottom() + dy;

        if (maxWidth !=0 && maxHeight!=0) {

            if(left < 0){
                left = 0;
                right = left + v.getWidth();
            }
            if(right > maxWidth){
                right = maxWidth;
                left = right - v.getWidth();
            }
            if(top < 0){
                top = 0;
                bottom = top + v.getHeight();
            }
            if(bottom > maxHeight){
                bottom = maxHeight;
                top = bottom - v.getHeight();
            }
        }

        v.layout(left, top, right, bottom);

        dragListener.dragging(v, left, top, right, bottom);
        lastX = (int) event.getRawX();
        lastY = (int) event.getRawY();
    }

}