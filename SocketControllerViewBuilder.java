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
import com.judian.jdsmart.common.entity.JdSmartDeviceType;
import com.judian.support.jdbase.JdbaseCallback;
import com.judian.support.jdbase.JdbaseContant;
import com.judianb5.R;

/**
 * Created by house on 16/7/6.
 */
public class SocketControllerViewBuilder extends DeviceControlViewBuilder {

    private JdSmartDevice mJdsmartDevice;
    private TextView mName;
    private ImageView mdeviceImg;

    public SocketControllerViewBuilder(Context cxt) {
        super(cxt);
    }

    @Override
    public void build(View rootView) {
        super.build(rootView);
        mName = (TextView) rootView.findViewById(R.id.name);
        mdeviceImg = (ImageView) rootView.findViewById(R.id.device_img);
        mdeviceImg.setImageResource(R.drawable.bfive_selector_roomdevice_socket_src);
    }

    private void refreshView() {
        if (mJdsmartDevice != null) {
            JdSmartCtrlCmd cmd = mJdsmartDevice.getJdSmartCtrlCmd();
            if (cmd == null) return;
            if (mJdsmartDevice.isNoReply()) {//有无状态返回
                tv_zanting.setVisibility(View.INVISIBLE);
                tv_guan.setVisibility(View.VISIBLE);
                tv_kai.setVisibility(View.VISIBLE);
            } else {
                tv_zanting.setVisibility(View.VISIBLE);
                tv_guan.setVisibility(View.INVISIBLE);
                tv_kai.setVisibility(View.INVISIBLE);
                String value1 = cmd.getValue1();
                int value = TextUtils.isEmpty(value1) ? 0 : Integer.parseInt(value1);
                tv_zanting.setSelected(value == 0 ? false : true);
                tv_zanting.setText(tv_zanting.isSelected() ? "开" : "关");
            }
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
        return JdSmartDeviceType.DEVICE_TYPE_SOCKECT;
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
        toggle();
    }

    //有状态返回的状态切换
    private void toggle() {
        if (mJdsmartDevice == null) {
            Toast.makeText(getContext(), "没有找到此设备！", Toast.LENGTH_SHORT).show();
            return;
        }
        final JdSmartCtrlCmd cmd = mJdsmartDevice.getJdSmartCtrlCmd();
        if (cmd == null) return;
        cmd.setOrder(!tv_zanting.isSelected() ? JdSmartDeviceOrder.ON : JdSmartDeviceOrder.OFF);
        Logg.d("toggle>>>" + cmd.toString());
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

    //开关
    private void toggle(final boolean open) {
        final JdSmartCtrlCmd cmd = mJdsmartDevice.getJdSmartCtrlCmd();
        if (cmd == null) return;
        cmd.setOrder(open ? JdSmartDeviceOrder.ON : JdSmartDeviceOrder.OFF);
        JdSmartServiceProxy.getInstance().controlDevice(JSON.toJSONString(cmd), new JdbaseCallback() {
            @Override
            public void onResult(final int code, String s, String s1) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        mdeviceImg.setSelected(open);
                    }
                });
            }
        });
    }
}
