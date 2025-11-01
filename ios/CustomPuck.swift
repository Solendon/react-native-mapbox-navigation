
import Foundation
import MapboxCoreNavigation
import MapboxNavigation
import MapboxDirections
import MapboxMaps
final class CustomPuck {
  static func puckIcon2D(imageName: String)-> Puck2DConfiguration?{
    guard let carImage = UIImage(named: imageName, in: .main, compatibleWith: nil),
          let cgImage = carImage.cgImage else {
      print("⚠️ Không tìm thấy ảnh \(imageName)")
      return nil
    }
    
    let rotatedImage = UIImage(cgImage: cgImage, scale: carImage.scale, orientation: .down)
    
    let puckConfig = Puck2DConfiguration(
      topImage: rotatedImage,
      bearingImage: rotatedImage,
      shadowImage: nil,
      scale: .constant(1.0),
      showsAccuracyRing: false,
      opacity: 1
    )
    
    return puckConfig
  }
  static func puck3D(modelName: String, scale: Double = 1.0) -> Puck3DConfiguration? {
      guard let modelURL = Bundle.main.url(forResource: modelName, withExtension: "glb") else {
          print("⚠️ Không tìm thấy model \(modelName).glb trong bundle")
          return nil
      }

      let model = Model(uri: modelURL)

      return Puck3DConfiguration(
          model: model,
          modelScale: .constant([scale, scale, scale]),
          modelRotation: .constant([0.0, 0.0, 0.0]),
          modelOpacity: .constant(1.0)
      )
  }
  static func demoPuck3D() -> Puck3DConfiguration? {
      guard let uri = Bundle.main.url(forResource: "arrow", withExtension: "glb") else {
          print("⚠️ Không tìm thấy file arrow.glb")
          return nil
      }
      
      let myModel = Model(
          uri: uri,
          position: [-122.396152, 37.79129],
          orientation: [0, 0, 0]
      )
      
      // Scale model theo mức zoom
      let scalingExpressionArrow = Exp(.interpolate) {
          Exp(.linear)
          Exp(.zoom)
          0; Exp(.literal) { [256000.0, 256000.0, 256000.0] }
          4; Exp(.literal) { [40000.0, 40000.0, 40000.0] }
          8; Exp(.literal) { [2000.0, 2000.0, 2000.0] }
          12; Exp(.literal) { [100.0, 100.0, 100.0] }
          16; Exp(.literal) { [10.0, 10.0, 10.0] }
          20; Exp(.literal) { [3.0, 3.0, 3.0] }
      }
      let scaleFactor = 1.5
      let scalingExpression = Exp(.interpolate) {
          Exp(.linear)
          Exp(.zoom)
          0; Exp(.literal) { [128000.0, 128000.0, 128000.0].map { $0 * scaleFactor } }
          4; Exp(.literal) { [20000.0, 20000.0, 20000.0].map { $0 * scaleFactor } }
          8; Exp(.literal) { [1000.0, 1000.0, 1000.0].map { $0 * scaleFactor } }
          12; Exp(.literal) { [50.0, 50.0, 50.0].map { $0 * scaleFactor } }
          16; Exp(.literal) { [5.0, 5.0, 5.0].map { $0 * scaleFactor } }
          20; Exp(.literal) { [1.0, 1.0, 1.0].map { $0 * scaleFactor } }
      }
      
       
      let rotationArrow: [Double] = [0, 90, 90]
      let rotationFix: [Double] = [0, 0, -180] 
      
      let puck = Puck3DConfiguration(
          model: myModel,
          modelScale: .expression(scalingExpressionArrow),
          modelRotation: .constant(rotationArrow)
      )
      
      return puck
  }

  static func updatePuck(on location: LocationManager, vehicle: Int? ) {
    var carName = "ic_car"
    if let vehicleInt = vehicle {
        if vehicleInt == Vehicle.MOTO.rawValue {
            carName = "ic_moto"
        } else {
            carName = "ic_car"
        }
    }
    guard let config2d = puckIcon2D(imageName: carName) else { return }
//    guard let config3d = demoPuck3D() else { return }
    location.options.puckType = nil
    location.options.puckType = .puck2D(config2d)
//    location.options.puckType = .puck3D(config3d)
    location.options.puckBearingEnabled = true
    location.options.puckBearing = .heading
    
    print("✅ Puck updated → \(carName)")
  }
}
