import * as React from 'react';

import type { Permission, TextStyle, TouchableOpacityProps, ViewStyle } from 'react-native';
import {
  Button,
  findNodeHandle,
  PermissionsAndroid,
  Platform,
  StyleSheet,
  Text,
  UIManager,
  View,
  TouchableOpacity,
  Image
} from 'react-native';

import type { MapboxNavigationProps } from './types';
import MapboxNavigationView from './MapboxNavigationViewNativeComponent';
const permissions: Array<Permission> =
  Platform.OS === 'android' && Platform.Version >= 33
    ? [
      'android.permission.ACCESS_FINE_LOCATION',
      'android.permission.POST_NOTIFICATIONS',
    ]
    : ['android.permission.ACCESS_FINE_LOCATION'];

type MapboxNavigationState = {
  prepared: boolean;
  error?: string;
  follow: boolean
  embedding: boolean
};

class MapboxNavigation extends React.Component<
  MapboxNavigationProps,
  MapboxNavigationState
> {
  mapRef = React.createRef<any>();
  constructor(props: MapboxNavigationProps) {
    super(props);
    this.createState();
  }

  createState() {
    this.state = { prepared: false, follow: true, embedding: false };
  }

  componentDidMount(): void {
    if (Platform.OS === 'android') {
      this.requestPermission();
    } else {
      this.setState({ prepared: true });
    }
  }
  componentWillUnmount(): void {
    // if (Platform.OS === 'android') {
    this.dispatchViewManagerCommand('destroy');
    // }
  }
  async requestPermission() {
    try {
      let result = await PermissionsAndroid.requestMultiple(permissions);
      type ResultKey = keyof typeof result;
      if (
        result[permissions[0] as ResultKey] ===
        PermissionsAndroid.RESULTS.GRANTED
      ) {
        this.setState({ prepared: true });
      } else {
        const errorMessage = 'Permission is not granted.';
        this.setState({ error: errorMessage });
      }
      if (
        permissions.length > 1 &&
        result[permissions[1] as ResultKey] !==
        PermissionsAndroid.RESULTS.GRANTED
      ) {
        const errorMessage = 'Notification permission is not granted.';
        console.warn(errorMessage);

        this.props.onError?.({ message: errorMessage });
      }
    } catch (e) {
      const error = e as Error;
      this.setState({ error: error.message });
      console.warn('[Mapbox Navigation] ' + error.message);
      this.props.onError?.({ message: error.message });
    }
  }
  initNavigation = () => {
    this.dispatchViewManagerCommand('initNavigation');
  };
  cameraToOverview = () => {
    this.dispatchViewManagerCommand('cameraToOverview');
  }
  cameraToFollowing = () => {
    this.dispatchViewManagerCommand('cameraToFollowing');
  }
  dispatchViewManagerCommand = (name: string) => {
    try {
      const viewId = findNodeHandle(this.mapRef.current);
      if (!viewId) return
      if (Platform.OS == 'android') {
        UIManager.dispatchViewManagerCommand(
          viewId,
          UIManager.getViewManagerConfig('MapboxNavigationView').Commands[name],
          [],
        );
      }
      if (Platform.OS == 'ios') {

        // UIManager.dispatchViewManagerCommand(
        //   viewId,
        //   UIManager.getViewManagerConfig('MapboxNavigationView').Commands.callNativeMethod,
        //   [],
        // );
        // UIManager.dispatchViewManagerCommand(
        //   viewId,
        //   UIManager.getViewManagerConfig('MapboxNavigationView').Commands.receiveCommand,
        //   [],
        // );
        if (name === "initNavigation") return
        UIManager.dispatchViewManagerCommand(
          viewId,
          UIManager.getViewManagerConfig('MapboxNavigationView').Commands[name],
          [],
        );
      }
    } catch (error) {
      console.log(error);
    }
  };
  render() {
    if (!this.state.prepared) {
      const overiteViewStyle: ViewStyle = {
        justifyContent: 'center',
        alignItems: 'center',
      };
      const overiteTextStyle: TextStyle = this.state.error
        ? { color: 'red' }
        : {};
      return (
        <View style={[this.props.style, overiteViewStyle]}>
          <Text style={[styles.message, overiteTextStyle]}>Loading...</Text>
        </View>
      );
    }
    const {
      startOrigin,
      destination,
      style,
      distanceUnit = 'metric',
      onArrive,
      onLocationChange,
      onRouteProgressChange,
      onManeuversUpdate,
      onCancelNavigation,
      onEmbeddingChange,
      onError,
      ...rest
    } = this.props;

    const { follow, embedding } = this.state

    return (
      <View style={style}>
        <MapboxNavigationView
          ref={this.mapRef}
          style={styles.mapbox}
          distanceUnit={distanceUnit}
          startOrigin={[startOrigin.longitude, startOrigin.latitude]}
          destinationTitle={destination.title}
          destination={[destination.longitude, destination.latitude]}
          onLocationChange={(event) => onLocationChange?.(event.nativeEvent)}
          onRouteProgressChange={(event) =>
            onRouteProgressChange?.(event.nativeEvent)
          }
          onManeuversUpdate={(event) =>
            onManeuversUpdate?.(event.nativeEvent)
          }
          onError={(event) => onError?.(event.nativeEvent)}
          onArrive={(event) => onArrive?.(event.nativeEvent)}
          onCancelNavigation={(event) =>
            onCancelNavigation?.(event.nativeEvent)
          }
          onEmbeddingChange={(event) => {
            this.setState({ embedding: Boolean(event.nativeEvent.embedding) })
            onEmbeddingChange?.(event.nativeEvent)
          }}
          onNavigationCameraState={(event) => {
            // this.props.onNavigationCameraState?.(event.nativeEvent)
            console.log('onNavigationCameraState', event.nativeEvent)
            this.setState({ follow: event.nativeEvent.follow })
          }}
          {...rest}
        />
        {!embedding &&
          <View style={{ position: 'absolute', bottom: 80, right: 10, zIndex: 999, }}  >
            <ButtonIcon visible={!follow} onPress={() => this.cameraToFollowing()} icon={require('./assets/compass.png')} />
            <ButtonIcon visible={follow} onPress={() => this.cameraToOverview()} icon={require('./assets/overview.png')} />
          </View>
        }
      </View>
    );
  }
}
type ButtonIconProps = {
  onPress: () => void;
  icon: any,
  size?: number,
  visible?: boolean
  style?: TouchableOpacityProps['style']
}
const ButtonIcon = ({ onPress, icon, size = 25, ...props }: ButtonIconProps) => {
  if (props.visible === false) return null
  return (
    <TouchableOpacity
      onPress={onPress}
      style={[{
        backgroundColor: "white",
        borderRadius: 100,
        padding: size * 0.5,
        marginBottom: 5,
        borderWidth: 1,
        borderColor: '#eee',
      }, props.style]}
    >
      <Image source={icon} style={{ width: size, height: size, tintColor: 'red' }} resizeMode='contain' />
    </TouchableOpacity>
  )
};
const styles = StyleSheet.create({
  mapbox: {
    flex: 1,
  },
  message: {
    textAlign: 'center',
    fontSize: 16,
  },
});

export default MapboxNavigation;
