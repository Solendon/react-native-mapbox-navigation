package com.mapboxnavigation

import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.annotations.ReactProp
import com.mapbox.geojson.Point
import android.util.Log
//

@ReactModule(name = MapboxNavigationViewManager.NAME)
class MapboxNavigationViewManager(private var reactContext: ReactApplicationContext) :
    MapboxNavigationViewManagerSpec<MapboxNavigationView>() {
    override fun getName(): String {
        return NAME
    }

    public override fun createViewInstance(context: ThemedReactContext): MapboxNavigationView {
        return MapboxNavigationView(context)
    }
    override fun onAfterUpdateTransaction(view: MapboxNavigationView) {
        super.onAfterUpdateTransaction(view)
        try{
            Log.e(NAME, "onAfterUpdateTransaction.initNavigation")
            view.initNavigation()
        }catch (e: Exception) {
            Log.e(NAME, "❌ MapboxNavigationView Error initNavigation", e)
        }
    }
    override fun onDropViewInstance(view: MapboxNavigationView) {
        try {
            val ctx = view.context
            Log.i(NAME, "🧹 onDropViewInstance() called for ${ctx::class.java.simpleName}")

            // Cleanup regardless of context type
            view.cleanupAndDestroy()
        } catch (e: Exception) {
            Log.e(NAME, "❌ Error during onDropViewInstance", e)
        }
        super.onDropViewInstance(view)
    }

    override fun getExportedCustomDirectEventTypeConstants(): MutableMap<String, Map<String, String>> {
        val key = "registrationName"
        return mutableMapOf(
            "onLocationChange" to mapOf(key to "onLocationChange"),
            "onError" to mapOf(key to "onError"),
            "onCancelNavigation" to mapOf(key to "onCancelNavigation"),
            "onArrive" to mapOf(key to "onArrive"),
            "onRouteProgressChange" to mapOf(key to "onRouteProgressChange"),
            "onManeuversUpdate" to mapOf(key to "onManeuversUpdate"),
            "onNavigationCameraState" to mapOf(key to "onNavigationCameraState"),
            "onEmbeddingChange" to mapOf(key to "onEmbeddingChange"),
        )
    }

    override fun getCommandsMap(): MutableMap<String, Int> {
        return mutableMapOf(
            "initNavigation" to INIT_NAVIGATION,
            "cameraToOverview" to CAMERA_TO_OVERVIEW,
            "cameraToFollowing" to CAMERA_TO_FOLLOWING,
            "destroy" to DESTROY

        )
    }
    override fun receiveCommand(view: MapboxNavigationView, commandId: Int, args: ReadableArray?) {
        when (commandId) {
            INIT_NAVIGATION -> {
                view.initNavigation()
            }

            CAMERA_TO_OVERVIEW -> {
                view.cameraToOverview()
            }

            CAMERA_TO_FOLLOWING -> {
                view.cameraToFollowing()
            }

            DESTROY -> {
                try {
                    view.onDropViewInstance()
                } catch (e: Exception) {
                    Log.e(NAME, "❌ Error destroying native Mapbox view", e)
                }
            }

        }
    }

    companion object {
        const val NAME = "MapboxNavigationView"
        const val INIT_NAVIGATION = 1
        const val CAMERA_TO_OVERVIEW = 2
        const val CAMERA_TO_FOLLOWING = 3
        const val DESTROY = 4
    }




    @ReactProp(name = "vehicle")
    override fun vehicle(view: MapboxNavigationView?, value: Int?) {
        when (value) {
            Vehicle.CAR, Vehicle.MOTO -> view?.setVehicle(value)
            else -> view?.setVehicle(Vehicle.CAR)
        }
    }
    @ReactProp(name = "startOrigin")
    override fun setStartOrigin(view: MapboxNavigationView?, value: ReadableArray?) {
        Log.e("@@@@","MapboxNavigationViewManager.setStartOrigin:"+value?.toArrayList().toString())
        if (value == null) {
            view?.setStartOrigin(null)
            return
        }
        view?.setStartOrigin(Point.fromLngLat(value.getDouble(0), value.getDouble(1)))
    }
    @ReactProp(name = "destination")
    override fun setDestination(view: MapboxNavigationView?, value: ReadableArray?) {
        Log.e("@@@@","MapboxNavigationViewManager.setDestination:"+value?.toArrayList().toString())
        if (value == null) {
            view?.setDestination(null)
            return
        }
        view?.setDestination(Point.fromLngLat(value.getDouble(0), value.getDouble(1)))
    }

    @ReactProp(name = "destinationTitle")
    override fun setDestinationTitle(view: MapboxNavigationView?, value: String?) {
        if (value != null) {
            view?.setDestinationTitle(value)
        }
    }

    @ReactProp(name = "distanceUnit")
    override fun setDirectionUnit(view: MapboxNavigationView?, value: String?) {
        if (value != null) {
            view?.setDirectionUnit(value)
        }
    }

    @ReactProp(name = "waypoints")
    override fun setWaypoints(view: MapboxNavigationView?, value: ReadableArray?) {
        if (value == null) {
            view?.setWaypoints(listOf())
            return
        }
        val legs = mutableListOf<WaypointLegs>()
        val waypoints: List<Point> = value.toArrayList().mapIndexedNotNull { index, item ->
            val map = item as? Map<*, *>
            val latitude = map?.get("latitude") as? Double
            val longitude = map?.get("longitude") as? Double
            val name = map?.get("name") as? String
            val separatesLegs = map?.get("separatesLegs") as? Boolean
            if (separatesLegs != false) {
                legs.add(WaypointLegs(index = index + 1, name = name ?: "waypoint-$index"))
            }
            if (latitude != null && longitude != null) {
                Point.fromLngLat(longitude, latitude)
            } else {
                null
            }
        }
        view?.setWaypointLegs(legs)
        view?.setWaypoints(waypoints)
    }

    @ReactProp(name = "language")
    override fun setLocal(view: MapboxNavigationView?, language: String?) {
        if (language !== null) {
            view?.setLocal(language)
        }
    }

    @ReactProp(name = "showCancelButton")
    override fun setShowCancelButton(view: MapboxNavigationView?, value: Boolean) {
//        view?.setShowCancelButton(value)
    }

    @ReactProp(name = "mute")
    override fun setMute(view: MapboxNavigationView?, value: Boolean) {
        view?.setMute(value)
    }

    @ReactProp(name = "visibleSound")
    fun setVisibleSound(view: MapboxNavigationView?, value: Boolean?) {
        if (value != null) {
            view?.setVisibleSound(value)
        }

    }

    @ReactProp(name = "visibleCenter")
    fun setVisibleCenter(view: MapboxNavigationView?, value: Boolean?) {
        if (value != null) {
            view?.setVisibleCenter(value)
        }

    }

    @ReactProp(name = "visibleRouteOverview")
    fun setVisibleRouteOverview(view: MapboxNavigationView?, value: Boolean) {
        if (value != null) {
            view?.setVisibleRouteOverview(value)
        }
    }

    @ReactProp(name = "visibleTripProgressCard")
    fun setVisibleTripProgressCard(view: MapboxNavigationView?, value: Boolean) {
        if (value != null) {
            view?.setVisibleTripProgressCard(value)
        }
    }

    @ReactProp(name = "visibleManeuver")
    fun setVisibleManeuver(view: MapboxNavigationView?, value: Boolean) {
        if (value != null) {
            view?.setVisibleManeuver(value)
        }
    }

}
