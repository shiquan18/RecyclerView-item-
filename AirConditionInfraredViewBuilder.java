package com.judianb5.builder;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.judian.fastjson.JSON;
import com.judian.jdresourcelib.utils.Logg;
import com.judian.jdsmart.common.JdSmartServiceProxy;
import com.judian.jdsmart.common.entity.JdSmartCtrlCmd;
import com.judian.jdsmart.common.entity.JdSmartDevice;
import com.judian.jdsmart.common.entity.JdSmartDeviceOrder;
import com.judian.jdsmart.common.entity.JdSmartIRConstant;
import com.judian.support.jdbase.JdbaseCallback;
import com.judian.support.jdbase.JdbaseContant;
import com.judianb5.R;
import com.judianb5.adapter.ControlAdapter;
import com.judianb5.base.InfraredControlViewBuilder;
import com.judianb5.ui.TitleBar;
import com.judianb5.utils.T;

import java.util.HashMap;

import static com.judianb5.R.id.kai;
import static com.tencent.bugly.crashreport.crash.c.k;

/**
 * Created by house on 16/7/6.
 */
public class AirConditionInfraredViewBuilder extends InfraredControlViewBuilder {
    private int mTmpTemprature = 23;
    private TitleBar mtoobar;
    private GridView mGridView;
    private int mTemprature = 23;
    private JdSmartDevice mJdsmartDevice;
    private HashMap<String, Integer> keyLists = new HashMap<>();

    public AirConditionInfraredViewBuilder(Context cxt) {
        super(cxt);
    }

