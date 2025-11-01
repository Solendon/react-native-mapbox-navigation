import React


@objc(MapboxNavigationViewManager)
class MapboxNavigationViewManager: RCTViewManager {
  override func view() -> UIView! {
    return MapboxNavigationView();
  }
  @objc override func constantsToExport() -> [AnyHashable: Any]! {
    return [
      "Commands": [
        "CAMERA_TO_OVERVIEW": 1,
        "INIT_NAVIGATION": 2,
        "CAMERA_TO_FOLLOWING": 3
      ]
    ]
  }
  override static func requiresMainQueueSetup() -> Bool {
    return true
  }
  
  @objc(setWaypoints:waypoints:)
  public func setWaypoints(view: Any, waypoints: [MapboxWaypoint]) {
    guard let currentView = view as? MapboxNavigationView else {
      return
    }
    currentView.setWaypoints(waypoints: waypoints)
  }
  
  @objc func receiveCommand(_ reactTag: NSNumber) {
    guard let uiManager = self.bridge.module(for: RCTUIManager.self) as? RCTUIManager else { return }
    uiManager.addUIBlock { _, viewRegistry in
//      guard let view = viewRegistry?[reactTag] as? MapboxNavigationView else { return }
      NSLog("receiveCommand")
 
    }
  }
  
  @objc func destroy(_ reactTag:NSNumber){
    guard let uiManager = self.bridge.module(for: RCTUIManager.self) as? RCTUIManager else { return }
    uiManager.addUIBlock { _, viewRegistry in
      guard let view = viewRegistry?[reactTag] as? MapboxNavigationView else { return }
      NSLog("🧹  destroy")
      view.navViewController?.navigationService.stop()
      view.cleanupNavigation()
      // Pop về root của UINavigationController (tương tự như trước)
      if let rootNav = UIApplication.shared.connectedScenes
        .compactMap({ $0 as? UIWindowScene })
        .flatMap({ $0.windows })
        .first(where: { $0.isKeyWindow })?
        .rootViewController as? UINavigationController {
        rootNav.popToRootViewController(animated: true)
      }
    }
  }
  @objc func cameraToOverview(_ reactTag: NSNumber) {
    guard let uiManager = self.bridge.module(for: RCTUIManager.self) as? RCTUIManager else { return }
    uiManager.addUIBlock { _, viewRegistry in
      guard let view = viewRegistry?[reactTag] as? MapboxNavigationView else { return }
      NSLog("cameraToOverview")
      view.cameraToOverview()
    }
  }
  @objc func cameraToFollowing(_ reactTag: NSNumber) {
    guard let uiManager = self.bridge.module(for: RCTUIManager.self) as? RCTUIManager else { return }
    uiManager.addUIBlock { _, viewRegistry in
      guard let view = viewRegistry?[reactTag] as? MapboxNavigationView else { return }
      NSLog("cameraToFollowing")
      view.cameraToFollowing()
    }
  }

}
