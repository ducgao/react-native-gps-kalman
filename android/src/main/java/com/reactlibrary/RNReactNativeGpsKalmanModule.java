
package com.reactlibrary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mad.location.manager.lib.Commons.Utils;
import mad.location.manager.lib.Loggers.GeohashRTFilter;

public class RNReactNativeGpsKalmanModule extends ReactContextBaseJavaModule {

    public static final String GPS_KALMAN_EVENT_FILTER_LOCATION = "com.ducgao.gps_kalman_location";
    public static final String GPS_KALMAN_LOCATION_UPDATED = "GPS_KALMAN_LOCATION_UPDATED";

    private final ReactApplicationContext reactContext;

    private GeohashRTFilter geoHashRTFilter;
    private final ArrayList<Location> backgroundLocations = new ArrayList<>();

    private final BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            double latitude = intent.getDoubleExtra("latitude", 0.0);
            double longitude = intent.getDoubleExtra("longitude", 0.0);
            double altitude = intent.getDoubleExtra("altitude", 0.0);
            long time = intent.getLongExtra("time", 0);

            if (latitude == 0.0 || longitude == 0.0 || time == 0) {
                return;
            }

            Location loc = new Location("kalman");
            loc.setLatitude(latitude);
            loc.setLongitude(longitude);
            loc.setAltitude(altitude);
            loc.setTime(time);

            backgroundLocations.add(loc);

            WritableMap resolveMap = Arguments.createMap();

            resolveMap.putDouble("latitude", latitude);
            resolveMap.putDouble("longitude", longitude);
            resolveMap.putDouble("altitude", altitude);
            resolveMap.putDouble("time", time);

            reactContext
                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit(GPS_KALMAN_LOCATION_UPDATED, resolveMap);
        }
    };

    public RNReactNativeGpsKalmanModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;

        IntentFilter kalmanLocFilter = new IntentFilter();
        kalmanLocFilter.addAction(GPS_KALMAN_EVENT_FILTER_LOCATION);
        LocalBroadcastManager.getInstance(reactContext.getApplicationContext()).registerReceiver(locationReceiver, kalmanLocFilter);
    }

    @Override
    public String getName() {
        return "GpsKalman";
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put(GPS_KALMAN_LOCATION_UPDATED, GPS_KALMAN_LOCATION_UPDATED);
        return constants;
    }

    @ReactMethod
    public void startBackgroundSession() {
        backgroundLocations.clear();

        Intent intent = new Intent(reactContext.getApplicationContext(), GpsKalmanService.class);
        reactContext.startService(intent);
    }

    @ReactMethod
    void stopBackgroundSession(Promise promise) {
        Intent intent = new Intent(reactContext.getApplicationContext(), GpsKalmanService.class);
        reactContext.stopService(intent);

        WritableArray resolveArray = Arguments.createArray();

        for (int i = 0; i < backgroundLocations.size(); i++) {
            Location loc = backgroundLocations.get(i);
            WritableMap resolveMap = Arguments.createMap();

            resolveMap.putDouble("latitude", loc.getLatitude());
            resolveMap.putDouble("longitude", loc.getLongitude());
            resolveMap.putDouble("altitude", loc.getAltitude());
            resolveMap.putDouble("time", loc.getTime());

            resolveArray.pushMap(resolveMap);
        }

        promise.resolve(resolveArray);
    }

    @ReactMethod
    public void startSession() {
        if (geoHashRTFilter == null) {
            geoHashRTFilter = new GeohashRTFilter(Utils.GEOHASH_DEFAULT_PREC, Utils.GEOHASH_DEFAULT_MIN_POINT_COUNT);
        }

        geoHashRTFilter.reset(null);
    }

    @ReactMethod
    public void process(Double latitude, Double longitude, Double altitude, Double timeStamp, Promise promise) {
        Location loc = new Location("GpsKalman");
        loc.setLatitude(latitude);
        loc.setLongitude(longitude);
        loc.setAltitude(altitude);
        loc.setTime(timeStamp.longValue());

        geoHashRTFilter.filter(loc);
        List<Location> latestData = geoHashRTFilter.getGeoFilteredTrack();

        Location locToResolve;

        if (latestData.isEmpty()) {
            locToResolve = loc;
        } else {
            locToResolve = latestData.get(latestData.size() - 1);
        }

        WritableMap objectResolve = Arguments.createMap();

        objectResolve.putDouble("latitude", locToResolve.getLatitude());
        objectResolve.putDouble("longitude", locToResolve.getLongitude());
        objectResolve.putDouble("altitude", locToResolve.getAltitude());
        objectResolve.putDouble("time", timeStamp);

        promise.resolve(objectResolve);
    }
}