package com.kiminonawa.mydiary.entries.photo;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.samples.zoomable.DoubleTapGestureListener;
import com.facebook.samples.zoomable.ZoomableDraweeView;
import com.kiminonawa.mydiary.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by daxia on 2017/4/12.
 */

public class PhotoDetailViewerFragment extends DialogFragment {

    /**
     * The UI
     */
    @BindView(R.id.zdv_photo_detail)
    ZoomableDraweeView zdvPhotoDetail;
    Unbinder unbinder;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.TransparentDialog);


        return dialog;
    }


    @Override
    public void onStart() {
        super.onStart();
//        ScreenHelper.openInmmersiveMode(getDialog().getWindow().getDecorView());
        Dialog d = getDialog();
        if (d != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_fragment_diary_photo_detail_viewer, container, false);
        unbinder = ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initZoomableDraweeView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void initZoomableDraweeView() {
        zdvPhotoDetail.setAllowTouchInterceptionWhileZoomed(true);
        // needed for double tap to zoom
        zdvPhotoDetail.setIsLongpressEnabled(false);
        zdvPhotoDetail.setTapListener(new DoubleTapGestureListener(zdvPhotoDetail));
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri("https://www.gstatic.com/webp/gallery/1.sm.jpg")
                .build();
        zdvPhotoDetail.setController(controller);
    }


}