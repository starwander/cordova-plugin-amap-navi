package com.github.starwander.amap.navigation;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.amap.api.navi.model.NaviLatLng;

import com.github.starwander.amap.activity.AMapActivity;

public class AMapNavigation extends CordovaPlugin {
    private CallbackContext callbackContext;
    private static AMapNavigation mapNavigation = null;
    public static CordovaWebView cordovaWebView = null;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        cordovaWebView = webView;
        if (action.equals("navigation")) {
            mapNavigation = this;
            this.callbackContext = callbackContext;
            Intent intent = new Intent();
            intent.setClass(this.cordova.getActivity().getApplicationContext(), AMapActivity.class);
            intent.putExtra("NaviStartLng", args.getString(0));
            intent.putExtra("NaviStartLat", args.getString(1));
            intent.putExtra("NaviEndLng", args.getString(2));
            intent.putExtra("NaviEndLat", args.getString(3));
            intent.putExtra("NavType", args.getString(4));
            this.cordova.startActivityForResult((CordovaPlugin) this, intent, 100);
            return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 100) {
            JSONObject json = new JSONObject();
            try {
                if (resultCode == Activity.RESULT_CANCELED) {
                    json.put("status", -1);
                    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, json);
                    callbackContext.sendPluginResult(pluginResult);
                } else {
                    json.put("status", 0);
                    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, json);
                    callbackContext.sendPluginResult(pluginResult);
                }
            } catch (JSONException ex) {
                Log.e("AMapNavigation.onActivityResult", ex.getMessage());
            }
        }
    }

    public void keepCallback(NaviLatLng point) {
        JSONObject json = new JSONObject();
        try {
            json.put("status", 1);
            json.put("lat", point.getLatitude());
            json.put("lng", point.getLongitude());
        } catch (JSONException ex) {
            Log.e("AMapNavigation.keepCallback", ex.getMessage());
        }
        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, json);
        pluginResult.setKeepCallback(true);
        callbackContext.sendPluginResult(pluginResult);
    }

    public static AMapNavigation getInstance() {
        return mapNavigation;
    }
}
