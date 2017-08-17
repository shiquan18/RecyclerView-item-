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
public class LampControllerViewBuilder extends DeviceControlViewBuilder {

    private JdSmartDevice mJdDevice;
    private TextView mName;
    private ImageView mdeviceImg;
    private TextView mJia;
    private TextView mJian;
    private final float Max = 255f;

    public LampControllerViewBuilder(Context cxt) {
        super(cxt);
    }

    @Override
    public void build(View rootView) {
        super.build(rootView);
        mName = (TextView) rootView.findViewById(R.id.name);
        mdeviceImg = (ImageView) rootView.findViewById(R.id.device_img);
        mdeviceImg.setImageResource(R.drawable.bfive_selector_roomdevice_lamp_src);
        tv_kai.setVisibility(View.INVISIBLE);
        tv_guan.setVisibility(View.INVISIBLE);
        rootView.findViewById(R.id.ll_more).setVisibility(View.VISIBLE);
        mJia = (TextView) rootView.findViewById(R.id.jia);
        mJia.setOnClickListener(Listener);
        mJian = (TextView) rootView.findViewById(R.id.jian);
        mJian.setOnClickListener(Listener);

    }

    View.OnClickListener Listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.jia:
                    increaseBright();
                    break;
                case R.id.jian:
                    reduceBright();
                    break;
            }
        }
    };

    private void refreshView() {
        if (mJdDevice == null) {
            return;
        }
        mName.setText(mJdDevice.getJdDeviceName());
        JdSmartCtrlCmd cmd = mJdDevice.getJdSmartCtrlCmd();
        if (cmd == null) return;
        Logg.d("refreshView>>cmd>>" + cmd.toString());
        try {
            //调光灯 （可调节亮度）
            if (Integer.parseInt(mJdDevice.getDeviceType()) == JdSmartDeviceType.DEVICE_TYPE_DIMMER) {
                mdeviceImg.setImageResource(R.drawable.bfive_selector_roomdevice_color_lamp_src);

                tv_zanting.setVisibility(View.VISIBLE);
                mJian.setVisibility(View.VISIBLE);
                mJia.setVisibility(View.VISIBLE);
                tv_guan.setVisibility(View.INVISIBLE);
                tv_kai.setVisibility(View.INVISIBLE);
                String value1 = cmd.getValue1();
                int order = TextUtils.isEmpty(value1) ? 0 : Integer.parseInt(value1);
                tv_zanting.setSelected(order == 0 ? true : false);
                tv_zanting.setText(tv_zanting.isSelected() ? "开" : "关");
//                mRootView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        toggle();
//                    }
//                });

                String value2 = cmd.getValue2();
                int bright = TextUtils.isEmpty(value2) ? 0 : Integer.parseInt(value2);
                if (bright > -1) {
                    mCurPercentage = bright;
                    int progress = Math.round((float) mCurPercentage / Max * 100);
                    if (progress == 0) {
                        mJian.setEnabled(true);
                        mJia.setEnabled(false);
                    } else if (progress == 100) {
                        mJian.setEnabled(false);
                        mJia.setEnabled(true);
                    } else {
                        mJian.setEnabled(true);
                        mJia.setEnabled(true);
                    }
                }
            } else {//正常灯（只支持开关）
                mJia.setVisibility(View.INVISIBLE);
                mJian.setVisibility(View.INVISIBLE);
                tv_zanting.setVisibility(View.INVISIBLE);

                if (mJdDevice.isNoReply()) {
                    mdeviceImg.setImageResource(R.drawable.bfive_selector_roomdevice_lamp_src);
                    tv_guan.setVisibility(View.VISIBLE);
                    tv_kai.setVisibility(View.VISIBLE);
                } else {
                    tv_guan.setVisibility(View.VISIBLE);
                    tv_kai.setVisibility(View.VISIBLE);
//                    mRootView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            toggle();
//                        }
//                    });
                    String value1 = cmd.getValue1();
                    int order = TextUtils.isEmpty(value1) ? 0 : Integer.parseInt(value1);
                    tv_zanting.setSelected(order == 0 ? true : false);
                    tv_zanting.setText(tv_zanting.isSelected() ? "开" : "关");
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
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
        toggle();
    }

    //开关
    private void toggle(final boolean open) {
        final JdSmartCtrlCmd cmd = mJdDevice.getJdSmartCtrlCmd();
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

    //有状态返回的状态切换
    private void toggle() {
        if (mJdDevice == null) {
            Toast.makeText(getContext(), "没有找到此设备！", Toast.LENGTH_SHORT).show();
            return;
        }
        final JdSmartCtrlCmd cmd = mJdDevice.getJdSmartCtrlCmd();
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

    private void reduceBright() {
        controlBright(mCurPercentage - (int) (Max / 10));
    }

    private void increaseBright() {
        controlBright(mCurPercentage + (int) (Max / 10));
    }

    private int mCurPercentage = 0;
    private int mTempBright = 0;

    private void controlBright(int per) {
        if (mJdDevice == null) {
            Toast.makeText(getContext(), "没有找到此设备！", Toast.LENGTH_SHORT).show();
            return;
        }

        if (per >= 255) {
            per = 255;
        } else if (per <= 0) {
            per = 0;
        }

        mTempBright = per;
        JdSmartCtrlCmd cmd = mJdDevice.getJdSmartCtrlCmd();
        if (cmd == null) return;
        cmd.setOrder(JdSmartDeviceOrder.MOVE_TO_LEVEL);
        cmd.setValue2(mTempBright + "");
        Logg.d("controlBright>>>cmd:" + cmd);
        JdSmartServiceProxy.getInstance().controlDevice(JSON.toJSONString(cmd), new JdbaseCallback() {
            @Override
            public void onResult(final int code, String s, String s1) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (code == JdbaseContant.RESULT_SUCCESS) {
                            mCurPercentage = mTempBright;
                        } else {
                            Toast.makeText(getContext(), String.format("控制失败！（%d）", code), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }


}
