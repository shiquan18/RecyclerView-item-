package com.judianb5.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.judian.fastjson.JSON;
import com.judian.jdresourcelib.utils.Logg;
import com.judian.jdresourcelib.widget.recyclerview.adapter.BaseAdapterHelper;
import com.judian.jdresourcelib.widget.recyclerview.adapter.BaseQuickAdapter;
import com.judian.jdresourcelib.widget.recyclerview.adapter.MultiItemTypeSupport;
import com.judian.jdsmart.common.JdSmartServiceProxy;
import com.judian.jdsmart.common.entity.JdSmartCtrlCmd;
import com.judian.jdsmart.common.entity.JdSmartDevice;
import com.judian.jdsmart.common.entity.JdSmartDeviceType;
import com.judian.jdsmart.common.entity.JdSmartHostInfo;
import com.judian.support.jdbase.JdbaseCallback;
import com.judian.support.jdbase.JdbaseContant;
import com.judianb5.R;
import com.judianb5.activity.InfraredCtrlActivity;
import com.judianb5.activity.RoomChooseActivity;
import com.judianb5.activity.SmartSettingvity;
import com.judianb5.base.BaseFragment;
import com.judianb5.builder.AirConditionControllerViewBuilder;
import com.judianb5.builder.CurtainContollerViewBuilder;
import com.judianb5.builder.DeviceControlViewBuilder;
import com.judianb5.builder.HeaterControllerViewBuilder;
import com.judianb5.builder.LampControllerViewBuilder;
import com.judianb5.builder.SensorStandardViewBuilder;
import com.judianb5.builder.SmartBoxContollerViewBuilder;
import com.judianb5.builder.SocketControllerViewBuilder;
import com.judianb5.builder.TVorBoxControllerViewBuilder;
import com.judianb5.builder.TempOrHumidirtyViewBuilder;
import com.judianb5.eventbus.CommandMessage;
import com.judianb5.manager.JdSmartLoadManager;
import com.judianb5.manager.MusicManager;
import com.judianb5.ui.TitleBar;
import com.judianb5.utils.SPUtils;

import java.util.ArrayList;
import java.util.List;

import solid.ren.skinlibrary.loader.SkinManager;

import static android.app.Activity.RESULT_OK;
import static com.judianb5.R.id.rl_control;

/**
 * Created by house on 16/7/6.
 */
public class SmartDeviceControlFragment extends BaseFragment {

    private RecyclerView mRecyclerView;
    private List<JdSmartDevice> mDatas = new ArrayList<>();
    private MyAdapter myAdapter;
    private TitleBar mTitleBar;
    private View mRootView;
    private View mLoadingView;
    private Button mSetting;
    JdSmartHostInfo mHostInfo;
    private String mfloorName;
    private String mroomName;

