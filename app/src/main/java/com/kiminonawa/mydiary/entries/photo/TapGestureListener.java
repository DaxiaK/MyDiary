package com.kiminonawa.mydiary.entries.photo;

import android.view.MotionEvent;
import android.view.View;

import com.facebook.samples.zoomable.DoubleTapGestureListener;
import com.facebook.samples.zoomable.ZoomableDraweeView;
import com.kiminonawa.mydiary.shared.ScreenHelper;

/**
 * Created by daxia on 2017/4/20.
 */

public class TapGestureListener extends DoubleTapGestureListener {

    private View decorView;

    public TapGestureListener(View decorView, ZoomableDraweeView zoomableDraweeView) {
        super(zoomableDraweeView);
        this.decorView = decorView;
    }

    /**
     * Listen the single tap for control system UI
     *
     * @param e
     * @return
     */
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        boolean visible = (decorView.getSystemUiVisibility() &
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0;
        if (visible) {
            ScreenHelper.hideSystemUI(decorView);
        } else {
            ScreenHelper.showSystemUI(decorView);
        }
        return super.onSingleTapConfirmed(e);
    }
}
