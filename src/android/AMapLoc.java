package com.github.starwander.amap.location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;

public class AMapLoc extends CordovaPlugin {
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;

    private CallbackContext callbackContext = null;
    private LocationWrapper myLocation = new LocationWrapper();
    private Context context;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        context = this.cordova.getActivity().getApplicationContext();
        super.initialize(cordova, webView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;
        if (action.equals("getLocation")) {
            myLocation.getLocation(context);
            PluginResult r = new PluginResult(PluginResult.Status.NO_RESULT);
            r.setKeepCallback(true);
            callbackContext.sendPluginResult(r);
            return true;
        }
        return false;
    }

    public class LocationWrapper implements AMapLocationListener {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation != null && amapLocation.getErrorCode() == 0) {
                Double latitude = amapLocation.getLatitude();
                Double longitude = amapLocation.getLongitude();
                boolean hasAccuracy = amapLocation.hasAccuracy();
                float accuracy = amapLocation.getAccuracy();
                String address = amapLocation.getAddress();
                String province = amapLocation.getProvince();
                String road = amapLocation.getRoad();
                float speed = amapLocation.getSpeed();
                float bearing = amapLocation.getBearing();
                int satellites = amapLocation.getExtras().getInt("satellites", 0);
                long time = amapLocation.getTime();

                JSONObject jo = new JSONObject();
                try {
                    jo.put("latitude", latitude);
                    jo.put("longitude", longitude);
                    jo.put("hasAccuracy", hasAccuracy);
                    jo.put("accuracy", accuracy);
                    jo.put("address", address);
                    jo.put("province", province);
                    jo.put("road", road);
                    jo.put("speed", speed);
                    jo.put("bearing", bearing);
                    jo.put("satellites", satellites);
                    jo.put("time", time);

                } catch (JSONException e) {
                    jo = null;
                    e.printStackTrace();
                }
                callbackContext.success(jo);
            } else {
                callbackContext.error(amapLocation.getErrorInfo());
            }
        }

        public void getLocation(Context context) {
            locationClient = new AMapLocationClient(context);
            locationOption = new AMapLocationClientOption();

            locationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
            locationClient.setLocationListener(this);
            locationOption.setOnceLocation(true);
            locationClient.setLocationOption(locationOption);

            locationClient.startLocation();
        }
    }
}
