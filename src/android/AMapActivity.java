package com.github.starwander.amap.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.util.Log;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.enums.PathPlanningStrategy;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;

import com.autonavi.tbt.TrafficFacilityInfo;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.SpeechUtility;

import com.github.starwander.amap.navigation.AMapNavigation;

public class AMapActivity extends Activity implements AMapNaviListener, AMapNaviViewListener {
    private AMapNaviView mAmapAMapNaviView;
    private boolean mIsEmulatorNavi = false;

    private NaviLatLng mNaviStart;
    private NaviLatLng mNaviEnd;
    private ArrayList<NaviLatLng> mStartPoints = new ArrayList<NaviLatLng>();
    private ArrayList<NaviLatLng> mEndPoints = new ArrayList<NaviLatLng>();

    private SpeechSynthesizer mSpeechSynthesizer = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AMapNavi.getInstance(this).setAMapNaviListener(this);
        Intent intent = getIntent();
        mNaviStart = new NaviLatLng(Float.parseFloat(intent.getStringExtra("NaviStartLat")),
                Float.parseFloat(intent.getStringExtra("NaviStartLng")));
        mNaviEnd = new NaviLatLng(Float.parseFloat(intent.getStringExtra("NaviEndLat")),
                Float.parseFloat(intent.getStringExtra("NaviEndLng")));
        LinearLayout l = new LinearLayout(this);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        l.setLayoutParams(layoutParams);

        if (intent.getStringExtra("NavType").equals("0")) {
            mIsEmulatorNavi = false;
        } else {
            mIsEmulatorNavi = true;
        }

        mAmapAMapNaviView = new AMapNaviView(this);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        l.addView(mAmapAMapNaviView, lp);

        setContentView(l);
        init(savedInstanceState);
    }

    private void init(Bundle savedInstanceState) {
        mStartPoints.add(mNaviStart);
        mEndPoints.add(mNaviEnd);
        mAmapAMapNaviView.onCreate(savedInstanceState);
        mAmapAMapNaviView.setAMapNaviViewListener(this);
        AMapNavi.getInstance(this).calculateDriveRoute(mStartPoints, mEndPoints, null,
                PathPlanningStrategy.DRIVING_AVOID_CONGESTION);

        SpeechUtility.createUtility(this, SpeechConstant.APPID + "="
                + AMapNavigation.cordovaWebView.getPreferences().getString("iflytekappid", ""));
        mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(this, null);
        mSpeechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
        mSpeechSynthesizer.setParameter(SpeechConstant.SPEED, "50");
        mSpeechSynthesizer.setParameter(SpeechConstant.VOLUME, "80");

        mSpeechSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
    }

    @Override
    public boolean onNaviBackClick() {
        return false;
    }

    private SynthesizerListener mTtsListener = new SynthesizerListener() {
        @Override
        public void onSpeakBegin() {

        }

        @Override
        public void onSpeakPaused() {

        }

        @Override
        public void onSpeakResumed() {

        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {

        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {

        }

        @Override
        public void onCompleted(SpeechError error) {

        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {

        }
    };

    @Override
    public void onCalculateRouteFailure(int arg0) {
        mSpeechSynthesizer.startSpeaking("路径计算失败，请检查网络或输入参数", mTtsListener);
    }

    @Override
    public void onCalculateRouteSuccess() {
        if (mIsEmulatorNavi) {
            AMapNavi.getInstance(this).setEmulatorNaviSpeed(100);
            AMapNavi.getInstance(this).startNavi(AMapNavi.EmulatorNaviMode);

        } else {
            AMapNavi.getInstance(this).startNavi(AMapNavi.GPSNaviMode);
        }
    }

    @Override
    public void onArrivedWayPoint(int arg0) {

    }

    @Override
    public void onEndEmulatorNavi() {

        mSpeechSynthesizer.startSpeaking("导航结束", mTtsListener);
    }

    @Override
    public void onGetNavigationText(int arg0, String arg1) {

        mSpeechSynthesizer.startSpeaking(arg1, mTtsListener);
    }

    @Override
    public void onGpsOpenStatus(boolean arg0) {

    }

    @Override
    public void onInitNaviFailure() {

        Log.i("result", "导航失败");
    }

    @Override
    public void onInitNaviSuccess() {

        Log.i("result", "导航注册成功");
    }

    @Override
    public void onLocationChange(AMapNaviLocation arg0) {

        AMapNavigation.getInstance().keepCallback(arg0.getCoord());
    }

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo arg0) {

    }

    @Override
    public void onReCalculateRouteForTrafficJam() {

        mSpeechSynthesizer.startSpeaking("前方路线拥堵，路线重新规划", mTtsListener);
    }

    @Override
    public void onReCalculateRouteForYaw() {

        mSpeechSynthesizer.startSpeaking("您已偏航", mTtsListener);
    }

    @Override
    public void onStartNavi(int arg0) {

        Log.i("result", "启动导航1");
    }

    @Override
    public void onTrafficStatusUpdate() {

    }

    @Override
    public void onServiceAreaUpdate(AMapServiceAreaInfo[] infoArray) {

    }

    @Override
    public void updateCameraInfo(AMapNaviCameraInfo[] infoArray) {

    }

    @Override
    public void onPlayRing(int type) {

    }

    @Override
    public void onArriveDestination() {

    }

    //-----------------------------导航界面回调事件------------------------
    @Override
    public void onNaviCancel() {
        this.setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onNaviSetting() {

    }

    @Override
    public void onNaviMapMode(int arg0) {

    }

    @Override
    public void onNaviTurnClick() {

    }

    @Override
    public void onNextRoadClick() {

    }

    @Override
    public void onScanViewButtonClick() {

    }

    // ------------------------------生命周期方法---------------------------
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mAmapAMapNaviView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mAmapAMapNaviView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mAmapAMapNaviView.onPause();
        AMapNavi.getInstance(this).stopNavi();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAmapAMapNaviView.onDestroy();
        mSpeechSynthesizer.stopSpeaking();
    }

    @Override
    public void onLockMap(boolean arg0) {

    }

    @Override
    public void onNaviViewLoaded() {

    }

    @Override
    public void onNaviInfoUpdate(NaviInfo arg0) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

    }

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {

    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {

    }

    @Override
    public void hideCross() {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

    }

    @Override
    public void hideLaneInfo() {

    }

    @Override
    public void onCalculateMultipleRoutesSuccess(int[] ints) {

    }

    @Override
    public void notifyParallelRoad(int i) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

    }

    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

    }

}
