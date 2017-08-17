package com.judianb5.builder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.judian.fastjson.JSON;
import com.judian.jdresourcelib.utils.Logg;
import com.judian.jdsmart.common.JdSmartServiceProxy;
import com.judian.jdsmart.common.entity.JdSmartCtrlCmd;
import com.judian.jdsmart.common.entity.JdSmartDevice;
import com.judian.jdsmart.common.entity.JdSmartDeviceOrder;
import com.judian.jdsmart.common.entity.JdSmartDeviceType;
import com.judian.support.jdbase.JdbaseCallback;
import com.judian.support.jdbase.JdbaseContant;
import com.judianb5.R;

import solid.ren.skinlibrary.loader.SkinManager;


/**
 * Created by randy on 16/7/6.
 * TV and TV-Box
 */
public class TVorBoxControllerViewBuilder extends DeviceControlViewBuilder {
    private TextView mName;
    private ImageView mImage;
    private JdSmartDevice mJdDevice;

    public TVorBoxControllerViewBuilder(Context cxt) {
        super(cxt);
    }

    @Override
    public void build(View rootView) {
        super.build(rootView);
        mName = (TextView) rootView.findViewById(R.id.name);
        mImage = (ImageView) rootView.findViewById(R.id.device_img);
        mImage.setImageResource(R.drawable.bfive_selector_roomdevice_socket_src);
        tv_zanting.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setDeviceInfo(JdSmartDevice jdsmartDevice) {
        mJdDevice = jdsmartDevice;
        Logg.d("refreshView>>>" + jdsmartDevice.toString());
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                refreshView();
            }
        });
    }

    private void refreshView() {
        if (mJdDevice == null) {
            return;
        }
        mName.setText(mJdDevice.getJdDeviceName());
        int deviceType = Integer.valueOf(mJdDevice.getDeviceType());
        if (deviceType == JdSmartDeviceType.DEVICE_TYPE_TV) {
            mImage.setImageDrawable(SkinManager.getInstance().getDrawable(R.drawable.bfive_selector_roomdevice_tv_src));
        } else {
            mImage.setImageDrawable(SkinManager.getInstance().getDrawable(R.drawable.bfive_selector_roomdevice_stp_src));
        }
    }

    @Override
    public int getDeviceType() {
        return Integer.parseInt(mJdDevice.getDeviceType());
    }

    @Override
    public void toOn() {
        toggle(true);
    }

    @Override
    public void toOff() {
        toggle(false);
    }

    @Override
    public void toPause() {

    }

    private void toggle(final boolean open) {
        if (mJdDevice == null) {
            Toast.makeText(getContext(), "没有找到此设备！", Toast.LENGTH_SHORT).show();
            return;
        }
        final JdSmartCtrlCmd cmd = mJdDevice.getJdSmartCtrlCmd();
        if (cmd == null) return;
        cmd.setOrder(open ? JdSmartDeviceOrder.ON : JdSmartDeviceOrder.OFF);
        Logg.d("control>>>" + cmd.toString());
        JdSmartServiceProxy.getInstance().controlDevice(JSON.toJSONString(cmd), new JdbaseCallback() {
            @Override
            public void onResult(final int code, String s, String s1) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (code == JdbaseContant.RESULT_SUCCESS) {
                            mImage.setSelected(open);
                        } else {
                            Toast.makeText(getContext(), String.format("控制失败！（%d）", code), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
