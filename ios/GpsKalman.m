//
//  GpsKalman.m
//  RNReactNativeGpsKalman
//
//  Created by Macintosh HD on 26/06/2021.
//  Copyright © 2021 Facebook. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "React/RCTBridgeModule.h"
#import <React/RCTEventEmitter.h>

@interface RCT_EXTERN_MODULE(GpsKalman, RCTEventEmitter)

- (NSDictionary *)constantsToExport {
    return @{ @"GPS_KALMAN_LOCATION_UPDATED": @"GPS_KALMAN_LOCATION_UPDATED" };
}

RCT_EXTERN_METHOD(startSession)
RCT_EXTERN_METHOD(process:(NSNumber* _Nonnull)latitude longitude:(NSNumber* _Nonnull)longitude altitude:(NSNumber* _Nonnull)altitude timeStamp:(NSNumber* _Nonnull)timeStamp resolver:(RCTPromiseResolveBlock)resolver reject:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(startBackgroundSession)
RCT_EXTERN_METHOD(getCurrentBackgroundLocations:(RCTPromiseResolveBlock)resolver reject:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(stopBackgroundSession:(RCTPromiseResolveBlock)resolver reject:(RCTPromiseRejectBlock)reject)

@end
