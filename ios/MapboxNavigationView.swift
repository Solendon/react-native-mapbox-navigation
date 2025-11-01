
import MapboxCoreNavigation
import MapboxNavigation
import MapboxDirections
import MapboxMaps
import React

import CoreLocation
import UIKit


extension UIView {
  var parentViewController: UIViewController? {
    var parentResponder: UIResponder? = self
    while parentResponder != nil {
      parentResponder = parentResponder!.next
      if let viewController = parentResponder as? UIViewController {
        return viewController
      }
    }
    return nil
  }
}

 
 
@objc(MapboxNavigationView)
public class MapboxNavigationView: UIView, NavigationViewControllerDelegate {
  public weak var navViewController: NavigationViewController?
  public var indexedRouteResponse: IndexedRouteResponse?
  private let routingProvider = MapboxRoutingProvider()
  var embedded: Bool
  var embedding: Bool = false {
    didSet {
      onEmbeddingChange?(["embedding": embedding])
    }
  }
  
  
  @objc public var startOrigin: NSArray = [] {
    didSet { setNeedsLayout() }
  }
  @objc var destination: NSArray = [] {
    didSet { setNeedsLayout() }
  }
  @objc var vehicle: NSNumber = 1 {
    didSet { setNeedsLayout() }
  }
  var waypoints: [Waypoint] = [] {
    didSet { setNeedsLayout() }
  }
  
  func setWaypoints(waypoints: [MapboxWaypoint]) {
    self.waypoints = waypoints.enumerated().map { (index, waypointData) in
      let name = waypointData.name as? String ?? "\(index)"
      let waypoint = Waypoint(coordinate: waypointData.coordinate, name: name)
      waypoint.separatesLegs = waypointData.separatesLegs
      return waypoint
    }
  }
  @objc func cameraToOverview() {
    self.navViewController?.navigationView.navigationMapView.navigationCamera.moveToOverview()
  }
  @objc func cameraToFollowing() {
    self.navViewController?.navigationView.navigationMapView.navigationCamera.follow()
  }
  
  @objc var shouldSimulateRoute: Bool = false
  @objc var showsEndOfRouteFeedback: Bool = false
  @objc var showCancelButton: Bool = false
  @objc var hideStatusView: Bool = false
  @objc var mute: Bool = true
  @objc var distanceUnit: NSString = "imperial"
  @objc var language: NSString = "vi"
  @objc var destinationTitle: NSString = "Điểm đến"
  
  @objc var onLocationChange: RCTDirectEventBlock?
  @objc var onEmbeddingChange: RCTDirectEventBlock?
  @objc var onRouteProgressChange: RCTDirectEventBlock?
  @objc var onManeuversUpdate: RCTDirectEventBlock?
  @objc var onNavigationCameraState: RCTDirectEventBlock?
  @objc var onError: RCTDirectEventBlock?
  @objc var onCancelNavigation: RCTDirectEventBlock?
  @objc var onArrive: RCTDirectEventBlock?
  @objc var vehicleMaxHeight: NSNumber?
  @objc var vehicleMaxWidth: NSNumber?
  
  override init(frame: CGRect) {
    self.embedded = false
    self.embedding = false
    super.init(frame: frame)
  }
  
  required init?(coder aDecoder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }
  
  public override func layoutSubviews() {
    super.layoutSubviews()
    
    if (navViewController == nil && !embedding && !embedded) {
      embed()
    } else {
      navViewController?.view.frame = bounds
    }
  }
  public override func removeFromSuperview() {
    super.removeFromSuperview()
    print("🧹 removeFromSuperview  — cleaning up")
    cleanupNavigation()
  }
  deinit {
    print("🧹 deinit — cleaning up")
    cleanupNavigation()
  }
  
