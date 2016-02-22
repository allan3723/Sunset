package com.bignerdranch.android.sunset;

import android.graphics.Rect;
import android.view.View;

public class AnimationObjects {

    private View mSunView;
    private View mSkyView;
    private View mRingView;
    private View mReflectionView;
    private View mSeaView;

    private float mSunStart;
    private float mSunEnd;
    private float mRingStart;
    private float mRingEnd;
    private float mReflectStart;
    private float mReflectEnd;

    public AnimationObjects(View view) {
        mSunView = view.findViewById(R.id.sun);
        mSkyView = view.findViewById(R.id.sky);
        mRingView = view.findViewById(R.id.sun_ring);
        mReflectionView = view.findViewById(R.id.sun_reflection);
        mSeaView = view.findViewById(R.id.sea);
    }

    public void setObjectPositions(){
        float ringSunDiff = mSunView.getBottom()- mRingView.getBottom();

        mSunStart = mSunView.getTop();
        mSunEnd = mSkyView.getHeight() - ringSunDiff;
        mRingStart = mRingView.getTop();
        mRingEnd = mSkyView.getHeight();
        mReflectStart = mReflectionView.getTop();
        mReflectEnd = mReflectionView.getTop() - mSeaView.getHeight() - ringSunDiff;
    }

    public float getSunStart() {
        return mSunStart;
    }

    public float getSunEnd() {
        return mSunEnd;
    }

    public float getRingStart() {
        return mRingStart;
    }

    public float getRingEnd() {
        return mRingEnd;
    }

    public float getReflectStart() {
        return mReflectStart;
    }

    public float getReflectEnd() {
        return mReflectEnd;
    }
}
