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
 * Created by house on 16/7/6.
 */

public class SmartBoxContollerViewBuilder extends DeviceControlViewBuilder {

    private JdSmartDevice mJdSmartDevice;
    private TextView mName;
    private ImageView mImage;
    private int mDeviceType;

    public SmartBoxContollerViewBuilder(Context cxt) {
        super(cxt);
    }

    @Override
    public void build(View rootView) {
        super.build(rootView);
        mName = (TextView) rootView.findViewById(R.id.name);
        mImage = (ImageView) rootView.findViewById(R.id.device_img);
        mImage.setImageResource(R.drawable.bfive_selector_roomdevice_lcurtains_src);
    }

    private void refreshView() {
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                Logg.d("refreshView>>>" + mJdSmartDevice);
                if (mJdSmartDevice == null) return;
                if (mDeviceType == JdSmartDeviceType.DEVICE_TYPE_CURTAINS_NO_POSITION
                        || mDeviceType == JdSmartDeviceType.DEVICE_TYPE_CURTAINS) {
                    mImage.setImageDrawable(SkinManager.getInstance().getDrawable(R.drawable.bfive_selector_roomdevice_lcurtains_src));
                } else {
                    mImage.setImageDrawable(SkinManager.getInstance().getDrawable(R.drawable.bfive_selector_roomdevice_controlbox_src));
                }
                try {
                    JdSmartCtrlCmd jdSmartCtrlCmd = mJdSmartDevice.getJdSmartCtrlCmd();
                    if (jdSmartCtrlCmd == null) return;
                    mName.setText(mJdSmartDevice.getJdDeviceName());
                    if ("100".equals(jdSmartCtrlCmd.getValue1())) {
                        tv_kai.setEnabled(false);
                        tv_guan.setEnabled(true);
                    } else if ("0".equals(jdSmartCtrlCmd.getValue1())) {
                        tv_guan.setEnabled(false);
                        tv_kai.setEnabled(true);
                    } else {
                        tv_guan.setEnabled(true);
                        tv_kai.setEnabled(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public void setDeviceInfo(JdSmartDevice jdsmartDevice) {
        mJdSmartDevice = jdsmartDevice;
        mDeviceType = Integer.valueOf(mJdSmartDevice.getDeviceType());
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                refreshView();
            }
        });
    }

    @Override
    public int getDeviceType() {
        return mDeviceType;
    }

    @Override
    public void toOn() {
        control(0);
        tv_kai.setEnabled(false);
        tv_guan.setEnabled(true);
        mImage.setSelected(true);
    }

    @Override
    public void toOff() {
        control(100);
        tv_guan.setEnabled(false);
        tv_kai.setEnabled(true);
        mImage.setSelected(false);
    }

    @Override
    public void toPause() {
        ctrlPasue();
        tv_kai.setEnabled(true);
        tv_guan.setEnabled(true);
    }

    private void control(int closePer) {
        if (mJdSmartDevice == null) {
            Toast.makeText(getContext(), "没有找到此设备！", Toast.LENGTH_SHORT).show();
            return;
        }
        final JdSmartCtrlCmd cmd = mJdSmartDevice.getJdSmartCtrlCmd();
        if (cmd == null) return;
        int orderPer = 0;
        if (closePer > 0 && closePer < 100) {
            cmd.setOrder(JdSmartDeviceOrder.CLOSE);
            orderPer = 100 - closePer;
        } else {
            if (closePer == 0) {
                cmd.setOrder(JdSmartDeviceOrder.OPEN);
                orderPer = 100;
            } else {
                cmd.setOrder(JdSmartDeviceOrder.CLOSE);
                orderPer = 0;
            }
        }
        cmd.setValue1(orderPer + "");
        Logg.d("control>>>" + cmd.toString());
        JdSmartServiceProxy.getInstance().controlDevice(JSON.toJSONString(cmd), new JdbaseCallback() {
            @Override
            public void onResult(final int code, String s, String s1) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (code == JdbaseContant.RESULT_SUCCESS) {

                        } else {
                            Toast.makeText(getContext(), String.format("控制失败！（%d）", code), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    //开关控制
    private void ctrlPasue() {
        if (mJdSmartDevice == null) return;
        final JdSmartCtrlCmd cmd = mJdSmartDevice.getJdSmartCtrlCmd();
        if (cmd == null) return;
        cmd.setOrder(JdSmartDeviceOrder.STOP);
        Logg.d("control>>>" + cmd.toString());
        JdSmartServiceProxy.getInstance().controlDevice(JSON.toJSONString(cmd), new JdbaseCallback() {
            @Override
            public void onResult(final int code, String s, String s1) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (code == JdbaseContant.RESULT_SUCCESS) {
                            Logg.d("ctrlPasue>>> success");
                        } else {
                            Toast.makeText(getContext(), String.format("控制失败！（%d）", code), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
