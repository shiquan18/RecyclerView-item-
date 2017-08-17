package com.judianb5.builder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.judian.jdresourcelib.utils.Logg;
import com.judian.jdsmart.common.entity.JdSmartCtrlCmd;
import com.judian.jdsmart.common.entity.JdSmartDevice;
import com.judian.jdsmart.common.entity.JdSmartDeviceType;
import com.judianb5.R;

import solid.ren.skinlibrary.loader.SkinManager;


/**
 * 湿度传感器
 * Created by house on 16/7/6.
 */
public class TempOrHumidirtyViewBuilder extends DeviceControlViewBuilder {

    private JdSmartDevice mJdsmartDevice;

    private TextView mName;

    private ImageView mImge;

    public TempOrHumidirtyViewBuilder(Context cxt) {
        super(cxt);
    }

    @Override
    public void build(View rootView) {
        super.build(rootView);
        mName = (TextView) rootView.findViewById(R.id.name);
        mImge = (ImageView) rootView.findViewById(R.id.device_img);
        mImge.setImageResource(R.drawable.bfive_selector_roomdevice_temperature_humidity_src);
        tv_kai.setVisibility(View.INVISIBLE);
        tv_guan.setVisibility(View.INVISIBLE);
        tv_connect.setVisibility(View.VISIBLE);

    }

    /**
     * 对于温湿度传感器：value1填写温度值，value2填写湿度值
     * 除以100等于实际值
     */
    private void refreshView() {
        if (mJdsmartDevice != null) {
            Logg.d(mJdsmartDevice.toString());
            JdSmartCtrlCmd cmd = mJdsmartDevice.getJdSmartCtrlCmd();
            if (cmd == null) return;
            if (mJdsmartDevice.getDeviceType().equals(JdSmartDeviceType.DEVICE_TYPE_HUMIDITY_SENSOR + "")) {
                String value2 = cmd.getValue2();
                float value = TextUtils.isEmpty(value2) ? 0 : Float.parseFloat(value2);
                tv_zanting.setText(R.string.smart_device_lable_humidity);
                tv_connect.setText(Math.round(value / 100f) + "%");
            } else {
                String value1 = cmd.getValue1();
                float value = TextUtils.isEmpty(value1) ? 0 : Float.parseFloat(value1);
                tv_zanting.setText(R.string.smart_device_lable_temp);
                tv_connect.setText(Math.round(value / 100f) + "℃");
            }
            mName.setText(mJdsmartDevice.getJdDeviceName());
        }

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
            String value2 = cmd.getValue2();
            switch (type) {
                case JdSmartDeviceType.DEVICE_TYPE_TEMP_HUMIDITY_SENSOR:
                    float tmp = TextUtils.isEmpty(value1) ? 0 : Float.parseFloat(value1);
                    tv_zanting.setText(R.string.smart_device_lable_temp);
                    tv_connect.setText(Math.round(tmp / 100f) + "℃");
                    float hum = TextUtils.isEmpty(value2) ? 0 : Float.parseFloat(value2);
                    tv_zanting.setText(R.string.smart_device_lable_humidity);
                    tv_connect.setText(Math.round(hum / 100f) + "%");
                    imageResouce = R.drawable.bfive_selector_roomdevice_smoke_sensor_src;
                    break;
                case JdSmartDeviceType.DEVICE_TYPE_CO2_SENSOR:
                    float co2 = TextUtils.isEmpty(value1) ? 0 : Float.parseFloat(value1);
                    tv_zanting.setText(R.string.smart_device_lable_co2);
                    tv_connect.setText(co2 + " PPM");
                    imageResouce = R.drawable.bfive_selector_roomdevice_temperature_humidity2_src;
                    break;
                case JdSmartDeviceType.DEVICE_TYPE_HUMIDITY_SENSOR:
                    float hum1 = TextUtils.isEmpty(value2) ? 0 : Float.parseFloat(value2);
                    tv_zanting.setText(R.string.smart_device_lable_humidity);
                    tv_connect.setText(Math.round(hum1 / 100f) + "%");
                    imageResouce = R.drawable.bfive_selector_roomdevice_temperature_humidity_src;
                    break;
                case JdSmartDeviceType.DEVICE_TYPE_TEMPERATURE_SENSOR:
                    float tmp1 = TextUtils.isEmpty(value1) ? 0 : Float.parseFloat(value1);
                    tv_zanting.setText(R.string.smart_device_lable_temp);
                    tv_connect.setText(Math.round(tmp1 / 100f) + "℃");
                    imageResouce = R.drawable.bfive_selector_roomdevice_temperature_humidity_src;
                    break;
                default:
                    imageResouce = R.drawable.bfive_selector_roomdevice_temperature_humidity_src;
                    break;
            }
            mImge.setImageDrawable(SkinManager.getInstance().getDrawable(imageResouce));
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
