import UIKit
import MapboxMaps
import MapboxCoreNavigation
import MapboxNavigation
import MapboxDirections

//final class CustomStyleUIElements: UIViewController {
//    private let mapboxNavigationProvider = MapboxNavigationProvider(
//        coreConfig: .init(
//            locationSource: simulationIsEnabled ? .simulation(
//                initialLocation: .init(latitude: 37.7744, longitude: -122.4354)
//            ) : .live
//        )
//    )
//    private var mapboxNavigation: MapboxNavigation {
//        mapboxNavigationProvider.mapboxNavigation
//    }
//
//    override func viewDidLoad() {
//        super.viewDidLoad()
//
//        let origin = CLLocationCoordinate2D(latitude: 37.7744, longitude: -122.4354)
//        let destination = CLLocationCoordinate2D(latitude: 37.7655, longitude: -122.4240)
//        let options = NavigationRouteOptions(coordinates: [origin, destination])
//
//        let request = mapboxNavigation.routingProvider().calculateRoutes(options: options)
//
//        Task {
//            switch await request.result {
//            case .failure(let error):
//                print("Route error:", error.localizedDescription)
//            case .success(let navigationRoutes):
//                let navigationOptions = NavigationOptions(
//                    mapboxNavigation: mapboxNavigation,
//                    voiceController: mapboxNavigationProvider.routeVoiceController,
//                    eventsManager: mapboxNavigationProvider.eventsManager(),
//                    styles: [CustomDayStyle(), CustomNightStyle()]
//                )
//                let navigationVC = NavigationViewController(
//                    navigationRoutes: navigationRoutes,
//                    navigationOptions: navigationOptions
//                )
//
//                navigationVC.modalPresentationStyle = .fullScreen
//                navigationVC.routeLineTracksTraversal = true
//
//                present(navigationVC, animated: true)
//            }
//        }
//    }
//}

final class CustomDayStyle: DayStyle {
  required init() {
    super.init()
    //        mapStyleURL = URL(string: "mapbox://styles/mapbox/satellite-streets-v12")!
    styleType = .day
  }
  
