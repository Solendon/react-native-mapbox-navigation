
import React, { useEffect, useImperativeHandle, useRef, useState } from 'react';
import MapboxNavigation from '../src/MapboxNavigation';
import { DeviceEventEmitter, Image, SafeAreaView, StyleSheet, Text, View } from 'react-native';
enum PuckIcon {
  XE_MAY = 1,
  OTO = 2
}
export default function App() {
  console.log('App')
  const refBottom = useRef<any>(null)
  const refTop = useRef<any>(null)

  const [location, setLocation] = useState<any>({
    latitude: 21.0245,
    longitude: 105.8531

  })
  const mapRef = useRef<MapboxNavigation>(null)
  return (
    <SafeAreaView style={styles.container}>
      <MapboxNavigation
        ref={mapRef}
        startOrigin={{
          latitude: location.latitude,
          longitude: location.longitude
        }}
        destination={{
          latitude: 21.0589,
          longitude: 105.8334
        }}
        // waypoints={[
        //   { "latitude": 21.0245067, "longitude": 105.8531333 },
        //   { "latitude": 21.058945, "longitude": 105.8334467 },
        // ]}
        style={styles.container}
        shouldSimulateRoute={false}
        showCancelButton={false}
        language="vi"
        puckIcon={PuckIcon.OTO}
        // onLocationChange={(e) => console.info("onLocationChange", e)}
        onRouteProgressChange={(e) => {
          // console.log('onRouteProgressChange', e)
          refBottom.current?.setRoute(e)
        }}
        onError={(e) => console.info("onError", e)}
        onArrive={(e) => console.info("onArrive", e)}
        onCancelNavigation={(e) => console.info("onCancelNavigation", e)}
        mute={false}
        visibleSound={false}
        visibleRouteOverview={false}
        visibleTripProgressCard={false}
        // visibleManeuver={false}
        visibleCenter={false}
        onLocationChange={event => {
          // console.log('onLocationChange', event)
          // setLocation(event)
        }}
        onManeuversUpdate={data => {
          // console.log('onManeuversUpdate', data.maneuvers[0])
          refTop.current?.setManeuvers(data.maneuvers ?? [])
        }}
      />
      <BottomView ref={refBottom} />
      {/* <TopView ref={refTop} /> */}
    </SafeAreaView>
  );
}

const BottomView = React.forwardRef((prop, ref) => {
  React.useImperativeHandle(ref, () => ({
    setRoute
  }))
  const [route, setRoute] = useState({
    "distanceRemaining": 0,
    "fractionTraveled": 0,
    "durationRemaining": 0,
    "distanceTraveled": 0
  })
  return (
    <View style={styles.bottom}>
      <Text style={{ fontSize: 16, fontWeight: '600' }}>{route?.distanceRemaining}</Text>
      <Text>{'Tuyến đường tốt nhất rành cho bạn'}</Text>
    </View>
  )
})

type IManeuvers = {
  primary: {
    text: string,
    id: string,
    type: string,
    drivingSide: string,
    modifier: string
  },
  sub: {
    text: string,
    id: string
  },
  maneuverPoint: {
    latitude: number,
    longitude: number
  },
  stepDistance: {
    distanceRemaining: number,
    totalDistance: number
  },
  test?: number,
  laneGuidance?: any
}

const TopView = React.forwardRef((prop, ref) => {
  React.useImperativeHandle(ref, () => ({
    setManeuvers
  }))
  const [maneuvers, setManeuvers] = useState<IManeuvers[]>([])
  const maneuver = maneuvers.length > 0 ? unflattenObject(maneuvers[0]) as IManeuvers : null
  console.info('maneuver', maneuver)
  // console.info('maneuvers', maneuvers.map(item => unflattenObject(item)))
  // console.info('maneuvers.length', maneuvers.length)
  return (
    <View style={styles.bottom}>
      <Text style={{ color: 'red' }}>{maneuver?.primary?.text}</Text>
      <Text style={{ color: 'red' }}>{maneuver?.primary?.modifier}</Text>
      <Text >{maneuver?.stepDistance.distanceRemaining}</Text>
    </View>
  )
})
const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  bottom: {
    minHeight: 50,
    backgroundColor: '#fff',
    borderTopLeftRadius: 20,
    borderTopRightRadius: 20,
    marginTop: -20,
    padding: 20,
    borderWidth: 1,
  }
});
function formatDistance(meters: number) {
  if (meters < 1000) {
    return `${meters.toFixed(0)} m`;
  } else {
    return `${(meters / 1000).toFixed(2)} km`;
  }
}

import { set } from "lodash";
import { Block, Icon, Touch } from 'ui-rn';

function unflattenObject(obj: Record<string, any>) {
  const result = {};
  Object.entries(obj).forEach(([key, value]) => {
    set(result, key, value);
  });
  return result;
}
export const getTurnIcon = (action: string) => {
  switch (action) {
    case 'straight': // đi thẳng
      return 'straight';
    case 'right': // rẽ phải
      return 'turn-right';
    case 'left': // rẽ trái
      return 'turn-left';
    case 'slight left': // rẽ hơi trái
      return 'turn-slight-left';
    case 'slight right': // rẽ hơi phải
      return 'turn-slight-right';
    case 'uturn': // rẽ hơi phải
      return 'u-turn-left';
    default:
      return '';
  }
};