package com.judianb5.builder;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.judian.jdsmart.common.entity.JdSmartDevice;
import com.judianb5.R;

/**
 * Created by house on 16/8/4.
 */
public abstract class DeviceControlViewBuilder {
    public TextView tv_kai;
    public TextView tv_guan;
    public TextView tv_zanting;
    public TextView tv_connect;
    public RelativeLayout rl_control;
    private View mRootView;

    protected Context mContext;

    public Context getContext() {
        return mContext;
    }

    public RelativeLayout getRelatveL() {
        return rl_control;
    }

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }

    public DeviceControlViewBuilder(Context cxt) {
        mContext = cxt;
    }

    public void build(View rootView) {
        mRootView = rootView;
        tv_kai = (TextView) rootView.findViewById(R.id.kai);
        tv_guan = (TextView) rootView.findViewById(R.id.guan);
        tv_zanting = (TextView) rootView.findViewById(R.id.zanting);
        tv_connect = (TextView) rootView.findViewById(R.id.tv_content);
        rl_control = (RelativeLayout) rootView.findViewById(R.id.rl_control);
        tv_kai.setOnClickListener(onClickListener);
        tv_guan.setOnClickListener(onClickListener);
        tv_zanting.setOnClickListener(onClickListener);
    }

    protected void runOnMainThread(Runnable task) {
        new Handler(Looper.getMainLooper()).post(task);
    }

    public View getRootView() {
        return mRootView;
    }

    public void setRootView(View rootView) {
        mRootView = rootView;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.kai:
                    toOn();
                    break;
                case R.id.guan:
                    toOff();
                    break;
                case R.id.zanting:
                    toPause();
                    break;
            }
        }
    };

    public abstract void setDeviceInfo(JdSmartDevice jdsmartDevice);

    public abstract int getDeviceType();

    public abstract void toOn();

    public abstract void toOff();

    public abstract void toPause();

    public void toEdit() {
    }

    public void onGone() {
        if (rl_control.getVisibility() != View.GONE) {
            setVorGView(rl_control, 1f, 0f);
        }
    }

    public void onVior() {
        if (rl_control.getVisibility() != View.VISIBLE) {
            setVorGView(rl_control, 0f, 1f);
        }
    }

    /**
     * view渐变动画
     *
     * @param mview
     * @param f
     * @param f1
     */
    public void setVorGView(View mview, float f, float f1) {
        AlphaAnimation mHiddenAction = new AlphaAnimation(f, f1);
        mHiddenAction.setDuration(600);
        mview.startAnimation(mHiddenAction);
        if (mview.getVisibility() == View.VISIBLE) {
            mview.setVisibility(View.GONE);
        } else {
            mview.setVisibility(View.VISIBLE);
        }
    }
}