  override func apply() {
    super.apply()
    
    let traitCollection = UIScreen.main.traitCollection
    let carPlayTraitCollection = UITraitCollection(userInterfaceIdiom: .carPlay)
     
    let primary = #colorLiteral(red: 1, green: 0.1491314173, blue: 0, alpha: 1)
    let white = #colorLiteral(red: 1, green: 1, blue: 1, alpha: 1)
//    let routePrimary = #colorLiteral(red: 0, green: 0.9058823529, blue: 0.01568627451, alpha: 1) // #00E704
 
    // Top banner
    TopBannerView.appearance(for: traitCollection).backgroundColor = primary
    InstructionsBannerView.appearance(for: traitCollection).backgroundColor = primary
    
    
    StepsBackgroundView.appearance(for: traitCollection).backgroundColor = .defaultTintColor
    StepInstructionsView.appearance(for: traitCollection).backgroundColor = .defaultTintColor
    StepListIndicatorView.appearance(for: traitCollection).gradientColors = [#colorLiteral(red: 0.431372549, green: 0.431372549, blue: 0.431372549, alpha: 1), #colorLiteral(red: 0.6274509804, green: 0.6274509804, blue: 0.6274509804, alpha: 1), #colorLiteral(red: 0.431372549, green: 0.431372549, blue: 0.431372549, alpha: 1)]
    StepTableViewCell.appearance(for: traitCollection).backgroundColor = .defaultTintColor


    
    // Màu của text chỉ đường
    PrimaryLabel.appearance(for: traitCollection).normalFont = UIFont.systemFont(ofSize: 30.0, weight: .medium).adjustedFont
    PrimaryLabel.appearance(for: traitCollection, whenContainedInInstancesOf: [InstructionsBannerView.self]).normalTextColor = #colorLiteral(red: 1, green: 1, blue: 1, alpha: 1)
    PrimaryLabel.appearance(for: traitCollection, whenContainedInInstancesOf: [InstructionsCardView.self]).normalTextColor = #colorLiteral(red: 1, green: 1, blue: 1, alpha: 1)
    PrimaryLabel.appearance(for: traitCollection, whenContainedInInstancesOf: [InstructionsCardView.self]).textColorHighlighted = #colorLiteral(red: 0.2196078449, green: 0.007843137719, blue: 0.8549019694, alpha: 1) // Khi text được chọn
    PrimaryLabel.appearance(for: traitCollection, whenContainedInInstancesOf: [InstructionsCardView.self]).normalFont = UIFont.boldSystemFont(ofSize: 24.0).adjustedFont
    PrimaryLabel.appearance(for: traitCollection, whenContainedInInstancesOf: [StepInstructionsView.self]).normalTextColor = #colorLiteral(red: 1, green: 1, blue: 1, alpha: 1)
    
    SecondaryLabel.appearance(for: traitCollection).normalFont = UIFont.systemFont(ofSize: 26.0, weight: .medium).adjustedFont
    SecondaryLabel.appearance(for: traitCollection, whenContainedInInstancesOf: [InstructionsBannerView.self]).normalTextColor = #colorLiteral(red: 1, green: 1, blue: 1, alpha: 1)
    SecondaryLabel.appearance(for: traitCollection, whenContainedInInstancesOf: [InstructionsCardView.self]).normalTextColor = #colorLiteral(red: 1, green: 1, blue: 1, alpha: 1)
    SecondaryLabel.appearance(for: traitCollection, whenContainedInInstancesOf: [InstructionsCardView.self]).textColorHighlighted = #colorLiteral(red: 0.2196078449, green: 0.007843137719, blue: 0.8549019694, alpha: 1) // Khi text được chọn
    SecondaryLabel.appearance(for: traitCollection, whenContainedInInstancesOf: [InstructionsCardView.self]).normalFont = UIFont.boldSystemFont(ofSize: 18.0).adjustedFont
    SecondaryLabel.appearance(for: traitCollection, whenContainedInInstancesOf: [StepInstructionsView.self]).normalTextColor = #colorLiteral(red: 1, green: 1, blue: 1, alpha: 1)
    
    
    // Maneuver (mũi tên hướng rẽ)

    ManeuverView.appearance(for: traitCollection).primaryColorHighlighted = .defaultTurnArrowPrimaryHighlighted
    ManeuverView.appearance(for: traitCollection).secondaryColorHighlighted = .defaultTurnArrowSecondaryHighlighted
    ManeuverView.appearance(for: traitCollection, whenContainedInInstancesOf: [InstructionsBannerView.self]).primaryColor = .defaultTurnArrowPrimary
    ManeuverView.appearance(for: traitCollection, whenContainedInInstancesOf: [InstructionsBannerView.self]).secondaryColor = .defaultTurnArrowSecondary
    ManeuverView.appearance(for: traitCollection, whenContainedInInstancesOf: [InstructionsCardView.self]).primaryColor = .defaultTurnArrowPrimary
    ManeuverView.appearance(for: traitCollection, whenContainedInInstancesOf: [InstructionsCardView.self]).secondaryColor = .defaultTurnArrowSecondary
    ManeuverView.appearance(for: traitCollection, whenContainedInInstancesOf: [NextBannerView.self]).primaryColor = .defaultTurnArrowPrimary
    ManeuverView.appearance(for: traitCollection, whenContainedInInstancesOf: [NextBannerView.self]).secondaryColor = .defaultTurnArrowSecondary
    ManeuverView.appearance(for: traitCollection, whenContainedInInstancesOf: [StepInstructionsView.self]).primaryColor = .defaultTurnArrowPrimary
    ManeuverView.appearance(for: traitCollection, whenContainedInInstancesOf: [StepInstructionsView.self]).secondaryColor = .defaultTurnArrowSecondary
    
    // Khoảng cách | 20 km

    DistanceLabel.appearance(for: traitCollection).unitFont = UIFont.systemFont(ofSize: 14.0, weight: .medium).adjustedFont
    DistanceLabel.appearance(for: traitCollection).valueFont = UIFont.systemFont(ofSize: 22.0, weight: .medium).adjustedFont
    DistanceLabel.appearance(for: traitCollection, whenContainedInInstancesOf: [InstructionsBannerView.self]).unitTextColor = #colorLiteral(red: 1, green: 1, blue: 1, alpha: 1) // km
    DistanceLabel.appearance(for: traitCollection, whenContainedInInstancesOf: [InstructionsBannerView.self]).valueTextColor = #colorLiteral(red: 1, green: 1, blue: 1, alpha: 1) //20
    DistanceLabel.appearance(for: traitCollection, whenContainedInInstancesOf: [InstructionsCardView.self]).valueTextColor = #colorLiteral(red: 1, green: 1, blue: 1, alpha: 1)
    DistanceLabel.appearance(for: traitCollection, whenContainedInInstancesOf: [InstructionsCardView.self]).unitTextColor = #colorLiteral(red: 1, green: 1, blue: 1, alpha: 1)
    DistanceLabel.appearance(for: traitCollection, whenContainedInInstancesOf: [InstructionsCardView.self]).unitFont = UIFont.systemFont(ofSize: 16.0).adjustedFont
    DistanceLabel.appearance(for: traitCollection, whenContainedInInstancesOf: [InstructionsCardView.self]).valueFont = UIFont.boldSystemFont(ofSize: 20.0).adjustedFont
    DistanceLabel.appearance(for: traitCollection, whenContainedInInstancesOf: [InstructionsCardView.self]).unitTextColorHighlighted = #colorLiteral(red: 1, green: 1, blue: 1, alpha: 1)
    DistanceLabel.appearance(for: traitCollection, whenContainedInInstancesOf: [InstructionsCardView.self]).valueTextColorHighlighted = #colorLiteral(red: 1, green: 1, blue: 1, alpha: 1)
    DistanceLabel.appearance(for: traitCollection, whenContainedInInstancesOf: [StepInstructionsView.self]).unitTextColor = #colorLiteral(red: 1, green: 1, blue: 1, alpha: 1)
    DistanceLabel.appearance(for: traitCollection, whenContainedInInstancesOf: [StepInstructionsView.self]).valueTextColor = #colorLiteral(red: 1, green: 1, blue: 1, alpha: 1)
    
    
    
    TimeRemainingLabel.appearance(for: traitCollection).textColor = .white
    
    
    
    // Lanes (làn đường)
    LanesView.appearance(for: traitCollection).backgroundColor = primary
    
    LaneView.appearance(for: traitCollection).primaryColor = .defaultLaneArrowPrimary
    LaneView.appearance(for: traitCollection).secondaryColor = .defaultLaneArrowSecondary
    LaneView.appearance(for: traitCollection).primaryColorHighlighted = .defaultLaneArrowPrimaryHighlighted
    LaneView.appearance(for: traitCollection).secondaryColorHighlighted = .defaultLaneArrowSecondaryHighlighted
    StatusView.appearance(for: traitCollection).backgroundColor = primary.withAlphaComponent(0.2)
    
    // Nút nổi
    FloatingButton.appearance(for: traitCollection).backgroundColor = .clear
    FloatingButton.appearance(for: traitCollection).tintColor = .clear
    FloatingButton.appearance(for: traitCollection).isHidden = true
    FloatingButton.appearance(for: traitCollection).borderWidth = 0
    FloatingButton.appearance(for: traitCollection).borderColor = .clear
    FloatingButton.appearance(for: traitCollection).isUserInteractionEnabled = false
    
    ResumeButton.appearance(for: traitCollection).backgroundColor = .clear
    ResumeButton.appearance(for: traitCollection).tintColor = .clear
    ResumeButton.appearance(for: traitCollection).largeContentTitle = nil
    ResumeButton.appearance(for: traitCollection).isHidden = true
    ResumeButton.appearance(for: carPlayTraitCollection).isHidden = true
    ResumeButton.appearance(for: traitCollection).borderWidth = 0
    ResumeButton.appearance(for: traitCollection).isUserInteractionEnabled = false
 
    
    
    
    
    // Bản đồ
    NavigationMapView.appearance(for: traitCollection).tintColor = primary
    
    NavigationMapView.appearance(for: traitCollection).trafficHeavyColor = .trafficHeavy
    NavigationMapView.appearance(for: traitCollection).trafficLowColor = .trafficLow
    NavigationMapView.appearance(for: traitCollection).trafficModerateColor = .trafficModerate
    NavigationMapView.appearance(for: traitCollection).trafficSevereColor = .trafficSevere
    NavigationMapView.appearance(for: traitCollection).trafficUnknownColor = .trafficUnknown
    
    NavigationMapView.appearance(for: traitCollection).routeDurationAnnotationColor = .routeDurationAnnotationColor
    NavigationMapView.appearance(for: traitCollection).routeDurationAnnotationSelectedColor = .selectedRouteDurationAnnotationColor
    
    NavigationMapView.appearance(for: traitCollection).routeDurationAnnotationTextColor = #colorLiteral(red: 0.09803921569, green: 0.09803921569, blue: 0.09803921569, alpha: 1)
    NavigationMapView.appearance(for: traitCollection).routeDurationAnnotationSelectedTextColor = #colorLiteral(red: 1, green: 1, blue: 1, alpha: 1)
    
    NavigationMapView.appearance(for: carPlayTraitCollection).routeAlternateColor = .defaultAlternateLine
    NavigationMapView.appearance(for: carPlayTraitCollection).routeCasingColor = .defaultRouteCasing

    
    
    NavigationMapView.appearance(for: traitCollection).maneuverArrowColor = .red
    NavigationMapView.appearance(for: traitCollection).maneuverArrowStrokeColor = .red
    
    //
    UserPuckCourseView.appearance(for: traitCollection).puckColor = .puckColor
    UserPuckCourseView.appearance(for: traitCollection).fillColor = .fillColor
    UserPuckCourseView.appearance(for: traitCollection).shadowColor = .shadowColor
    
    UserPuckCourseView.appearance(for: carPlayTraitCollection).puckColor = .puckColor
    UserPuckCourseView.appearance(for: carPlayTraitCollection).fillColor = .fillColor
    UserPuckCourseView.appearance(for: carPlayTraitCollection).shadowColor = .shadowColor
  
    
    
    UserHaloCourseView.appearance(for: carPlayTraitCollection).haloColor = #colorLiteral(red: 1, green: 1, blue: 1, alpha: 0.5)
    UserHaloCourseView.appearance(for: carPlayTraitCollection).haloRingColor = #colorLiteral(red: 0.149, green: 0.239, blue: 0.341, alpha: 0.3)
    UserHaloCourseView.appearance(for: carPlayTraitCollection).haloRadius = 100.0
    
    
    
    
    WayNameLabel.appearance(for: traitCollection).normalFont = UIFont.systemFont(ofSize: 20.0, weight: .medium).adjustedFont
    WayNameLabel.appearance(for: traitCollection).normalTextColor = .white
    WayNameLabel.appearance(for: traitCollection).roadShieldBlackColor = .roadShieldBlackColor
    WayNameLabel.appearance(for: traitCollection).roadShieldBlueColor = .roadShieldBlueColor
    WayNameLabel.appearance(for: traitCollection).roadShieldGreenColor = .roadShieldGreenColor
    WayNameLabel.appearance(for: traitCollection).roadShieldRedColor = .roadShieldRedColor
    WayNameLabel.appearance(for: traitCollection).roadShieldWhiteColor = .roadShieldWhiteColor
    WayNameLabel.appearance(for: traitCollection).roadShieldYellowColor = .roadShieldYellowColor
    WayNameLabel.appearance(for: traitCollection).roadShieldOrangeColor = .roadShieldOrangeColor
    WayNameLabel.appearance(for: traitCollection).roadShieldDefaultColor = .roadShieldDefaultColor
    
    WayNameView.appearance(for: traitCollection).backgroundColor = .defaultTintColor
    WayNameView.appearance(for: traitCollection).borderColor = .defaultTintColor.withAlphaComponent(0.5)
    WayNameView.appearance(for: traitCollection).borderWidth = 1.0
    
  }
}
 
