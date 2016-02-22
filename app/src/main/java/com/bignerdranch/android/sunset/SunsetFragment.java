package com.bignerdranch.android.sunset;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

public class SunsetFragment extends Fragment {

    private View mSceneView;
    private View mSunView;
    private View mSkyView;
    private View mRingView;
    private View mReflectionView;

    private int mBlueSkyColor;
    private int mSunsetSkyColor;
    private int mNightSkyColor;

    private ObjectAnimator mSunAnimator;
    private ObjectAnimator mRingAnimator;
    private ObjectAnimator mSkyAnimator;
    private ObjectAnimator mNightAnimator;
    private ObjectAnimator mReflectionAnimator;

    AnimatorSet mAnimator;

    AnimationObjects mObjects;

    private static final long DURATION = 3000;

    private boolean mSunSet;

    public static SunsetFragment newInstance() {
        return new SunsetFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sunset, container, false);

        mSceneView = view;
        mSunView = view.findViewById(R.id.sun);
        mSkyView = view.findViewById(R.id.sky);
        mRingView = view.findViewById(R.id.sun_ring);
        mReflectionView = view.findViewById(R.id.sun_reflection);

        mObjects = new AnimationObjects(view);

        Resources resources = getResources();
        mBlueSkyColor = resources.getColor(R.color.blue_sky);
        mNightSkyColor = resources.getColor(R.color.night_sky);
        mSunsetSkyColor = resources.getColor(R.color.sunset_sky);

        ObjectAnimator ringFadeAnimator = ObjectAnimator
                .ofFloat(mRingView, "alpha", 0f, 0.4f)
                .setDuration(1500);
        ringFadeAnimator.setRepeatCount(Integer.MAX_VALUE);
        ringFadeAnimator.start();

        mSunSet = true;

        mSceneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mObjects.setObjectPositions(); //Set current object positions

                long playTime, nightTime;

                if (mSunAnimator == null || !mSunAnimator.isRunning()) {
                    if (mSunAnimator == null || mSunSet) {
                        playTime = 0;   //did not run while sunrise before = did not reach it
                    } else {
                        playTime = DURATION;
                    }
                } else {    //not null && running = currently using it
                    playTime = mSunAnimator.getCurrentPlayTime();
                }

                if (mNightAnimator == null || !mNightAnimator.isRunning()) {
                    if (mNightAnimator == null || !mSunSet) {
                        nightTime = 0;  //sunset before this && not running = did not reach it
                    } else {
                        nightTime = DURATION / 2;
                    }
                } else {
                    nightTime = mNightAnimator.getCurrentPlayTime();
                }

                if (mAnimator != null && !mAnimator.isRunning()) { //Ran through the entire thing
                    playTime = 0;
                    nightTime = 0;
                }

