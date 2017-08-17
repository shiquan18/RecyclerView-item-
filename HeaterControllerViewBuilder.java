package com.judianb5.builder;

import android.content.Context;
import android.text.TextUtils;
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
import com.judian.support.jdbase.JdbaseCallback;
import com.judian.support.jdbase.JdbaseContant;
import com.judianb5.R;

/**
 * Created by house on 16/7/6.
 */

public class HeaterControllerViewBuilder extends DeviceControlViewBuilder {

    private TextView mName;
    private ImageView mDeviceImg;
    private JdSmartDevice mJdsmartDevice;
    private int mTemprature = 26;

    public HeaterControllerViewBuilder(Context cxt) {
        super(cxt);
    }

    @Override
    public void build(View rootView) {
        super.build(rootView);
        mName = (TextView) rootView.findViewById(R.id.name);
        mDeviceImg = (ImageView) rootView.findViewById(R.id.device_img);
        mDeviceImg.setImageResource(R.drawable.bfive_selector_heater_src);
        rootView.findViewById(R.id.ll_more).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.jia).setOnClickListener(Listener);
        rootView.findViewById(R.id.jian).setOnClickListener(Listener);
        tv_guan.setVisibility(View.GONE);
        tv_kai.setVisibility(View.GONE);
        tv_zanting.setText(tv_zanting.isSelected() ? "开" : "关");
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
                    mTemprature = TextUtils.isEmpty(jdSmartCtrlCmd.getValue1()) ? mTemprature : Integer.parseInt(jdSmartCtrlCmd.getValue1());
                    if (jdSmartCtrlCmd.getOrder().equals(JdSmartDeviceOrder.OPEN) || jdSmartCtrlCmd.getOrder().equals(JdSmartDeviceOrder.CLOSE)) {
                        tv_zanting.setSelected(jdSmartCtrlCmd.getOrder().equals(JdSmartDeviceOrder.OPEN));
                        tv_zanting.setText(tv_zanting.isSelected() ? "开" : "关");
                    }
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
    public void toOn() {
    }

    @Override
    public void toOff() {
    }

    @Override
    public void toPause() {
        switchStatus();
    }

    View.OnClickListener Listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.jia:
                    setTemprature(true);
                    break;
                case R.id.jian:
                    setTemprature(false);
                    break;
            }
        }
    };

    //开关控制
    private void switchStatus() {
        if (mJdsmartDevice == null) return;
        final JdSmartCtrlCmd cmd = mJdsmartDevice.getJdSmartCtrlCmd();
        if (cmd == null) return;
        cmd.setOrder(!tv_zanting.isSelected() ? JdSmartDeviceOrder.OPEN : JdSmartDeviceOrder.CLOSE);
        Logg.d("control>>>" + cmd.toString());
        JdSmartServiceProxy.getInstance().controlDevice(JSON.toJSONString(cmd), new JdbaseCallback() {
            @Override
            public void onResult(final int code, String s, String s1) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (code == JdbaseContant.RESULT_SUCCESS) {
                            tv_zanting.setSelected(!tv_zanting.isSelected());
                            tv_zanting.setText(tv_zanting.isSelected() ? "开" : "关");
                        } else {
                            Toast.makeText(getContext(), String.format("控制失败！（%d）", code), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    //温度控制
    private void setTemprature(boolean up) {
        if (mJdsmartDevice == null) return;
        final JdSmartCtrlCmd cmd = mJdsmartDevice.getJdSmartCtrlCmd();
        if (cmd == null) return;
        cmd.setOrder(JdSmartDeviceOrder.MOVE_TO_LEVEL);
        cmd.setValue1((mTemprature + (up ? 1 : -1)) + "");
        Logg.d("control>>>" + cmd.toString());
        JdSmartServiceProxy.getInstance().controlDevice(JSON.toJSONString(cmd), new JdbaseCallback() {
            @Override
            public void onResult(final int code, String s, String s1) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (code == JdbaseContant.RESULT_SUCCESS) {
                            Logg.d("switchMode>>> success");
                        } else {
                            Toast.makeText(getContext(), String.format("控制失败！（%d）", code), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

}
