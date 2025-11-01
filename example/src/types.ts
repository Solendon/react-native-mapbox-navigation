import type { StyleProp, ViewStyle } from 'react-native';
import { VehileMap } from './index';


export type Coordinate = {
  latitude: number;
  longitude: number;
};

export type Waypoint = Coordinate & {
  name?: string;
  /**
   * Indicates whether the `onArrive` event is triggered when reaching the waypoint effectively.
   * @Default true
   */
  separatesLegs?: boolean;
};

export type WaypointEvent = Coordinate & {
  /**
   * Name of Waypoint if provided or index of legs/waypoint
   * @available iOS
   **/
  name?: string;
  /**
   * Index of legs/waypoint
   * @available Android
   **/
  index?: number;
};

export type Location = {
  latitude: number;
  longitude: number;
  heading: number;
  accuracy: number;
};

export type NativeEvent<T> = {
  nativeEvent: T;
};

export type RouteProgress = {
  distanceTraveled: number;
  durationRemaining: number;
  fractionTraveled: number;
  distanceRemaining: number;
};

export type MapboxEvent = {
  message?: string;
};

export type NativeEventsProps = {
  onLocationChange?: (event: NativeEvent<Location>) => void;
  onEmbeddingChange?: (event: NativeEvent<{ embedding: boolean }>) => void;
  onRouteProgressChange?: (event: NativeEvent<RouteProgress>) => void;
  onManeuversUpdate?: (event: NativeEvent<any>) => void;
  onError?: (event: NativeEvent<MapboxEvent>) => void;
  onCancelNavigation?: (event: NativeEvent<MapboxEvent>) => void;
  onArrive?: (event: NativeEvent<WaypointEvent>) => void;
  onNavigationCameraState?: (event: NativeEvent<{ follow: boolean }>) => void;
};

export interface MapboxNavigationProps {
  style?: StyleProp<ViewStyle>;
  mute?: boolean;
  visibleSound?: boolean;
  visibleRouteOverview?: boolean;
  visibleTripProgressCard?: boolean;
  visibleManeuver?: boolean;
  visibleCenter?: boolean;
  showCancelButton?: boolean;
  startOrigin: Coordinate;
  waypoints?: Waypoint[];
  separateLegs?: boolean;
  destination: Coordinate & { title?: string };
  language?: 'vi' | 'en';
  distanceUnit?: 'metric' | 'imperial';
  /**
   * [iOS only]
   * @Default false
   */
  showsEndOfRouteFeedback?: boolean;

  /**
   * Hide status of bar on navigation [iOS only]
   * @Default false
   */
  hideStatusView?: boolean;

  /**
   * Location simulation for debug.
   * @Default false
   * @available iOS
   * @android Planned for next release
   */
  shouldSimulateRoute?: boolean;

  onLocationChange?: (location: Location) => void;
  onManeuversUpdate?: (data: { maneuvers: any[] }) => void;
  onRouteProgressChange?: (progress: RouteProgress) => void;
  onError?: (error: MapboxEvent) => void;
  onCancelNavigation?: (event: MapboxEvent) => void;
  onEmbeddingChange?: (event: { embedding: boolean }) => void;
  onArrive?: (point: WaypointEvent) => void;
  vehicle?: VehileMap
}