                if (mSunSet) {
                    if (mAnimator != null && mAnimator.isRunning()) {
                        mAnimator.end();
                        mAnimator = new AnimatorSet();
                        startAnimationSet(DURATION - playTime, DURATION / 2 - nightTime);
                    } else {
                        mAnimator = new AnimatorSet();
                        startAnimationSet(playTime, nightTime);
                    }
                } else {
                    if (mAnimator != null && mAnimator.isRunning()) {
                        mAnimator.end();
                        mAnimator = new AnimatorSet();
                        startAnimationRise(DURATION - playTime, DURATION / 2 - nightTime);
                    } else {
                        mAnimator = new AnimatorSet();
                        startAnimationRise(playTime, nightTime);
                    }
                }
            }
        });

        return view;
    }

    private void startAnimationSet(long playTime, long nightTime) {

        mSunSet = false;

        float sunYStart = mObjects.getSunStart();
        float sunYEnd = mObjects.getSunEnd();
        float ringYStart = mObjects.getRingStart();
        float ringYEnd = mObjects.getRingEnd();
        float reflectYStart = mObjects.getReflectStart();
        float reflectYEnd = mObjects.getReflectEnd();

        mSunAnimator = setSunAnimation(DURATION, sunYStart, sunYEnd);
        mRingAnimator = setRingAnimation(DURATION, ringYStart, ringYEnd);
        mSkyAnimator = setSkyAnimator(DURATION, mBlueSkyColor, mSunsetSkyColor);
        mNightAnimator = setNightAnimator(DURATION / 2,
                mSunsetSkyColor, mNightSkyColor);
        mReflectionAnimator = setReflectionAnimator(DURATION,
                reflectYStart, reflectYEnd);

        if (playTime + nightTime > DURATION) {
            mAnimator.play(mNightAnimator);
        } else {
            mAnimator.play(mSunAnimator)
                    .with(mRingAnimator)
                    .with(mSkyAnimator)
                    .with(mReflectionAnimator)
            .before(mNightAnimator);
        }
        mAnimator.start();
        mSunAnimator.setCurrentPlayTime(playTime);
        mRingAnimator.setCurrentPlayTime(playTime);
        mSkyAnimator.setCurrentPlayTime(playTime);
        mNightAnimator.setCurrentPlayTime(nightTime);
        mReflectionAnimator.setCurrentPlayTime(playTime);
    }

    private void startAnimationRise(long playTime, long nightTime) {

        mSunSet = true;

        float sunYStart = mObjects.getSunStart();
        float sunYEnd = mObjects.getSunEnd();
        float ringYStart = mObjects.getRingStart();
        float ringYEnd = mObjects.getRingEnd();
        float reflectYStart = mObjects.getReflectStart();
        float reflectYEnd = mObjects.getReflectEnd();

        mSunAnimator = setSunAnimation(DURATION, sunYEnd, sunYStart);
        mRingAnimator = setRingAnimation(DURATION, ringYEnd, ringYStart);
        mSkyAnimator = setSkyAnimator(DURATION, mSunsetSkyColor, mBlueSkyColor);
        mNightAnimator = setNightAnimator(DURATION / 2,
                mNightSkyColor, mSunsetSkyColor);
        mReflectionAnimator = setReflectionAnimator(DURATION,
                reflectYEnd, reflectYStart);

        if (playTime + nightTime > DURATION/2) {
            mAnimator.play(mSunAnimator)
                    .with(mRingAnimator)
                    .with(mSkyAnimator)
                    .with(mReflectionAnimator);
        } else {
            mAnimator.play(mSunAnimator)
                    .with(mRingAnimator)
                    .with(mSkyAnimator)
                    .with(mReflectionAnimator)
                    .after(mNightAnimator);
        }
        mAnimator.start();
        mSunAnimator.setCurrentPlayTime(playTime);
        mRingAnimator.setCurrentPlayTime(playTime);
        mSkyAnimator.setCurrentPlayTime(playTime);
        mNightAnimator.setCurrentPlayTime(nightTime);
        mReflectionAnimator.setCurrentPlayTime(playTime);
    }

    private ObjectAnimator setSunAnimation(long duration, float yStart, float yEnd) {
        ObjectAnimator heightAnimator = ObjectAnimator
                .ofFloat(mSunView, "y", yStart, yEnd)
                .setDuration(duration);

        //heightAnimator.setInterpolator(new AccelerateInterpolator());

        return heightAnimator;
    }

    private ObjectAnimator setRingAnimation(long duration, float yStart, float yEnd) {
        ObjectAnimator ringAnimator = ObjectAnimator
                .ofFloat(mRingView, "y", yStart, yEnd)
                .setDuration(duration);

        //ringAnimator.setInterpolator(new AccelerateInterpolator());

        return ringAnimator;
    }

    private ObjectAnimator setSkyAnimator(long duration, int initialColor, int finalColor) {
        ObjectAnimator skyAnimator = ObjectAnimator
                .ofInt(mSkyView, "backgroundColor", initialColor, finalColor)
                .setDuration(duration);

        skyAnimator.setEvaluator(new ArgbEvaluator());
        return skyAnimator;
    }

    private ObjectAnimator setNightAnimator(long duration, int initialColor, int finalColor) {
        ObjectAnimator nightAnimator = ObjectAnimator
                .ofInt(mSkyView, "backgroundColor", initialColor, finalColor)
                .setDuration(duration);
        nightAnimator.setEvaluator(new ArgbEvaluator());

        return nightAnimator;
    }

    private ObjectAnimator setReflectionAnimator(long duration, float yStart, float yEnd) {
        ObjectAnimator reflectionAnimator = ObjectAnimator
                .ofFloat(mReflectionView, "y", yStart, yEnd)
                .setDuration(duration);
        //reflectionAnimator.setInterpolator(new AccelerateInterpolator());

        return reflectionAnimator;
    }
}
