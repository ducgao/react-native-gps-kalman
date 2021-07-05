//
//  GpsKalman.swift
//  RNReactNativeGpsKalman
//
//  Created by Macintosh HD on 26/06/2021.
//  Copyright © 2021 Facebook. All rights reserved.
//

import Foundation
import HCKalmanFilter
import MapKit

@objc(GpsKalman)
class GpsKalman: RCTEventEmitter, CLLocationManagerDelegate {

    var resetKalmanFilter: Bool = false
    var hcKalmanFilter: HCKalmanAlgorithm?
    
    var locationManager: CLLocationManager?
    var backgroundLocations: NSMutableArray?

    @objc
    func startSession() {
        resetKalmanFilter = true
    }
    
    @objc
    func process(_ latitude: Double, longitude: Double, altitude: Double, timeStamp: NSNumber) -> CLLocation? {
        let date = Date.init(timeIntervalSince1970: TimeInterval(truncating: timeStamp))
        let location = CLLocation(coordinate: CLLocationCoordinate2D(latitude: latitude, longitude: longitude), altitude: CLLocationDistance(0), horizontalAccuracy: CLLocationAccuracy(0), verticalAccuracy: CLLocationAccuracy(0), timestamp: date)
        
        if (resetKalmanFilter) {
            if (self.hcKalmanFilter == nil) {
                self.hcKalmanFilter = HCKalmanAlgorithm(initialLocation: location)
            } else {
                self.hcKalmanFilter!.resetKalman(newStartLocation: location)
            }
            
            resetKalmanFilter = false
            return location
        }
        
        
        return self.hcKalmanFilter?.processState(currentLocation: location)
    }
    
    @objc
    func process(_ latitude: NSNumber, longitude: NSNumber, altitude: NSNumber, timeStamp: NSNumber, resolver: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
        let result = self.process(latitude.doubleValue, longitude: longitude.doubleValue, altitude: altitude.doubleValue, timeStamp: timeStamp)
        
        if (result == nil) {
            reject("ERROR", "Can not correct the point", nil)
        } else {
            resolver([
                "latitude": result!.coordinate.latitude,
                "longitude": result!.coordinate.longitude,
                "altitude": result!.altitude,
                "time": timeStamp,
            ])
        }
    }
        
    @objc
    func startBackgroundSession() {
        resetKalmanFilter = true
        
        if (backgroundLocations == nil) {
            backgroundLocations = NSMutableArray()
        }
        
        backgroundLocations?.removeAllObjects()
        
        if (locationManager == nil) {
            locationManager = CLLocationManager()
            locationManager?.delegate = self
            locationManager?.desiredAccuracy = kCLLocationAccuracyBest
            locationManager?.distanceFilter = 0.1
            
            locationManager?.requestAlwaysAuthorization()
        }
        
        locationManager?.startUpdatingLocation()
    }
    
    @objc
    func getCurrentBackgroundLocations(_ resolver: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
        resolver(backgroundLocations)
    }
    
    @objc
    func stopBackgroundSession(_ resolver: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
        locationManager?.stopUpdatingLocation()
        resolver(backgroundLocations)
    }
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        
        if (locations.isEmpty || locations.first == nil) {
            return
        }
        
        if (resetKalmanFilter) {
            if (self.hcKalmanFilter == nil) {
                self.hcKalmanFilter = HCKalmanAlgorithm(initialLocation: locations.first!)
            } else {
                self.hcKalmanFilter!.resetKalman(newStartLocation: locations.first!)
            }
            
            resetKalmanFilter = false
            return
        }
        
        let filteredLoc = self.hcKalmanFilter?.processState(currentLocation: locations.first!)
        if (filteredLoc != nil) {
            backgroundLocations?.add([
                "latitude": filteredLoc!.coordinate.latitude,
                "longitude": filteredLoc!.coordinate.longitude,
                "altitude": filteredLoc!.altitude,
                "time": filteredLoc!.timestamp,
            ])
            self.sendEvent(withName: "GPS_KALMAN_LOCATION_UPDATED", body: [
                "latitude": filteredLoc!.coordinate.latitude,
                "longitude": filteredLoc!.coordinate.longitude,
                "altitude": filteredLoc!.altitude,
                "time": filteredLoc!.timestamp,
            ])
        }
    }
}
