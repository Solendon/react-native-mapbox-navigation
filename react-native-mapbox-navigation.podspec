
require 'json'

package = JSON.parse(File.read(File.join(__dir__, 'package.json')))

Pod::Spec.new do |s|
  s.name         = "react-native-mapbox-navigation"
  s.version      = package["version"]
  s.summary      = package["description"] || "React Native module"
  s.license      = package["license"] || "MIT"
  s.author       = package["author"] || "Author"
  s.homepage     = package["homepage"] || "https://example.com"

  s.platform     = :ios, "11.0"

  # 👇 chỉ cần chỉ ra source_files để import toàn bộ code iOS
  s.source       = { :path => "." }
  s.source_files = "ios/**/*.{h,m,mm,swift}"

  # Liên kết với React Native
  s.dependency "React-Core"
  s.dependency 'MapboxNavigation', '~> 2.19.0'
  s.dependency 'MapboxCoreNavigation', '~> 2.19.0'
  s.resource_bundles = {
  'RNMapboxResources' => ['ios/Assets.xcassets']
  }

end
