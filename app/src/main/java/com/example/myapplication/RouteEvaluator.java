package com.example.myapplication;

import android.animation.TypeEvaluator;
import com.google.android.gms.maps.model.LatLng;

/**
 * last update: 2020/4/12
 * by: Jinyao.Xu
 */

//the Evaluator used in the animation for drawing line
public class RouteEvaluator implements TypeEvaluator<LatLng> {

    @Override
    public LatLng evaluate(float t, LatLng startPoint, LatLng endPoint) {
        double lat = startPoint.latitude + t * (endPoint.latitude - startPoint.latitude);
     double lng = startPoint.longitude + t * (endPoint.longitude - startPoint.longitude);

        return new LatLng(lat, lng);

    }
}