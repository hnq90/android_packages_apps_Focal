/**
 * Copyright (C) 2013 The CyanogenMod Project
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.cyanogenmod.nemesis.widgets;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import org.cyanogenmod.nemesis.BitmapFilter;
import org.cyanogenmod.nemesis.CameraActivity;
import org.cyanogenmod.nemesis.CameraManager;
import org.cyanogenmod.nemesis.R;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Base for simple widgets simply changing a parameter
 * (eg. flash=on/off/auto, iso=100/200/300/500/800) from
 * a simple button.
 */
public class SimpleToggleWidget extends WidgetBase implements OnClickListener {
    public final static String TAG = "SimpleToggleWidget";

    private String mKey;
    private boolean mVideoOnly;

    private Map<WidgetBase.WidgetOptionButton, String> mButtonsValues;
    private Context mContext;
    private WidgetOptionButton mActiveButton;

    public SimpleToggleWidget(CameraManager cam, Context context, int sidebarIcon) {
        super(cam, context, sidebarIcon);

        mButtonsValues = new HashMap<WidgetBase.WidgetOptionButton, String>();
        mContext = context;
    }

    public SimpleToggleWidget(CameraManager cam, Context context, String key, int sidebarIcon) {
        super(cam, context, sidebarIcon);

        mButtonsValues = new HashMap<WidgetBase.WidgetOptionButton, String>();
        mContext = context;
        mKey = key;
    }

    /**
     * Defines or redefines the key used for the settings
     * @param key
     */
    protected void setKey(String key) {
        mKey = key;
    }

    /**
     * Sets whether or not this setting applies only in video mode
     * @param videoOnly
     */
    protected void setVideoOnly(boolean videoOnly) {
        mVideoOnly = videoOnly;
    }

    /**
     * Add a value that can be toggled to the widget
     * If the HAL reports a [key]-values array, it will check and filter
     * the values against this array. Otherwise, all the values are added
     * to the list.
     *
     * You might want to check for device-specific values that aren't
     * reported in -values in the child class before doing addValue
     *
     * @param value The value that the key of this widget can take
     * @param resId The icon that represents it
     */
    public void addValue(String value, int resId) {
        Camera.Parameters params = mCamManager.getParameters();

        String values = params.get(mKey + "-values");

        // If we don't have a -values provided, or if it contains the value, add it.
        if ((values == null && filterDeviceSpecific(value))
                || Arrays.asList(values.split(",")).contains(value)) {
            WidgetBase.WidgetOptionButton button = new WidgetBase.WidgetOptionButton(resId, mContext);
            button.setOnClickListener(this);
            mButtonsValues.put(button, value);
            addViewToContainer(button);

            String currentValue = params.get(mKey);
            if (currentValue != null && currentValue.equals(value)) {
                setButtonActivated(button, value);
            }
        } else {
            Log.w(TAG, "Device doesn't support " + value + " for setting " + mKey);
        }
    }

    /**
     * This method can be overriden by each widgets. If some keys doesn't
     * have a "-values" array, we can filter eventual device-specific incompatibilities
     * in this method.
     *
     * @param value The value tested for support
     * @return true if the value is supported, false if it's not
     */
    public boolean filterDeviceSpecific(String value) {
        return true;
    }

    /**
     * This method checks if the Key provided in the constructor
     * is supported (as per {@link WidgetBase} docs), by checking
     * if the key is in the Camera.Parameters array.
     */
    @Override
    public boolean isSupported(Camera.Parameters params) {
        if (CameraActivity.getCameraMode() != CameraActivity.CAMERA_MODE_VIDEO && mVideoOnly) {
            // This setting is only in video mode
            return false;
        }

        if (mButtonsValues.isEmpty() || mButtonsValues.size() == 1) {
            // There's only one setting, or it's empty, so not worth showing an icon/menu
            return false;
        }

        if (params.get(mKey) != null) {
            Log.v(TAG, "The device supports '" + mKey + "'");

            String values = params.get(mKey + "-values");
            if (values != null) {
                Log.v(TAG, "... and has these possible values: " + values);

                if (!values.contains(",")) {
                    // There is only one value, no need to show that button
                    return false;
                }
            } else {
                Log.w(TAG, "... but we don't know the possible values for " + mKey);
            }
            return true;
        } else {
            Log.d(TAG, "The device doesn't support '" + mKey + "'");
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        // We check what button was pressed, and enable it
        WidgetBase.WidgetOptionButton button = (WidgetBase.WidgetOptionButton) v;
        String value = mButtonsValues.get(button);

        if (value != null) {
            mCamManager.setParameterAsync(mKey, value);
            setButtonActivated(button, value);
        } else {
            Log.e(TAG, "Unknown value for this button!");
        }
    }

    private void setButtonActivated(WidgetOptionButton button, String value) {
        if (mActiveButton != null) {
            mActiveButton.resetImage();
        }

        mActiveButton = button;
        mActiveButton.setActiveDrawable(mKey + "=" + value);
    }
}
