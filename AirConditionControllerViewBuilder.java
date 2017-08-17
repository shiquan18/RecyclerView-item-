package com.judianb5.builder;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.judian.fastjson.JSON;
import com.judian.jdresourcelib.utils.Logg;
import com.judian.jdsmart.common.entity.JdSmartCtrlCmd;
import com.judian.jdsmart.common.entity.JdSmartDevice;
import com.judian.jdsmart.common.entity.JdSmartDeviceType;
import com.judianb5.R;
import com.judianb5.activity.InfraredCtrlActivity;

import static com.judianb5.R.drawable.bfive_selector_aricondition_src;


/**
 * Created by house on 16/7/6.
 */
public class AirConditionControllerViewBuilder extends DeviceControlViewBuilder {
    private TextView mName;
    private ImageView mDeviceImg;
    private JdSmartDevice mJdsmartDevice;

    public AirConditionControllerViewBuilder(Context cxt) {
        super(cxt);
    }

    @Override
    public void build(View rootView) {
        super.build(rootView);
        mName = (TextView) rootView.findViewById(R.id.name);
        mDeviceImg = (ImageView) rootView.findViewById(R.id.device_img);
        mDeviceImg.setImageResource(R.drawable.bfive_selector_aricondition_src);
    }


    @Override
    public void setDeviceInfo(JdSmartDevice jdsmartDevice) {
        mJdsmartDevice = jdsmartDevice;
        Logg.d("AirConditionControllerViewBuilder jdsmartDevice:" + jdsmartDevice);
        refresh();
    }

    private void refresh() {
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                if (mJdsmartDevice == null) return;
                try {
                    JdSmartCtrlCmd jdSmartCtrlCmd = mJdsmartDevice.getJdSmartCtrlCmd();
                    if (jdSmartCtrlCmd == null) return;
                    mName.setText(mJdsmartDevice.getJdDeviceName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getDeviceType() {
        return Integer.parseInt(mJdsmartDevice.getDeviceType());
    }

    @Override
    public void toEdit() {
        int deviceType = getDeviceType();
        if (deviceType == JdSmartDeviceType.DEVICE_TYPE_AIRCONDITION) {
            Intent intent = new Intent(mContext, InfraredCtrlActivity.class);
            intent.putExtra("jdSmartDevice", JSON.toJSONString(mJdsmartDevice));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }
    }

    @Override
    public void toOn() {

    }

    @Override
    public void toOff() {

    }

    @Override
    public void toPause() {

    }

}
