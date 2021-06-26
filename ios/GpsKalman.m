//
//  GpsKalman.m
//  RNReactNativeGpsKalman
//
//  Created by Macintosh HD on 26/06/2021.
//  Copyright © 2021 Facebook. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "React/RCTBridgeModule.h"

@interface RCT_EXTERN_MODULE(GpsKalman, NSObject)
RCT_EXTERN_METHOD(startSession)
RCT_EXTERN_METHOD(process:(NSNumber*)latitude longitude:(NSNumber*)longitude altitude:(NSNumber*)altitude timeStamp:(NSNumber*)timeStamp resolver:(RCTPromiseResolveBlock)resolver reject:(RCTPromiseRejectBlock)reject)

@end
