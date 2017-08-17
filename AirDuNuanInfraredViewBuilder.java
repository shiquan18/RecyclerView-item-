package com.judianb5.builder;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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

import java.util.HashMap;

/**
 * Created by house on 16/7/6.
 */
public class AirDuNuanInfraredViewBuilder extends InfraredControlViewBuilder {
    private TitleBar mtoobar;
    private GridView mGridView;
    private ImageView mImageView;

    private LinearLayout mLinearLayout1;
    private TextView mTextView1;
    private TextView mTextView2;
    private TextView mTextView3;
    private TextView mTextView4;
    private TextView mTextView5;
    private TextView mTextView6;
    private LinearLayout mLinearLayout2;
    private TextView mTextView11;
    private TextView mTextView12;
    private TextView mTextView13;

    private JdSmartDevice mJdsmartDevice;

    public AirDuNuanInfraredViewBuilder(Context cxt) {
        super(cxt);
    }

    @Override
    public void build(View rootView) {
        initView(rootView);
        mtoobar.setRightImageViewVisibleOrGone();
        mtoobar.setRightTVVisibleOrGone();
        mtoobar.setRightTextView("23℃");
        mtoobar.setLeftTextView("空气净化器控制");
        mtoobar.setTooBarOnClickListener(new TitleBar.TooBarOnClickListener() {
            @Override
            public void setRightImageViewOnClickListener() {

            }

            @Override
            public void setLeftImageViewOnClickListener() {
                ((Activity) mContext).onBackPressed();
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
                        break;
                    case 1://关
                        break;
                    case 2://加
                        break;
                    case 3://减
                        break;
                    case 4://制冷
                        break;
                    case 5://制热
                        break;
                    case 6://抽湿
                        break;
                    case 7://循环
                        break;
                }
            }
        });
    }

    private void initView(View rootView) {
        mtoobar = (TitleBar) rootView.findViewById(R.id.title_bar);
        mGridView = (GridView) rootView.findViewById(R.id.gv_infare);
        mImageView = (ImageView) rootView.findViewById(R.id.iv_image);
        mLinearLayout1 = (LinearLayout) rootView.findViewById(R.id.ll_jhq);
        mTextView1 = (TextView) rootView.findViewById(R.id.tv_name_zhi1);
        mTextView2 = (TextView) rootView.findViewById(R.id.tv_name_zhi2);
        mTextView3 = (TextView) rootView.findViewById(R.id.tv_name_zhi3);
        mTextView4 = (TextView) rootView.findViewById(R.id.tv_name_zhi4);
        mTextView5 = (TextView) rootView.findViewById(R.id.tv_name_zhi5);
        mTextView6 = (TextView) rootView.findViewById(R.id.tv_name_zhi6);
        mLinearLayout2 = (LinearLayout) rootView.findViewById(R.id.ll_dnuan);
        mTextView11 = (TextView) rootView.findViewById(R.id.tv_name_wendu);
        mTextView12 = (TextView) rootView.findViewById(R.id.tv_name_wendu_zhi1);
        mTextView13 = (TextView) rootView.findViewById(R.id.tv_tishi);
    }

    @Override
    public void setDeviceInfo(JdSmartDevice jdsmartDevice) {
        mJdsmartDevice = jdsmartDevice;
        Logg.d("AirDuNuanInfraredViewBuilder jdsmartDevice:" + jdsmartDevice);
        if (mJdsmartDevice != null) {
            refresh();
        }
    }

    private void refresh() {
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
            }
        });
    }

}
