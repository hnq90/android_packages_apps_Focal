package org.cyanogenmod.nemesis.widgets;

import android.hardware.Camera;
import android.view.View;

import org.cyanogenmod.nemesis.CameraActivity;
import org.cyanogenmod.nemesis.R;
import org.cyanogenmod.nemesis.feats.BurstCapture;

/**
 * Burst-shooting mode widget
 */
public class BurstModeWidget extends WidgetBase implements View.OnClickListener {
    private WidgetOptionButton mBtnOff;
    private WidgetOptionButton mBtn5;
    private WidgetOptionButton mBtn10;
    private WidgetOptionButton mBtn15;
    private WidgetOptionButton mBtnInf;
    private WidgetOptionButton mPreviousMode;
    private CameraActivity mCameraActivity;
    private BurstCapture mTransformer;

    public BurstModeWidget(CameraActivity activity) {
        super(activity.getCamManager(), activity, R.drawable.ic_widget_burst);

        mCameraActivity = activity;

        // Create options
        // XXX: Move that into an XML
        mBtnOff = new WidgetOptionButton(R.drawable.ic_widget_burst_off, activity);
        mBtn5 = new WidgetOptionButton(R.drawable.ic_widget_burst_5, activity);
        mBtn10 = new WidgetOptionButton(R.drawable.ic_widget_burst_10, activity);
        mBtn15 = new WidgetOptionButton(R.drawable.ic_widget_burst_15, activity);
        mBtnInf = new WidgetOptionButton(R.drawable.ic_widget_burst_inf, activity);

        addViewToContainer(mBtnOff);
        addViewToContainer(mBtn5);
        addViewToContainer(mBtn10);
        addViewToContainer(mBtn15);
        addViewToContainer(mBtnInf);

        mBtnOff.setOnClickListener(this);
        mBtn5.setOnClickListener(this);
        mBtn10.setOnClickListener(this);
        mBtn15.setOnClickListener(this);
        mBtnInf.setOnClickListener(this);

        mPreviousMode = mBtnOff;
        mPreviousMode.setActiveDrawable("nemesis-burst-mode=off");

        mTransformer = new BurstCapture(activity);
    }

    @Override
    public boolean isSupported(Camera.Parameters params) {
        // Burst mode is supported by everything. If we are in photo mode thatis.
        if (CameraActivity.getCameraMode() == CameraActivity.CAMERA_MODE_PHOTO) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onClick(View view) {
        mPreviousMode.resetImage();

        if (view == mBtnOff) {
            // Disable the transformer
            mCameraActivity.setCaptureTransformer(null);
            mBtnOff.setActiveDrawable("nemesis-burst-mode=off");
            mPreviousMode = mBtnOff;
        } else if (view == mBtn5) {
            mTransformer.setBurstCount(5);
            mCameraActivity.setCaptureTransformer(mTransformer);
            mBtn5.setActiveDrawable("nemesis-burst-mode=5");
            mPreviousMode = mBtn5;
        } else if (view == mBtn10) {
            mTransformer.setBurstCount(10);
            mCameraActivity.setCaptureTransformer(mTransformer);
            mBtn10.setActiveDrawable("nemesis-burst-mode=10");
            mPreviousMode = mBtn10;
        } else if (view == mBtn15) {
            mTransformer.setBurstCount(15);
            mCameraActivity.setCaptureTransformer(mTransformer);
            mBtn15.setActiveDrawable("nemesis-burst-mode=15");
            mPreviousMode = mBtn15;
        } else if (view == mBtnInf) {
            // Infinite burst count
            mTransformer.setBurstCount(0);
            mCameraActivity.setCaptureTransformer(mTransformer);
            mBtnInf.setActiveDrawable("nemesis-burst-mode=inf");
            mPreviousMode = mBtnInf;
        }
    }
}