    private static final int TIME = 6000;
    DeviceControlViewBuilder sbuilder = null;
    DeviceControlViewBuilder builder = null;
    Handler handler = new Handler();
    private boolean mNeedVview = false;
    private int mStart = -1;
    private int mLast = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.bfive_fragment_smart_device, null);
        mTitleBar = (TitleBar) mRootView.findViewById(R.id.tb_smartdevice);
        mTitleBar.setRightImageViewVisibleOrGone();
        mTitleBar.setLeftImageviewRs(R.drawable.bfive_selector_cebianlan_src);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_view);
        mLoadingView = mRootView.findViewById(R.id.loading_view);
        Logg.d("mLoadingView>>" + mLoadingView);
        initView();
        addDevicePropertyChangeListener();
        return mRootView;
    }

    private void addDevicePropertyChangeListener() {
        Logg.d("addDevicePropertyChangeListener");
        JdSmartServiceProxy.getInstance().registerDevicePropertyListener(mPropertyChangeListener);
    }

    private void removeDevicePropertyChangeListener() {
        Logg.d("removeDevicePropertyChangeListener");
        JdSmartServiceProxy.getInstance().removeDevicePropertyListener(mPropertyChangeListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeDevicePropertyChangeListener();
    }

    private JdbaseCallback mPropertyChangeListener = new JdbaseCallback() {
        @Override
        public void onResult(final int code, final String data1, String s1) {
            Logg.d("onResult>" + code + "  data1:" + data1);
            if (code == JdbaseContant.RESULT_SUCCESS) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        JdSmartDevice device = JSON.parseObject(data1, JdSmartDevice.class);
                        if (device == null) return;
                        int index = mDatas.indexOf(device);
                        Logg.d("onResult>index>" + index);
                        if (index >= 0) {
                            mDatas.remove(index);
                            mDatas.add(index, device);
                            Logg.d("onResult>notifyDataSetChanged");
                            mNeedVview = false;
                            myAdapter.notifyItemChanged(index);
                        }
                        if (TextUtils.isEmpty(device.getDeviceType())) {
                            Logg.e("deviceType is null");
                            return;
                        }

                        JdSmartCtrlCmd cmd = device.getJdSmartCtrlCmd();
                        if (cmd == null) return;
                        int type = Integer.parseInt(device.getDeviceType());
                        //只有安防设备报警
                        if (JdSmartDeviceType.isSensorType(type)) {
                            //status 不等于0 表示报警
                            int status = 0;
                            if (!TextUtils.isEmpty(cmd.getValue1())) {
                                status = Integer.parseInt(cmd.getValue1());
                            }
                            //报警
                            if (!TextUtils.isEmpty(device.getJdAlert()) && status != 0) {
                                MusicManager.getInstance().sendAlert(device);
                            }
                        }
                    }
                });
            }
        }
    };

    private void initView() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false));
        myAdapter = new MyAdapter(getActivity());
        mRecyclerView.setAdapter(myAdapter);
        myAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos, Object o) {
                int deviceType = Integer.valueOf(mDatas.get(pos).getDeviceType());
                if (deviceType == JdSmartDeviceType.DEVICE_TYPE_AIRCONDITION) {
                    Intent intent = new Intent(getActivity(), InfraredCtrlActivity.class);
                    intent.putExtra("jdSmartDevice", JSON.toJSONString(mDatas.get(pos)));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getActivity().startActivity(intent);
                } else {
                    mNeedVview = true;
                    if (mStart != pos) {
                        if (mStart != -1) {
                            mLast = mStart;
                            myAdapter.notifyItemChanged(mLast);
                        }
                        mStart = pos;
                        myAdapter.notifyItemChanged(pos);
                    } else {
                        myAdapter.notifyItemChanged(pos);
                    }
                }
            }
        });
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                mNeedVview = false;
            }
        });
        mSetting = (Button) mRootView.findViewById(R.id.setting);
        mSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentLocal = new Intent();
                intentLocal.setClass(getActivity(), SmartSettingvity.class);
                intentLocal.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(intentLocal);
            }
        });
        mTitleBar.setTooBarOnClickListener(new TitleBar.TooBarOnClickListener() {
            @Override
            public void setRightImageViewOnClickListener() {

            }

            @Override
            public void setLeftImageViewOnClickListener() {
                Intent intent = new Intent(getActivity(), RoomChooseActivity.class);
                startActivityForResult(intent, 100);
                getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }

            @Override
            public void setRLayoutOnClickListener() {

            }
        });
        refTitle();
        handlerData(JdSmartLoadManager.getInstance().getJdSmartDevices(mfloorName, mroomName));
    }

    public void refTitle() {
        mfloorName = (String) SPUtils.get(getActivity(), "fName", "");
        mroomName = (String) SPUtils.get(getActivity(), "rName", "");
        mTitleBar.setLeftTextView(mfloorName + "_" + mroomName + "_设备控制");
    }

    public void handlerData(List<JdSmartDevice> list) {
        if (!isAdded()) return;
        mLoadingView.setVisibility(View.VISIBLE);
        mDatas.clear();
        if (list != null && list.size() > 0) {
            for (JdSmartDevice d : list) {
                if (d != null && !"-1".equals(d.getDeviceType())) {
                    Logg.d("handlerData>>>0>" + d);
                    mDatas.add(d);
                }
            }
            hideEmptyView();
        } else {
            showEmptyView();
            return;
        }
        mNeedVview = false;
        myAdapter.notifyDataSetChanged();
        if (mDatas != null) {
            Logg.d("handlerData2>>>" + mDatas.size());
        }
        mLoadingView.setVisibility(View.GONE);
    }

    public void showEmptyView() {
        if (isAdded()) {
            mDatas.clear();
            mNeedVview = false;
            myAdapter.notifyDataSetChanged();
            mLoadingView.setVisibility(View.GONE);
            mRootView.findViewById(R.id.load_fail).setVisibility(View.VISIBLE);
        }
    }

    private void hideEmptyView() {
        mRootView.findViewById(R.id.load_fail).setVisibility(View.GONE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public class MyAdapter extends BaseQuickAdapter<JdSmartDevice, BaseAdapterHelper> {

        public MyAdapter(Context context) {
            super(context, mDatas);
            setMutiItemTypeSupport(new MultiItemTypeSupport<JdSmartDevice>() {
                @Override
                public int getLayoutId(int viewType) {
                    switch (viewType) {
                        case JdSmartDeviceType.DEVICE_TYPE_AIRCONDITION:
//                            return R.layout.item_myadapter_ctrl;
                        case JdSmartDeviceType.DEVICE_TYPE_HEATER:
//                            return R.layout.item_myadapter_header_ctrl;
                        case JdSmartDeviceType.DEVICE_TYPE_CURTAINS:
                        case JdSmartDeviceType.DEVICE_TYPE_WINDOW_CONTROLER:
                        case JdSmartDeviceType.DEVICE_TYPE_DESK:
                        case JdSmartDeviceType.DEVICE_TYPE_DOOR:
                        case JdSmartDeviceType.DEVICE_TYPE_CURTAINS_NO_POSITION:
//                            return R.layout.item_myadapter_curtain;
                        case JdSmartDeviceType.DEVICE_TYPE_DIMMER:
                        case JdSmartDeviceType.DEVICE_TYPE_LAMP:
//                            return R.layout.item_myadapter_lamp_one;
                        case JdSmartDeviceType.DEVICE_TYPE_SOCKECT:
//                            return R.layout.item_myadapter_socket;
                        case JdSmartDeviceType.DEVICE_TYPE_HUMIDITY_SENSOR:
                        case JdSmartDeviceType.DEVICE_TYPE_TEMPERATURE_SENSOR:
                            //温度和湿度传感器
                        case JdSmartDeviceType.DEVICE_TYPE_TEMP_HUMIDITY_SENSOR:
                            //co2传感器
                        case JdSmartDeviceType.DEVICE_TYPE_CO2_SENSOR:
//                            return R.layout.item_myadapter_humidirty;
                        case JdSmartDeviceType.DEVICE_TYPE_INFRARED_SENSOR:
                        case JdSmartDeviceType.DEVICE_TYPE_FLAMMABLE_GAS:
                        case JdSmartDeviceType.DEVICE_TYPE_SOS_SENSOR:
                        case JdSmartDeviceType.DEVICE_TYPE_MAGNETIC_WINDOW:
                            //烟雾传感器
                        case JdSmartDeviceType.DEVICE_TYPE_SMOKER_SENSOR:
//                            return R.layout.item_myadapter_smart_device_standard;
                        case JdSmartDeviceType.DEVICE_TYPE_TV:
                        case JdSmartDeviceType.DEVICE_TYPE_STB:
//                            return R.layout.item_myadapter_tv_box;
                            return R.layout.item_myadapter_ctrl;
                    }
                    return R.layout.layout_unkown_device;
                }

                @Override
                public int getItemViewType(int position, JdSmartDevice o) {
                    int deviceType = Integer.parseInt(o.getDeviceType());
                    return deviceType;
                }
            });
        }

        @Override
        public int getItemCount() {
            return super.getItemCount();
        }

        @Override
        protected void convert(final BaseAdapterHelper helper, final int position, final JdSmartDevice item) {
            final int deviceType = Integer.parseInt(item.getDeviceType());
            if (helper.getTag() == null) {
                switch (deviceType) {
                    case JdSmartDeviceType.DEVICE_TYPE_AIRCONDITION:
                        builder = new AirConditionControllerViewBuilder(getActivity());
                        break;
                    case JdSmartDeviceType.DEVICE_TYPE_HEATER:
                        builder = new HeaterControllerViewBuilder(getActivity());
                        break;
                    //存在子类型  （多功能控制盒）
                    case JdSmartDeviceType.DEVICE_TYPE_CURTAINS:
                    case JdSmartDeviceType.DEVICE_TYPE_WINDOW_CONTROLER:
                    case JdSmartDeviceType.DEVICE_TYPE_DESK:
                    case JdSmartDeviceType.DEVICE_TYPE_DOOR:
                        if (!TextUtils.isEmpty(item.getDeviceSubType())) {
                            int subDeviceType = Integer.parseInt(item.getDeviceSubType());
                            if (subDeviceType == JdSmartDeviceType.DEVICE_SUB_TYPE_CONTROL_BOX) {//多功能盒子
                                builder = new SmartBoxContollerViewBuilder(getActivity());
                                break;
                            }
                        } else {
                            if (item.isNoReply()) {//没有状态返回的
                                builder = new SmartBoxContollerViewBuilder(getActivity());
                                break;
                            }
                        }
                        builder = new CurtainContollerViewBuilder(getActivity());
                        break;
                    case JdSmartDeviceType.DEVICE_TYPE_CURTAINS_NO_POSITION:
                        builder = new SmartBoxContollerViewBuilder(getActivity());
                        break;
                    case JdSmartDeviceType.DEVICE_TYPE_DIMMER:
                    case JdSmartDeviceType.DEVICE_TYPE_LAMP:
                        builder = new LampControllerViewBuilder(getActivity());
                        break;
                    case JdSmartDeviceType.DEVICE_TYPE_SOCKECT:
                        builder = new SocketControllerViewBuilder(getActivity());
                        break;
                    case JdSmartDeviceType.DEVICE_TYPE_HUMIDITY_SENSOR:
                    case JdSmartDeviceType.DEVICE_TYPE_TEMPERATURE_SENSOR:
                        //温度和湿度传感器
                    case JdSmartDeviceType.DEVICE_TYPE_TEMP_HUMIDITY_SENSOR:
                        //co2传感器
                    case JdSmartDeviceType.DEVICE_TYPE_CO2_SENSOR:
                        builder = new TempOrHumidirtyViewBuilder(getActivity());
                        break;
                    case JdSmartDeviceType.DEVICE_TYPE_INFRARED_SENSOR:
                    case JdSmartDeviceType.DEVICE_TYPE_FLAMMABLE_GAS:
                    case JdSmartDeviceType.DEVICE_TYPE_SOS_SENSOR:
                    case JdSmartDeviceType.DEVICE_TYPE_MAGNETIC_WINDOW:
                        //烟雾传感器
                    case JdSmartDeviceType.DEVICE_TYPE_SMOKER_SENSOR:
                        builder = new SensorStandardViewBuilder(getActivity());
                        break;
                    case JdSmartDeviceType.DEVICE_TYPE_TV:
                    case JdSmartDeviceType.DEVICE_TYPE_STB:
                        builder = new TVorBoxControllerViewBuilder(getActivity());
                        break;
                    default:
                        helper.getTextView(R.id.name).setText(String.format(helper.getTextView(R.id.name).getText().toString(), item.getDeviceType()));
                        break;
                }
                if (builder != null) {
                    builder.build(helper.itemView);
                    if (item != null && item.getJdSmartCtrlCmd() != null) {
                        builder.setDeviceInfo(item);
                    }
                    helper.setTag(builder);
                }
            } else {
                builder = (DeviceControlViewBuilder) helper.getTag();
                if (builder != null) {
                    if (item != null && item.getJdSmartCtrlCmd() != null) {
                        builder.setDeviceInfo(item);
                    }
                } else {
                    helper.getTextView(R.id.name).setText(String.format(helper.getTextView(R.id.name).getText().toString(), item.getDeviceType()));
                }
            }

            if (deviceType != JdSmartDeviceType.DEVICE_TYPE_AIRCONDITION) {
                if (mLast == position) {
                    builder.onGone();
                }
                if (mStart == position) {
                    if (mNeedVview) {
                        builder.onVior();
                        sbuilder = builder;
                        handler.removeCallbacksAndMessages(null);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                sbuilder.onGone();
                            }
                        }, TIME);
                    }
                } else {
                    builder.onGone();
                }
            }

        }
    }

    @Override
    public void onEventMainThread(CommandMessage msg) {
        super.onEventMainThread(msg);
        switch (msg.getCmd()) {
            case CommandMessage.SWITCH_THEME_PLAYER_CONTRL:
                mSetting.setBackgroundDrawable(SkinManager.getInstance().getDrawable(R.drawable.bfive_selector_red_frame));
                mSetting.setTextColor(SkinManager.getInstance().getColorStateList(R.color.bfive_sel_text_color_redtowhite));
                mNeedVview = false;
                myAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                refTitle();
                handlerData(JdSmartLoadManager.getInstance().getJdSmartDevices(mfloorName, mroomName));
            }
        } else if (requestCode == 1001) {
            if (resultCode == RESULT_OK) {
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
