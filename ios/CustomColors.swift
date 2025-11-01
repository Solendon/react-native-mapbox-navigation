import UIKit

extension UIColor {
  class var primaryColor: UIColor { #colorLiteral(red: 1, green: 0.1491314173, blue: 0, alpha: 1) }
  /// Màu chính của app, dùng cho các nút, highlight, icon
  class var defaultTintColor: UIColor { #colorLiteral(red: 0.1843137255, green: 0.4784313725, blue: 0.7764705882, alpha: 1) }
  class var defaultRouteTintColor: UIColor { #colorLiteral(red: 0, green: 0.9058823529, blue: 0.01568627451, alpha: 1) }
  
  /// Màu chữ chính
  class var defaultPrimaryTextColor: UIColor { #colorLiteral(red: 0.176, green: 0.176, blue: 0.176, alpha: 1) }
  
  /// Nền khi giao diện tối
  class var defaultDarkAppearanceBackgroundColor: UIColor { #colorLiteral(red: 0.1493228376, green: 0.2374534607, blue: 0.333029449, alpha: 1) }
  
  /// Màu viền chung
  class var defaultBorderColor: UIColor { #colorLiteral(red: 0.804, green: 0.816, blue: 0.816, alpha: 1) }
  
  /// Nền chính của giao diện
  class var defaultBackgroundColor: UIColor { #colorLiteral(red: 1, green: 1, blue: 1, alpha: 1) }
  
  // MARK: - Route Colors

  /// Màu viền tuyến chính
  class var defaultRouteCasing: UIColor { .defaultRouteTintColor }
  
  /// Màu tuyến chính
  class var defaultRouteLayer: UIColor { .defaultRouteTintColor }
  
  /// Màu tuyến thay thế
  class var defaultAlternateLine: UIColor { #colorLiteral(red: 0.6, green: 0.6, blue: 0.6, alpha: 1) }
  
  /// Màu viền tuyến thay thế
  class var defaultAlternateLineCasing: UIColor { #colorLiteral(red: 0.5019607843, green: 0.4980392157, blue: 0.5019607843, alpha: 1) }
  
  /// Màu tuyến đã đi qua (trong suốt)
  class var defaultTraversedRouteColor: UIColor { #colorLiteral(red: 1, green: 1, blue: 1, alpha: 0) }
  
  // MARK: - Maneuver Arrow Colors

  /// Viền mũi tên hướng dẫn
  class var defaultManeuverArrowStroke: UIColor { .defaultRouteLayer }
  
  /// Màu mũi tên hướng dẫn
  class var defaultManeuverArrow: UIColor { #colorLiteral(red: 1, green: 1, blue: 1, alpha: 1) }
  
  /// Màu mũi tên rẽ chính (trắng)
  class var defaultTurnArrowPrimary: UIColor { #colorLiteral(red: 1, green: 1, blue: 1, alpha: 1) }
  
  /// Màu mũi tên rẽ phụ (xám)
  class var defaultTurnArrowSecondary: UIColor { #colorLiteral(red: 0.6196078431, green: 0.6196078431, blue: 0.6196078431, alpha: 1) }
  
  /// Màu mũi tên rẽ chính khi highlight
  class var defaultTurnArrowPrimaryHighlighted: UIColor { #colorLiteral(red: 1, green: 1, blue: 1, alpha: 1) }
  
  /// Màu mũi tên rẽ phụ khi highlight (trắng mờ)
  class var defaultTurnArrowSecondaryHighlighted: UIColor { UIColor.white.withAlphaComponent(0.4) }
  
  // MARK: - Lane Arrow Colors

  class var defaultLaneArrowPrimary: UIColor { #colorLiteral(red: 1, green: 1, blue: 1, alpha: 1) }
  class var defaultLaneArrowSecondary: UIColor { UIColor.defaultLaneArrowPrimary.withAlphaComponent(0.5) }
  class var defaultLaneArrowPrimaryHighlighted: UIColor { #colorLiteral(red: 1, green: 1, blue: 1, alpha: 1) }
  class var defaultLaneArrowSecondaryHighlighted: UIColor { UIColor.defaultLaneArrowPrimaryHighlighted.withAlphaComponent(0.2) }
  
  // MARK: - Traffic Colors

