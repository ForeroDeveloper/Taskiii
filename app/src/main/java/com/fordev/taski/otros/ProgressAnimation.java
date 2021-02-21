package com.fordev.taski.otros;

import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.google.android.material.progressindicator.LinearProgressIndicator;

public class ProgressAnimation extends Animation {
    private LinearProgressIndicator progressBar;
    private float from;
    private float  to;

    public ProgressAnimation(LinearProgressIndicator progressBar, float from, float to) {
        super();
        this.progressBar = progressBar;
        this.from = from;
        this.to = to;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        float value = from + (to - from) * interpolatedTime;
        progressBar.setProgress((int) value);
    }

}
