
package com.reactlibrary;

import android.location.Location;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;

import java.util.List;

import mad.location.manager.lib.Commons.Utils;
import mad.location.manager.lib.Loggers.GeohashRTFilter;

public class RNReactNativeGpsKalmanModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;

    private GeohashRTFilter geoHashRTFilter;

    public RNReactNativeGpsKalmanModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "GpsKalman";
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