  /// Giao thông tuyến chính
  class var trafficUnknown: UIColor { defaultRouteTintColor }/// dữ liệu giao thông "không xác định"
  class var trafficLow: UIColor { defaultRouteTintColor }/// mật độ giao thông thấp — di chuyển thuận lợi.
  class var trafficModerate: UIColor { #colorLiteral(red: 1, green: 0.5843137255, blue: 0, alpha: 1) }  /// mật độ giao thông trung bình — hơi đông nhưng vẫn đi được.
  class var trafficHeavy: UIColor { #colorLiteral(red: 1, green: 0.3019607843, blue: 0.3019607843, alpha: 1) } /// mật độ giao thông cao — thường là tắc nhẹ.
  class var trafficSevere: UIColor { #colorLiteral(red: 0.5607843137, green: 0.1411764706, blue: 0.2784313725, alpha: 1) } /// mật độ giao thông nghiêm trọng — tắc đường nặng.

  /// Giao thông tuyến thay thế
  class var alternativeTrafficUnknown: UIColor { defaultAlternateLine }
  class var alternativeTrafficLow: UIColor { defaultAlternateLine }
  class var alternativeTrafficModerate: UIColor { #colorLiteral(red: 0.75, green: 0.63, blue: 0.53, alpha: 1.0) }
  class var alternativeTrafficHeavy: UIColor { #colorLiteral(red: 0.71, green: 0.51, blue: 0.51, alpha: 1.0) }
  class var alternativeTrafficSevere: UIColor { #colorLiteral(red: 0.71, green: 0.51, blue: 0.51, alpha: 1.0) }

  // MARK: - Building Colors

  class var defaultBuildingColor: UIColor { #colorLiteral(red: 0.9833194452, green: 0.9843137255, blue: 0.9331936657, alpha: 0.8019049658) }
  class var defaultBuildingHighlightColor: UIColor { #colorLiteral(red: 0.337254902, green: 0.6588235294, blue: 0.9843137255, alpha: 0.949406036) }

  // MARK: - Restricted Area

  class var defaultRouteRestrictedAreaColor: UIColor { #colorLiteral(red: 0, green: 0, blue: 0, alpha: 1) }

  // MARK: - Route Duration Annotations

  class var routeDurationAnnotationColor: UIColor { #colorLiteral(red: 1, green: 1, blue: 1, alpha: 1) }
  class var selectedRouteDurationAnnotationColor: UIColor { #colorLiteral(red: 0.337254902, green: 0.6588235294, blue: 0.9843137255, alpha: 1) }
  class var routeDurationAnnotationTextColor: UIColor { #colorLiteral(red: 0, green: 0, blue: 0, alpha: 1) }
  class var selectedRouteDurationAnnotationTextColor: UIColor { #colorLiteral(red: 1, green: 1, blue: 1, alpha: 1) }

  // MARK: - Road Shield Colors (Biển báo đường)

  class var roadShieldDefaultColor: UIColor { #colorLiteral(red: 0.11, green: 0.11, blue: 0.15, alpha: 1) }
  class var roadShieldBlackColor: UIColor { roadShieldDefaultColor }
  class var roadShieldBlueColor: UIColor { #colorLiteral(red: 0.28, green: 0.36, blue: 0.8, alpha: 1) }
  class var roadShieldGreenColor: UIColor { #colorLiteral(red: 0.1, green: 0.64, blue: 0.28, alpha: 1) }
  class var roadShieldRedColor: UIColor { #colorLiteral(red: 0.95, green: 0.23, blue: 0.23, alpha: 1) }
  class var roadShieldWhiteColor: UIColor { #colorLiteral(red: 1.0, green: 1.0, blue: 1.0, alpha: 1) }
  class var roadShieldYellowColor: UIColor { #colorLiteral(red: 1.0, green: 0.9, blue: 0.4, alpha: 1) }
  class var roadShieldOrangeColor: UIColor { #colorLiteral(red: 1, green: 0.65, blue: 0, alpha: 1) } /// màu cam biển báo
  
  
  class var puckColor: UIColor { primaryColor } /// Màu mũi tên điều hướng
  class var fillColor: UIColor { UIColor.white } /// Màu mũi tên điều hướng
  class var shadowColor: UIColor { primaryColor.withAlphaComponent(0.1) } /// Màu mũi tên điều hướng
}
