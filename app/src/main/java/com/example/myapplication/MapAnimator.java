package com.example.myapplication;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * last update: 2020/4/12
 * by: Jinyao.Xu
 */

public class MapAnimator {

    private static MapAnimator mapAnimator;

// Google map attributes
    private PolylineOptions optionsForeground;
    private Polyline backgroundPolyline;
    private Polyline foregroundPolyline;

//a list to store animators for drawing line
    private List<Animator> AnimationList = new ArrayList<Animator>();

//a list used in drawing line
    private List<LatLng> DrawLinelist = new ArrayList<LatLng>();

 //animator attributes
    private ObjectAnimator foregroundRouteAnimator;
    private AnimatorSet LoopRunAnimSet;
    private AnimatorSet DrawLineSet;
    static final int GREY = Color.parseColor("#FFA7A6A6");

 //construction method
    private MapAnimator() {

    }

//get mapAnimator object to be called by other class
    public static MapAnimator getInstance() {
        if (mapAnimator == null) {
            mapAnimator = new MapAnimator();
        }
        return mapAnimator;
    }

    public void animateRoute(GoogleMap googleMap, final List<LatLng> bangaloreRoute) {
//initialize animations
        if (LoopRunAnimSet == null) {
            LoopRunAnimSet = new AnimatorSet();
        } else {
            LoopRunAnimSet.removeAllListeners();
            LoopRunAnimSet.end();
            LoopRunAnimSet.cancel();
            LoopRunAnimSet = new AnimatorSet();
        }
        if (DrawLineSet == null) {
            DrawLineSet = new AnimatorSet();
        } else {
            DrawLineSet.removeAllListeners();
            DrawLineSet.end();
            DrawLineSet.cancel();
            DrawLineSet = new AnimatorSet();
        }
        AnimationList.clear();

//Initialize the polylines and its options
        if (foregroundPolyline != null) {
            foregroundPolyline.remove();
            optionsForeground = null;
        }
        if (backgroundPolyline != null) {
            backgroundPolyline.remove();
        }
        optionsForeground = new PolylineOptions().add(bangaloreRoute.get(0)).color(GREY).width(5);
        backgroundPolyline = googleMap.addPolyline(optionsForeground);
        foregroundPolyline = googleMap.addPolyline(optionsForeground);
        foregroundPolyline.setColor(Color.GREEN);

//create an object animator to add line between two markers
// and use a for loop to build the line within several markers
        for(int i = 0 ; i< bangaloreRoute.size()-1 ; i++) {
            DrawLinelist.clear();
            DrawLinelist.add(bangaloreRoute.get(i));
            DrawLinelist.add(bangaloreRoute.get(i + 1));
            foregroundRouteAnimator = ObjectAnimator
                    .ofObject(this, "routeIncreaseForward", new RouteEvaluator(), DrawLinelist.toArray());
            foregroundRouteAnimator.setDuration(2000);
            foregroundRouteAnimator.setStartDelay(10);
            AnimationList.add(foregroundRouteAnimator);
        }

//define the animators in list as an animator set to play in a sequence
        DrawLineSet.playSequentially(AnimationList);
        DrawLineSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
 //the line drawing animator only draw foregroundline because of its property method we define later
 // set points in backgroundline as points in foregroundline
                backgroundPolyline.setPoints(foregroundPolyline.getPoints());

//line drawing finishes, start the animator set of changing length of foregroundline
                LoopRunAnimSet.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

//start animator set for drawing line
        DrawLineSet.start();

//create an int animator to reduce the length of polyline (foreground)
        final ValueAnimator ReduceCompletion = ValueAnimator.ofInt(0, 100);
        ReduceCompletion.setDuration(3000*bangaloreRoute.size());
        ReduceCompletion.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                List<LatLng> foregroundPoints = backgroundPolyline.getPoints();
                int percentageValue = (int) animation.getAnimatedValue();
                int pointcount = foregroundPoints.size();
                int countTobeRemoved = (int)(pointcount * (percentageValue / 100.0f));
                List<LatLng> subListTobeRemoved = foregroundPoints.subList(0, countTobeRemoved);
                subListTobeRemoved.clear();
                foregroundPolyline.setPoints(foregroundPoints);
            }
        });

//create an int animator to add the length of polyline (foreground)
        final ValueAnimator AddCompletion = ValueAnimator.ofInt(0, 100);
        AddCompletion.setDuration(2000*bangaloreRoute.size());
        AddCompletion.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                List<LatLng> foregroundPoints = backgroundPolyline.getPoints();
                int LineSize = foregroundPoints.size();
                int percentageValue = (int) animation.getAnimatedValue();
                int addPercent = 100 - percentageValue;
                int pointcount = foregroundPoints.size();
                int countTobeAdded = (int)(pointcount * (addPercent / 100.0f));
                int StartPoint = LineSize - countTobeAdded;
                List<LatLng> subListTobeAdded = foregroundPoints.subList(StartPoint, LineSize);
                subListTobeAdded.clear();
                foregroundPolyline.setPoints(foregroundPoints);
            }
        });

//combine ReduceCompletion and AddCompletion into an animation set
        LoopRunAnimSet.playSequentially(ReduceCompletion,AddCompletion);
        LoopRunAnimSet.setStartDelay(200);
        LoopRunAnimSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

//loop the set when it ends
            @Override
            public void onAnimationEnd(Animator animation) {
                LoopRunAnimSet.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

//This will be invoked by foregroundRouteAnimator using its property name multiple times to draw line
    public void setRouteIncreaseForward(LatLng endLatLng) {
//add new points in foregroundline
        List<LatLng> foregroundPoints = foregroundPolyline.getPoints();
        foregroundPoints.add(endLatLng);
        foregroundPolyline.setPoints(foregroundPoints);
    }

}
