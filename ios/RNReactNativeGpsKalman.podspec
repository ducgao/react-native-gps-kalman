
Pod::Spec.new do |s|
  s.name         = "RNReactNativeGpsKalman"
  s.version      = "1.0.0"
  s.summary      = "RNReactNativeGpsKalman"
  s.description  = <<-DESC
                  RNReactNativeGpsKalman
                   DESC
  s.homepage     = "https://github.com/ducgao/react-native-gps-kalman"
  s.license      = "MIT"
  # s.license      = { :type => "MIT", :file => "FILE_LICENSE" }
  s.author             = { "author" => "author@domain.cn" }
  s.platform     = :ios, "9.0"
  s.source       = { :git => "https://github.com/ducgao/react-native-gps-kalman.git", :tag => "master" }
  s.source_files  = "**/*.{c,h,m,mm,cpp,swift}"
  s.requires_arc = true


  s.dependency "React"
  s.dependency "HCKalmanFilter"
  #s.dependency "others"

  s.script_phase = { :name => "Config Umbrella Header", :script => "echo '#import \"React/RCTBridgeModule.h\"' >> Target\\ Support\\ Files/RNReactNativeGpsKalman/RNReactNativeGpsKalman-umbrella.h", :execution_position => :before_compile }


end

  