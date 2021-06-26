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
class GpsKalman: NSObject {

    var resetKalmanFilter: Bool = false
    var hcKalmanFilter: HCKalmanAlgorithm?

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
                self.hcKalmanFilter.resetKalman(newStartLocation: location)
            }
            
            return location
        }
        
        
        return self.hcKalmanFilter?.processState(currentLocation: location)
    }
    
    @objc
    func process(_ latitude: Double, longitude: Double, altitude: Double, timeStamp: NSNumber, resolver: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
        let result = self.process(latitude, longitude: longitude, altitude: altitude, timeStamp: timeStamp)
        
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
}
