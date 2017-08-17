package com.judianb5.builder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.judian.jdresourcelib.utils.Logg;
import com.judian.jdsmart.common.JdSmartServiceProxy;
import com.judian.jdsmart.common.entity.JdSmartCtrlCmd;
import com.judian.jdsmart.common.entity.JdSmartDevice;
import com.judian.jdsmart.common.entity.JdSmartDeviceType;
import com.judianb5.R;

import solid.ren.skinlibrary.loader.SkinManager;


/**
 * 安防大部分產品
 * Created by house on 16/7/6.
 */
public class SensorStandardViewBuilder extends DeviceControlViewBuilder {

    private JdSmartDevice mJdsmartDevice;

    private TextView mName;

    private ImageView mImageView;

    private final String VIEW_SENSOR_RECORD = "com.judian.jdsmart.sensor.record";

    public SensorStandardViewBuilder(Context cxt) {
        super(cxt);
    }

    @Override
    public void build(View rootView) {
        super.build(rootView);
        mName = (TextView) rootView.findViewById(R.id.name);
        mImageView = (ImageView) rootView.findViewById(R.id.device_img);
        mImageView.setImageResource(R.drawable.bfive_selector_roomdevice_magnetic_src);
        tv_guan.setVisibility(View.INVISIBLE);
        tv_kai.setVisibility(View.INVISIBLE);
        tv_zanting.setText("查看记录");
        tv_connect.setVisibility(View.VISIBLE);
        tv_zanting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mJdsmartDevice != null)
                    gotoSensorRecord(mJdsmartDevice.getVendor(), mJdsmartDevice.getDeviceId());
            }
        });

    }

    private void refreshView() {
        if (mJdsmartDevice != null) {
            Logg.d(mJdsmartDevice.toString());
            if (TextUtils.isEmpty(mJdsmartDevice.getDeviceType())) {
                Logg.e("deviceType is null");
                return;
            }
            int type = Integer.parseInt(mJdsmartDevice.getDeviceType());
            int imageResouce;
            JdSmartCtrlCmd cmd = mJdsmartDevice.getJdSmartCtrlCmd();
            if (cmd == null) return;
            String value1 = cmd.getValue1();
            int status = TextUtils.isEmpty(value1) ? 0 : Integer.parseInt(value1);
            switch (type) {
                //紧急按钮
                case JdSmartDeviceType.DEVICE_TYPE_SOS_SENSOR:
                    if (status == 0) {
                        tv_connect.setText(R.string.smart_device_status_narmal);
                    } else {
                        tv_connect.setText(R.string.smart_device_status_sos_warn);
                    }
                    imageResouce = R.drawable.bfive_selector_roomdevice_magnetic_src;
                    break;
                //燃气
                case JdSmartDeviceType.DEVICE_TYPE_FLAMMABLE_GAS:
                    if (status == 0) {
                        tv_connect.setText(R.string.smart_device_status_narmal);
                    } else {
                        tv_connect.setText(R.string.smart_device_status_sos_warn);
                    }
                    imageResouce = R.drawable.bfive_selector_roomdevice_smoke_sensor_src;
                    break;
                //烟雾传感器
                case JdSmartDeviceType.DEVICE_TYPE_SMOKER_SENSOR:
                    if (status == 0) {
                        tv_connect.setText(R.string.smart_device_status_narmal);
                    } else {
                        tv_connect.setText(R.string.smart_device_status_sos_warn);
                    }
                    imageResouce = R.drawable.bfive_selector_roomdevice_smoke_sensor_src;
                    break;
                //人体红外感应器
                case JdSmartDeviceType.DEVICE_TYPE_INFRARED_SENSOR:
                    if (status == 0) {
                        tv_connect.setText(R.string.smart_device_status_narmal);
                    } else {
                        tv_connect.setText(R.string.smart_device_status_people_across);
                    }
                    imageResouce = R.drawable.bfive_selector_roomdevice_infared_src;
                    break;
                //门窗磁性感应器
                case JdSmartDeviceType.DEVICE_TYPE_MAGNETIC_WINDOW:
                    if (status == 0) {
                        tv_connect.setText(R.string.smart_device_status_close);
                    } else {
                        tv_connect.setText(R.string.smart_device_status_open);
                    }
                    imageResouce = R.drawable.bfive_selector_roomdevice_infared_src;
                    break;
                default:
                    imageResouce = R.drawable.bfive_selector_roomdevice_magnetic_src;
                    break;

            }
            mImageView.setImageDrawable(SkinManager.getInstance().getDrawable(imageResouce));
            mName.setText(mJdsmartDevice.getJdDeviceName());
        }
    }

    @Override
    public void setDeviceInfo(JdSmartDevice jdsmartDevice) {
        mJdsmartDevice = jdsmartDevice;
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                refreshView();
            }
        });
    }

    @Override
    public int getDeviceType() {
        return Integer.parseInt(mJdsmartDevice.getDeviceType());
    }


    private void gotoSensorRecord(String vender, String deviceid) {
        JdSmartServiceProxy.getInstance().gotoSensorRecord(vender, deviceid);
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
