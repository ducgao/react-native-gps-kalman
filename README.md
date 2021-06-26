
# react-native-react-native-gps-kalman

## Getting started

`$ npm install react-native-react-native-gps-kalman --save`

### Mostly automatic installation

`$ react-native link react-native-react-native-gps-kalman`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-react-native-gps-kalman` and add `RNReactNativeGpsKalman.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNReactNativeGpsKalman.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.reactlibrary.RNReactNativeGpsKalmanPackage;` to the imports at the top of the file
  - Add `new RNReactNativeGpsKalmanPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-react-native-gps-kalman'
  	project(':react-native-react-native-gps-kalman').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-react-native-gps-kalman/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-react-native-gps-kalman')
  	```


## Usage
```javascript
import RNReactNativeGpsKalman from 'react-native-react-native-gps-kalman';

// TODO: What to do with the module?
RNReactNativeGpsKalman;
```
  