  public func cleanupNavigation() {
    print("🧹 cleanupNavigation")
    guard let navVC = navViewController else {
      print("🧹 navViewController is not found")
      return
    }
    navVC.navigationService.stop()
    navVC.navigationService.delegate = nil
    navVC.navigationService.router.delegate = nil
    navVC.navigationView.navigationMapView.navigationCamera.stop()
    navVC.navigationView.navigationMapView.mapView.removeFromSuperview()
    navVC.navigationView.navigationMapView.removeFromSuperview()
    navVC.view.removeFromSuperview()
    navVC.removeFromParent()
    navVC.willMove(toParent: nil)
    navVC.removeFromParent()
    navVC.didMove(toParent: nil)
    navVC.navigationService.router.delegate = nil

    self.navViewController = nil
    self.indexedRouteResponse = nil
    self.embedded = false
    self.embedding = false
    self.navViewController = nil
    self.indexedRouteResponse = nil
    
    NotificationCenter.default.removeObserver(self)
    
    NavigationSettings.shared.voiceMuted = true
    NavigationSettings.shared.distanceUnit = .kilometer
    print("✅ cleanupNavigation complete")
  }
  private func embed() {
     
    guard startOrigin.count == 2 && destination.count == 2 else {
      print("❌ error origin and destination are required")
      self.onError!(["error": "origin and destination are required"])
      return
    }
    NSLog("🧤🧤🧤  embed start ")
    
    embedding = true
    
    
    let originWaypoint = Waypoint(coordinate: CLLocationCoordinate2D(latitude: startOrigin[1] as! CLLocationDegrees, longitude: startOrigin[0] as! CLLocationDegrees))
    var waypointsArray = [originWaypoint]
    
    // Add Waypoints
    waypointsArray.append(contentsOf: waypoints)
    
    let destinationWaypoint = Waypoint(coordinate: CLLocationCoordinate2D(latitude: destination[1] as! CLLocationDegrees, longitude: destination[0] as! CLLocationDegrees), name: destinationTitle as String)
    waypointsArray.append(destinationWaypoint)
    var profileIdentifier: ProfileIdentifier = .automobileAvoidingTraffic
    
     
    if let vehicleInt = vehicle as? Int {
        if vehicleInt == Vehicle.MOTO.rawValue {
          profileIdentifier = .cycling
        } else {
          profileIdentifier = .automobileAvoidingTraffic
        }
    }
    
    let options = NavigationRouteOptions(waypoints: waypointsArray, profileIdentifier: profileIdentifier)
    
    let locale = self.language.replacingOccurrences(of: "-", with: "_")
    options.locale = Locale(identifier: locale)
    options.distanceMeasurementSystem =  distanceUnit == "imperial" ? .imperial : .metric
    routingProvider.calculateRoutes(options: options) { [weak self] result in
//    Directions.shared.calculateRoutes(options: options) { [weak self] result in
      guard let strongSelf = self, let parentVC = strongSelf.parentViewController else {
        return
      }
      
      switch result {
      case .failure(let error):
        strongSelf.onError!(["message": error.localizedDescription])
      case .success(let response):
        strongSelf.indexedRouteResponse = response
         
//        let navigationOptions = NavigationOptions(simulationMode: strongSelf.shouldSimulateRoute ? .always : .never)
//        navigationOptions.styles = [CustomDayStyle()]
//        navigationOptions.topBanner = CustomTopBarViewController()
        
        /// setting voice
        let navigationService = MapboxNavigationService(indexedRouteResponse: response,
                                                        customRoutingProvider: strongSelf.routingProvider,
                                                        credentials: NavigationSettings.shared.directions.credentials,
                                                        simulating: strongSelf.shouldSimulateRoute ? .always : .never)
        let speechSynthesizer = MultiplexedSpeechSynthesizer([CustomVoiceController(), SystemSpeechSynthesizer()])
        let routeVoiceController = RouteVoiceController(navigationService: navigationService,
                                                        speechSynthesizer: speechSynthesizer)
        let navigationOptions = NavigationOptions(navigationService: navigationService,
                                                  voiceController: routeVoiceController)
        navigationOptions.styles = [CustomDayStyle()]
        
        
        
        
        
        let vc = NavigationViewController(for: response, navigationOptions: navigationOptions)
        vc.navigationView.bottomBannerContainerView.hide(animated: false)
        vc.navigationView.navigationMapView.routeLineTracksTraversal = true
        vc.navigationView.navigationMapView.mapView.ornaments.options.logo.margins = .init(x: -1000, y: -1000)
        vc.navigationView.navigationMapView.mapView.ornaments.logoView.isHidden = true
        
         
        if let mapView = vc.navigationMapView?.mapView {
          mapView.mapboxMap.onEvery(event: .mapLoaded) { _ in
            vc.navigationView.navigationMapView.mapView.ornaments.logoView.isHidden = true
            vc.showsReportFeedback = false
//            vc.navigationView.navigationMapView.userLocationStyle = .none
//            CustomPuck.updatePuck(on: mapView.location, vehicle: self?.vehicle as? Int)
          }
        }
        
        vc.showsEndOfRouteFeedback = strongSelf.showsEndOfRouteFeedback
        StatusView.appearance().isHidden = strongSelf.hideStatusView
        
        NavigationSettings.shared.voiceMuted = strongSelf.mute
        NavigationSettings.shared.distanceUnit = strongSelf.distanceUnit == "imperial" ? .mile : .kilometer
        
        vc.delegate = strongSelf
        
        parentVC.addChild(vc)
        strongSelf.addSubview(vc.view)
        vc.view.frame = strongSelf.bounds
        vc.didMove(toParent: parentVC)
        let gesture = UILongPressGestureRecognizer(target: self, action: #selector(strongSelf.handleLongPress(_:)))
        vc.navigationView.navigationMapView.addGestureRecognizer(gesture)
        
        strongSelf.navViewController = vc
        NotificationCenter.default.addObserver(
          strongSelf,
          selector: #selector(strongSelf.navigationCameraStateDidChange(_:)),
          name: .navigationCameraStateDidChange,
          object: vc.navigationView.navigationMapView.navigationCamera
        )
      }
      
      strongSelf.embedding = false
      strongSelf.embedded = true

    }
  }
  @objc func handleLongPress(_ gesture: UILongPressGestureRecognizer) {
    guard gesture.state == .ended else { return }
    
    let point = gesture.location(in: self)
    onCancelNavigation?([
      "message": "Long press detected",
      "x": point.x,
      "y": point.y
    ])
  }
  @objc func navigationCameraStateDidChange(_ notification: Notification) {
    guard let navigationCameraState = notification.userInfo?[NavigationCamera.NotificationUserInfoKey.state] as? NavigationCameraState else {
      return
    }
    
    switch navigationCameraState {
    case .transitionToFollowing, .following:
      onNavigationCameraState?(["follow": true])
      break
    case .idle, .transitionToOverview, .overview:
      onNavigationCameraState?(["follow": false])
      break
    }
  }
  public func navigationViewController(_ navigationViewController: NavigationViewController, didUpdate progress: RouteProgress, with location: CLLocation, rawLocation: CLLocation) {
    onLocationChange?([
      "longitude": location.coordinate.longitude,
      "latitude": location.coordinate.latitude,
      "heading": 0,
      "accuracy": location.horizontalAccuracy.magnitude
    ])
    onRouteProgressChange?([
      "distanceTraveled": progress.distanceTraveled,
      "durationRemaining": progress.durationRemaining,
      "fractionTraveled": progress.fractionTraveled,
      "distanceRemaining": progress.distanceRemaining
    ])
    onManeuversUpdate?([
      "maneuvers": progress.remainingSteps.map { step in
        [
          "primary.text": step.description,
          "primary.drivingSide": step.drivingSide.rawValue,
          "primary.modifier": step.maneuverDirection?.rawValue ?? "",
          "sub.text": "",
          "stepDistance.distanceRemaining": step.distance,
          "stepDistance.totalDistance": "",
          "maneuverPoint.latitude": step.maneuverLocation.latitude,
          "maneuverPoint.longitude": step.maneuverLocation.longitude,
          "laneGuidance":"",
        ]
      }
    ])
  }
  
  public func navigationViewControllerDidDismiss(_ navigationViewController: NavigationViewController, byCanceling canceled: Bool) {
    if (!canceled) {
      return;
    }
    onCancelNavigation?(["message": "Navigation Cancel"]);
  }
  
  public func navigationViewController(_ navigationViewController: NavigationViewController, didArriveAt waypoint: Waypoint) -> Bool {
    onArrive?([
      "name": waypoint.name ?? waypoint.description,
      "longitude": waypoint.coordinate.latitude,
      "latitude": waypoint.coordinate.longitude,
    ])
    return true;
  }
}
