package com.reactlibrary;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.List;

import mad.location.manager.lib.Commons.Utils;
import mad.location.manager.lib.Loggers.GeohashRTFilter;

public class GpsKalmanService extends Service implements LocationListener {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startJob(intent);
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {

        Log.d("DUCGAO", "chet con me no roi");

        super.onDestroy();
    }

    private GeohashRTFilter geoHashRTFilter;


    private void startJob(Intent intent) {
        if (geoHashRTFilter == null) {
            geoHashRTFilter = new GeohashRTFilter(Utils.GEOHASH_DEFAULT_PREC, Utils.GEOHASH_DEFAULT_MIN_POINT_COUNT);
        }

        geoHashRTFilter.reset(null);

        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);

        boolean isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (isNetworkEnable) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
            Location lastLoc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            doKalman(lastLoc);
            return;
        }

        boolean isGpsEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGpsEnable) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
            Location lastLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            doKalman(lastLoc);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        doKalman(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private void doKalman(Location location) {
        Log.d("DUCGAO", "doKalman: " + location);
        if (location == null) return;

        geoHashRTFilter.filter(location);
        List<Location> latestData = geoHashRTFilter.getGeoFilteredTrack();

        Location locToResolve;

        if (latestData.isEmpty()) {
            locToResolve = location;
        } else {
            locToResolve = latestData.get(latestData.size() - 1);
        }

        Intent intentResolve = new Intent();

        intentResolve.putExtra("latitude", locToResolve.getLatitude());
        intentResolve.putExtra("longitude", locToResolve.getLongitude());
        intentResolve.putExtra("altitude", locToResolve.getAltitude());
        intentResolve.putExtra("time", location.getTime());

        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intentResolve);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