    @Override
    public void build(View rootView) {
        mtoobar = (TitleBar) rootView.findViewById(R.id.title_bar);
        mGridView = (GridView) rootView.findViewById(R.id.gv_infare);
        mtoobar.setRightImageViewVisibleOrGone();
        mtoobar.setRightTVVisibleOrGone();
        mtoobar.setRightTextView("23℃");
        mtoobar.setLeftTextView("空调控制");
        mtoobar.setTooBarOnClickListener(new TitleBar.TooBarOnClickListener() {
            @Override
            public void setRightImageViewOnClickListener() {

            }

            @Override
            public void setLeftImageViewOnClickListener() {
                ((Activity) mContext).onBackPressed();
                ((Activity) mContext).overridePendingTransition(R.anim.push_rigth_in, R.anim.push_rigth_out);

            }

            @Override
            public void setRLayoutOnClickListener() {

            }
        });
        mGridView.setAdapter(new ControlAdapter(mContext));
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0://开
                        switchOn();
                        break;
                    case 1://关
                        switchOff();
                        break;
                    case 2://加
                        setTemprature(true);
                        break;
                    case 3://减
                        setTemprature(false);
                        break;
                    case 4://制冷
                        switchMode(JdSmartDeviceOrder.AIRCONDITION_MODE_TYPE, JdSmartDeviceOrder.AIRCONDITION_MODE_COOL);
                        break;
                    case 5://制热
                        switchMode(JdSmartDeviceOrder.AIRCONDITION_MODE_TYPE, JdSmartDeviceOrder.AIRCONDITION_MODE_HEAT);
                        break;
                    case 6://抽湿
                        switchMode(JdSmartDeviceOrder.AIRCONDITION_MODE_TYPE, JdSmartDeviceOrder.AIRCONDITION_MODE_DEHUMIDIFY);
                        break;
                    case 7://循环
//                        switchMode();
                        break;
                    case 8://自动摆风
                        switchMode(JdSmartDeviceOrder.AIRCONDITION_WIND_DIRECTION_TYPE, JdSmartDeviceOrder.AIRCONDITION_WIND_DIRECTION_NO_DIRECTION);
                        break;
                    case 9://停止摆风
                        switchMode(JdSmartDeviceOrder.AIRCONDITION_WIND_RATE_TYPE, JdSmartDeviceOrder.AIRCONDITION_WIND_RATE_MUTE);
                        break;
                    case 10://低速
                        switchMode(JdSmartDeviceOrder.AIRCONDITION_WIND_RATE_TYPE, JdSmartDeviceOrder.AIRCONDITION_WIND_RATE_LOW);
                        break;
                    case 11://中速
                        switchMode(JdSmartDeviceOrder.AIRCONDITION_WIND_RATE_TYPE, JdSmartDeviceOrder.AIRCONDITION_WIND_RATE_MIDDLE);
                        break;
                    case 12://高速
                        switchMode(JdSmartDeviceOrder.AIRCONDITION_WIND_RATE_TYPE, JdSmartDeviceOrder.AIRCONDITION_WIND_RATE_HIGH);
                        break;
                }
            }
        });
        initKeyList();
    }

    private void initKeyList() {
        keyLists.put(JdSmartIRConstant.IR_KEY_AIR_CONDITION_ON, 0);
        keyLists.put(JdSmartIRConstant.IR_KEY_AIR_CONDITION_OFF, 1);
        keyLists.put(JdSmartIRConstant.IR_KEY_AIR_CONDITION_MODE_COOL, 4);
        keyLists.put(JdSmartIRConstant.IR_KEY_AIR_CONDITION_MODE_HEAT, 5);
        keyLists.put(JdSmartIRConstant.IR_KEY_AIR_CONDITION_MODE_DEHUMIDIFY, 6);
        keyLists.put(JdSmartIRConstant.IR_KEY_AIR_CONDITION_MODE_WIND, 8);
        keyLists.put(JdSmartIRConstant.IR_KEY_AIR_CONDITION_WIND_RATE_LOW, 9);
        keyLists.put(JdSmartIRConstant.IR_KEY_AIR_CONDITION_WIND_RATE_MIDDLE, 10);
        keyLists.put(JdSmartIRConstant.IR_KEY_AIR_CONDITION_WIND_RATE_HIGH, 11);
        keyLists.put(JdSmartIRConstant.IR_KEY_AIR_CONDITION_WIND_DIRECTION_MUTE_DIRECTION, 8);
    }


    //模式切换
    private void switchMode(String modeType, String mode) {
        final JdSmartCtrlCmd cmd = mJdsmartDevice.getJdSmartCtrlCmd();
        if (cmd == null) return;
        cmd.setOrder(JdSmartDeviceOrder.SET);
        cmd.setValue1(modeType);
        cmd.setValue2(mode);
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

    //温度控制
    private void setTemprature(boolean up) {
        final JdSmartCtrlCmd cmd = mJdsmartDevice.getJdSmartCtrlCmd();
        if (cmd == null) return;
        cmd.setOrder(JdSmartDeviceOrder.MOVE_TO_LEVEL);
        mTmpTemprature = mTemprature;
        mTemprature = (mTemprature + (up ? 1 : -1));
        cmd.setValue1(mTemprature + "");
        mtoobar.setRightTextView(mTemprature + "℃");
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
                            mtoobar.setRightTextView(mTmpTemprature + "℃");
                            Toast.makeText(getContext(), String.format("控制失败！（%d）", code), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void setDeviceInfo(JdSmartDevice jdsmartDevice) {
        mJdsmartDevice = jdsmartDevice;
        Logg.d("AirConditionControllerViewBuilder jdsmartDevice:" + jdsmartDevice);
        if (mJdsmartDevice != null) {
            refresh();
        }
    }

    private void refresh() {
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                //红外空调和wifi
                mtoobar.setRightTextView(mTemprature + "℃");
                //判断红外对应按键是否学习
                String keyList = mJdsmartDevice.getJdIRkeyList();
                if (!TextUtils.isEmpty(keyList)) {
                    String[] keys = keyList.split(",");
                    for (String key : keys) {
                        if (keyLists.containsKey(key)) {
                            mGridView.setSelection(keyLists.get(key));
                            mGridView.setEnabled(true);
//                                    setEnabled(true);
                            keyLists.remove(key);
                        }
                    }

                    if (keyLists.size() > 0) {
                        for (String key : keyLists.keySet()) {
                            mGridView.setSelection(keyLists.get(key));
//                            keyLists.get(key).setEnabled(false);
                            mGridView.setEnabled(true);
                        }
                    }
                }
            }
        });
    }

    //红外控制
    //开关控制
    private void switchOn() {
        final JdSmartCtrlCmd cmd = mJdsmartDevice.getJdSmartCtrlCmd();
        if (cmd == null) return;
        cmd.setOrder(JdSmartDeviceOrder.OPEN);
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
    private void switchOff() {
        final JdSmartCtrlCmd cmd = mJdsmartDevice.getJdSmartCtrlCmd();
        if (cmd == null) return;
        cmd.setOrder(JdSmartDeviceOrder.CLOSE);
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


}
