package com.mapboxnavigation

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.events.RCTEventEmitter
import com.facebook.react.uimanager.events.RCTModernEventEmitter
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.bindgen.Expected
import com.mapbox.common.location.Location
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.ImageHolder
import com.mapbox.maps.ViewAnnotationAnchor
import com.mapbox.maps.ViewAnnotationOptions
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.compass.compass
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.viewannotation.annotationAnchor
import com.mapbox.maps.viewannotation.annotationAnchors
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import com.mapbox.navigation.base.TimeFormat
import com.mapbox.navigation.base.extensions.applyDefaultNavigationOptions
import com.mapbox.navigation.base.extensions.applyLanguageAndVoiceUnitOptions
import com.mapbox.navigation.base.formatter.DistanceFormatterOptions
import com.mapbox.navigation.base.formatter.UnitType
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.base.route.NavigationRoute
import com.mapbox.navigation.base.route.NavigationRouterCallback
import com.mapbox.navigation.base.route.RouterFailure
import com.mapbox.navigation.base.route.RouterOrigin
import com.mapbox.navigation.base.trip.model.RouteLegProgress
import com.mapbox.navigation.base.trip.model.RouteProgress
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.MapboxNavigationProvider
import com.mapbox.navigation.core.arrival.ArrivalObserver
import com.mapbox.navigation.core.directions.session.RoutesObserver
import com.mapbox.navigation.core.formatter.MapboxDistanceFormatter
import com.mapbox.navigation.core.trip.session.LocationMatcherResult
import com.mapbox.navigation.core.trip.session.LocationObserver
import com.mapbox.navigation.core.trip.session.RouteProgressObserver
import com.mapbox.navigation.core.trip.session.VoiceInstructionsObserver
import com.mapbox.navigation.tripdata.maneuver.api.MapboxManeuverApi
import com.mapbox.navigation.tripdata.maneuver.model.Maneuver
import com.mapbox.navigation.tripdata.maneuver.model.ManeuverError
import com.mapbox.navigation.tripdata.progress.api.MapboxTripProgressApi
import com.mapbox.navigation.tripdata.progress.model.DistanceRemainingFormatter
import com.mapbox.navigation.tripdata.progress.model.EstimatedTimeToArrivalFormatter
import com.mapbox.navigation.tripdata.progress.model.PercentDistanceTraveledFormatter
import com.mapbox.navigation.tripdata.progress.model.TimeRemainingFormatter
import com.mapbox.navigation.tripdata.progress.model.TripProgressUpdateFormatter
import com.mapbox.navigation.ui.base.util.MapboxNavigationConsumer
import com.mapbox.navigation.ui.components.maneuver.model.ManeuverPrimaryOptions
import com.mapbox.navigation.ui.components.maneuver.model.ManeuverSecondaryOptions
import com.mapbox.navigation.ui.components.maneuver.model.ManeuverSubOptions
import com.mapbox.navigation.ui.components.maneuver.model.ManeuverViewOptions
import com.mapbox.navigation.ui.components.maneuver.view.MapboxManeuverView
import com.mapbox.navigation.ui.components.tripprogress.view.MapboxTripProgressView
import com.mapbox.navigation.ui.maps.NavigationStyles
import com.mapbox.navigation.ui.maps.camera.NavigationCamera
import com.mapbox.navigation.ui.maps.camera.data.MapboxNavigationViewportDataSource
import com.mapbox.navigation.ui.maps.camera.lifecycle.NavigationBasicGesturesHandler
import com.mapbox.navigation.ui.maps.camera.state.NavigationCameraState
import com.mapbox.navigation.ui.maps.camera.transition.NavigationCameraTransitionOptions
import com.mapbox.navigation.ui.maps.location.NavigationLocationProvider
import com.mapbox.navigation.ui.maps.route.RouteLayerConstants.TOP_LEVEL_ROUTE_LINE_LAYER_ID
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowApi
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowView
import com.mapbox.navigation.ui.maps.route.arrow.model.RouteArrowOptions
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineApiOptions
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineViewOptions
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineColorResources
import com.mapbox.navigation.voice.api.MapboxSpeechApi
import com.mapbox.navigation.voice.api.MapboxVoiceInstructionsPlayer
import com.mapbox.navigation.voice.model.SpeechAnnouncement
import com.mapbox.navigation.voice.model.SpeechError
import com.mapbox.navigation.voice.model.SpeechValue
import com.mapbox.navigation.voice.model.SpeechVolume
import com.mapboxnavigation.databinding.NavigationViewBinding

import java.util.Locale

@Suppress("DEPRECATION")
@SuppressLint("ViewConstructor")
class MapboxNavigationView(private val context: ThemedReactContext) :
    FrameLayout(context.baseContext) {
    private companion object {
        private const val BUTTON_ANIMATION_DURATION = 1500L
    }

    private var TAG: String = "Mapbox_View"
    private var origin: Point? = null
    private var originTitle: String = "Điểm bắt đầu"
    private var destination: Point? = null
    private var destinationTitle: String = "Điểm đến"

    private var waypoints: List<Point> = listOf()
    private var waypointLegs: List<WaypointLegs> = listOf()
    private var distanceUnit: String = DirectionsCriteria.METRIC
    private var locale = Locale("vi", "VN")

    private var puckIcon: Int = R.drawable.ic_car
    private var vehicle: Int = Vehicle.CAR
    private var visibleSound: Int = View.VISIBLE
    private var visibleRouteOverview: Int = View.VISIBLE
    private var visibleTripProgressCard: Int = View.VISIBLE
    private var visibleManeuver: Int = View.VISIBLE
    private var visibleCenter: Int = View.VISIBLE
    private var embedding: Boolean = false
        set(value) {
            field = value
            val params = Arguments.createMap()
            params.putBoolean("embedding", value)
            context
                .getJSModule(RCTEventEmitter::class.java)
                .receiveEvent(id, "onEmbeddingChange", params)

        }
    private var isActive = true

    fun setVisibleSound(visible: Boolean) {
        this.visibleSound = if (visible) View.VISIBLE else View.GONE
    }

    fun setVisibleCenter(visible: Boolean) {
        this.visibleCenter = if (visible) View.VISIBLE else View.GONE
    }

    fun setVisibleRouteOverview(visible: Boolean) {
        this.visibleRouteOverview = if (visible) View.VISIBLE else View.GONE
    }

    fun setVisibleTripProgressCard(visible: Boolean) {
        this.visibleTripProgressCard = if (visible) View.VISIBLE else View.GONE
    }

    fun setVisibleManeuver(visible: Boolean) {
        this.visibleManeuver = if (visible) View.VISIBLE else View.GONE
    }

    /**
     * Bindings to the example layout.
     */
    private var binding: NavigationViewBinding =
        NavigationViewBinding.inflate(LayoutInflater.from(context), this, true)

    /**
     * Produces the camera frames based on the location and routing data for the [navigationCamera] to execute.
     */
    private var viewportDataSource = MapboxNavigationViewportDataSource(binding.mapView.mapboxMap)

    /**
     * Used to execute camera transitions based on the data generated by the [viewportDataSource].
     * This includes transitions from route overview to route following and continuously updating the camera as the location changes.
     */
    private var navigationCamera = NavigationCamera(
        binding.mapView.mapboxMap,
        binding.mapView.camera,
        viewportDataSource
    )

    /**
     * Mapbox Navigation entry point. There should only be one instance of this object for the app.
     * You can use [MapboxNavigationProvider] to help create and obtain that instance.
     */
    private var mapboxNavigation: MapboxNavigation? = null

    /*
     * Below are generated camera padding values to ensure that the route fits well on screen while
     * other elements are overlaid on top of the map (including instruction view, buttons, etc.)
     */
    private val pixelDensity = Resources.getSystem().displayMetrics.density
    private val overviewPadding: EdgeInsets by lazy {
        EdgeInsets(
            140.0 * pixelDensity,
            40.0 * pixelDensity,
            120.0 * pixelDensity,
            40.0 * pixelDensity
        )
    }
    private val landscapeOverviewPadding: EdgeInsets by lazy {
        EdgeInsets(
            30.0 * pixelDensity,
            380.0 * pixelDensity,
            110.0 * pixelDensity,
            20.0 * pixelDensity
        )
    }
    private val followingPadding: EdgeInsets by lazy {
        EdgeInsets(
            180.0 * pixelDensity,
            40.0 * pixelDensity,
            150.0 * pixelDensity,
            40.0 * pixelDensity
        )
    }
    private val landscapeFollowingPadding: EdgeInsets by lazy {
        EdgeInsets(
            30.0 * pixelDensity,
            380.0 * pixelDensity,
            110.0 * pixelDensity,
            40.0 * pixelDensity
        )
    }

    /**
     * Generates updates for the [MapboxManeuverView] to display the upcoming maneuver instructions
     * and remaining distance to the maneuver point.
     */
    private lateinit var maneuverApi: MapboxManeuverApi

    /**
     * Generates updates for the [MapboxTripProgressView] that include remaining time and distance to the destination.
     */
    private lateinit var tripProgressApi: MapboxTripProgressApi

    /**
     * Stores and updates the state of whether the voice instructions should be played as they come or muted.
     */
    private var isVoiceInstructionsMuted = false
        set(value) {
            field = value
            if (value) {
                binding.soundButton.muteAndExtend(BUTTON_ANIMATION_DURATION)
                voiceInstructionsPlayer?.volume(SpeechVolume(0f))
            } else {
                binding.soundButton.unmuteAndExtend(BUTTON_ANIMATION_DURATION)
                voiceInstructionsPlayer?.volume(SpeechVolume(1f))
            }
        }

    /**
     * Extracts message that should be communicated to the driver about the upcoming maneuver.
     * When possible, downloads a synthesized audio file that can be played back to the driver.
     */
    private lateinit var speechApi: MapboxSpeechApi

    /**
     * Plays the synthesized audio files with upcoming maneuver instructions
     * or uses an on-device Text-To-Speech engine to communicate the message to the driver.
     * NOTE: do not use lazy initialization for this class since it takes some time to initialize
     * the system services required for on-device speech synthesis. With lazy initialization
     * there is a high risk that said services will not be available when the first instruction
     * has to be played. [MapboxVoiceInstructionsPlayer] should be instantiated in
     * `Activity#onCreate`.
     */
    private var voiceInstructionsPlayer: MapboxVoiceInstructionsPlayer? = null

    /**
     * Observes when a new voice instruction should be played.
     */
    private val voiceInstructionsObserver = VoiceInstructionsObserver { voiceInstructions ->
        speechApi.generate(voiceInstructions, speechCallback)
    }

    /**
     * Based on whether the synthesized audio file is available, the callback plays the file
     * or uses the fall back which is played back using the on-device Text-To-Speech engine.
     */
    private val speechCallback =
        MapboxNavigationConsumer<Expected<SpeechError, SpeechValue>> { expected ->
            expected.fold(
                { error ->
                    // play the instruction via fallback text-to-speech engine
//                    voiceInstructionsPlayer?.play(
//                        error.fallback,
//                        voiceInstructionsPlayerCallback
//                    )
                    VietnameseTTS.speak(error.fallback.announcement)
                },
                { value ->
                    // play the sound file from the external generator
//                    voiceInstructionsPlayer?.play(
//                        value.announcement,
//                        voiceInstructionsPlayerCallback
//                    )
                    Log.e(TAG, "MapboxNavigationConsumer-speak:" + value.announcement.announcement)
                    VietnameseTTS.speak(value.announcement.announcement)
                }
            )
        }

    /**
     * When a synthesized audio file was downloaded, this callback cleans up the disk after it was played.
     */
    private val voiceInstructionsPlayerCallback =
        MapboxNavigationConsumer<SpeechAnnouncement> { value ->
            // remove already consumed file to free-up space
            speechApi.clean(value)
        }

    /**
     * [NavigationLocationProvider] is a utility class that helps to provide location updates generated by the Navigation SDK
     * to the Maps SDK in order to update the user location indicator on the map.
     */
    private val navigationLocationProvider = NavigationLocationProvider()
    val traveledColor = ContextCompat.getColor(context, R.color.maneuver_background)

    /**
     * RouteLine: Additional route line options are available through the
     * [MapboxRouteLineViewOptions] and [MapboxRouteLineApiOptions].
     * Notice here the [MapboxRouteLineViewOptions.routeLineBelowLayerId] option. The map is made up of layers. In this
     * case the route line will be placed below the "road-label" layer which is a good default
     * for the most common Mapbox navigation related maps. You should consider if this should be
     * changed for your use case especially if you are using a custom map style.
     */


    private val routeLineViewOptions: MapboxRouteLineViewOptions by lazy {
        val colorLine = Color.parseColor("#00e704")
        MapboxRouteLineViewOptions.Builder(context)
            .routeLineColorResources(
                RouteLineColorResources.Builder()
                    .routeLineTraveledColor(colorLine)// màu đoạn đã đi
                    .routeDefaultColor(colorLine) // màu tuyến chính
                    // .routeCasingColor(colorLine)// viền
                    .restrictedRoadColor(colorLine)// đường cấm
                    .routeLowCongestionColor(colorLine)// giao thông thấp
                    .routeModerateCongestionColor(colorLine) // trung bình
                    .routeHeavyCongestionColor(colorLine) // giao thông nặng
                    .routeSevereCongestionColor(colorLine)
                    .routeUnknownCongestionColor(colorLine)
                    .build()
            )
            .routeLineBelowLayerId("road-label-navigation")
            .build()
    }

    private val routeLineApiOptions: MapboxRouteLineApiOptions by lazy {
        MapboxRouteLineApiOptions.Builder()
            .build()
    }

    /**
     * RouteLine: This class is responsible for rendering route line related mutations generated
     * by the [routeLineApi]
     */
    private val routeLineView by lazy {
        MapboxRouteLineView(routeLineViewOptions)
    }


    /**
     * RouteLine: This class is responsible for generating route line related data which must be
     * rendered by the [routeLineView] in order to visualize the route line on the map.
     */
    private val routeLineApi: MapboxRouteLineApi by lazy {
        MapboxRouteLineApi(routeLineApiOptions)
    }

    /**
     * RouteArrow: This class is responsible for generating data related to maneuver arrows. The
     * data generated must be rendered by the [routeArrowView] in order to apply mutations to
     * the map.
     */
    private val routeArrowApi: MapboxRouteArrowApi by lazy {
        MapboxRouteArrowApi()
    }

    /**
     * RouteArrow: Customization of the maneuver arrow(s) can be done using the
     * [RouteArrowOptions]. Here the above layer ID is used to determine where in the map layer
     * stack the arrows appear. Above the layer of the route traffic line is being used here. Your
     * use case may necessitate adjusting this to a different layer position.
     */
    private val routeArrowOptions by lazy {
        RouteArrowOptions.Builder(context)
            .withAboveLayerId(TOP_LEVEL_ROUTE_LINE_LAYER_ID)
            .build()
    }

    /**
     * RouteArrow: This class is responsible for rendering the arrow related mutations generated
     * by the [routeArrowApi]
     */
    private val routeArrowView: MapboxRouteArrowView by lazy {
        MapboxRouteArrowView(routeArrowOptions)
    }

    /**
     * Gets notified with location updates.
     *
     * Exposes raw updates coming directly from the location services
     * and the updates enhanced by the Navigation SDK (cleaned up and matched to the road).
     */
    private val locationObserver = object : LocationObserver {
        var firstLocationUpdateReceived = false

        override fun onNewRawLocation(rawLocation: Location) {
            // not handled
        }

        override fun onNewLocationMatcherResult(locationMatcherResult: LocationMatcherResult) {
            try {
//                Log.w(TAG, "⚠️ onNewLocationMatcherResult")


                val enhancedLocation = locationMatcherResult.enhancedLocation
//                Log.w(TAG, "⚠️ enhancedLocation.latitude"+enhancedLocation.latitude)
//                Log.w(TAG, "⚠️ enhancedLocation.longitude"+enhancedLocation.longitude)

                navigationLocationProvider.changePosition(
                    location = enhancedLocation,
                    keyPoints = locationMatcherResult.keyPoints,
                )

                // update camera position to account for new location
                viewportDataSource.onLocationChanged(enhancedLocation)
                viewportDataSource.evaluate()

                // if this is the first location update the activity has received,
                // it's best to immediately move the camera to the current user location
                if (!firstLocationUpdateReceived) {
                    firstLocationUpdateReceived = true
                    navigationCamera.requestNavigationCameraToOverview(
                        stateTransitionOptions = NavigationCameraTransitionOptions.Builder()
                            .maxDuration(0) // instant transition
                            .build()
                    )
                }

                val event = Arguments.createMap()
                event.putDouble("longitude", enhancedLocation.longitude)
                event.putDouble("latitude", enhancedLocation.latitude)
                event.putDouble("heading", enhancedLocation.bearing ?: 0.0)
                event.putDouble("accuracy", enhancedLocation.horizontalAccuracy ?: 0.0)
                context
                    .getJSModule(RCTEventEmitter::class.java)
                    .receiveEvent(id, "onLocationChange", event)
            } catch (e: Exception) {
                Log.e(TAG, "onNewLocationMatcherResult error", e)
            }

        }
    }

    fun translateManeuverTextToVietnamese(text: String): String {
        return text
            .replace("Destination", "Điểm đến", ignoreCase = true)
            .replace("on the left", "ở bên trái", ignoreCase = true)
            .replace("on the right", "ở bên phải", ignoreCase = true)
            .replace("Turn left", "Rẽ trái", ignoreCase = true)
            .replace("Turn right", "Rẽ phải", ignoreCase = true)
            .replace("Continue straight", "Đi thẳng", ignoreCase = true)
    }

    /**
     * Gets notified with progress along the currently active route.
     */
    private val routeProgressObserver = RouteProgressObserver { routeProgress ->
        try {

            if (routeProgress.route == null || routeProgress.currentLegProgress == null) return@RouteProgressObserver
            // update the camera position to account for the progressed fragment of the route
            if (routeProgress.fractionTraveled.toDouble() != 0.0) {
                viewportDataSource.onRouteProgressChanged(routeProgress)
            }
            viewportDataSource.evaluate()

            // draw the upcoming maneuver arrow on the map
            val style = binding.mapView.mapboxMap.style
            if (style != null) {
                val maneuverArrowResult = routeArrowApi.addUpcomingManeuverArrow(routeProgress)
                routeArrowView.renderManeuverUpdate(style, maneuverArrowResult)
            }

            // update top banner with maneuver instructions
            val maneuvers = maneuverApi.getManeuvers(routeProgress)
            maneuvers.fold(
                { error ->
                    Log.w("Maneuvers error:", error.throwable)
                },
                {
                    val maneuverViewOptions = ManeuverViewOptions.Builder()
                        .primaryManeuverOptions(
                            ManeuverPrimaryOptions.Builder()
                                .textAppearance(R.style.PrimaryManeuverTextAppearance)
                                .build()
                        )
                        .secondaryManeuverOptions(
                            ManeuverSecondaryOptions.Builder()
                                .textAppearance(R.style.ManeuverTextAppearance)
                                .build()
                        )
                        .subManeuverOptions(
                            ManeuverSubOptions.Builder()
                                .textAppearance(R.style.ManeuverTextAppearance)
                                .build()
                        )
                        .stepDistanceTextAppearance(R.style.StepDistanceRemainingAppearance)
                        .maneuverBackgroundColor(R.color.maneuver_background)
                        .subManeuverBackgroundColor(R.color.maneuver_background)
                        .build()

                    binding.maneuverView.visibility = visibleManeuver
                    binding.maneuverViewParent.visibility = visibleManeuver
                    binding.maneuverView.updateManeuverViewOptions(maneuverViewOptions)
                    binding.maneuverView.renderManeuvers(maneuvers)

                    maneuversToReact(context, maneuvers)

                }
            )

            // update bottom trip progress summary
            binding.tripProgressView.render(
                tripProgressApi.getTripProgress(routeProgress)
            )

            val event = Arguments.createMap()
            event.putDouble("distanceTraveled", routeProgress.distanceTraveled.toDouble())
            event.putDouble("durationRemaining", routeProgress.durationRemaining)
            event.putDouble("fractionTraveled", routeProgress.fractionTraveled.toDouble())
            event.putDouble("distanceRemaining", routeProgress.distanceRemaining.toDouble())
            context
                .getJSModule(RCTEventEmitter::class.java)
                .receiveEvent(id, "onRouteProgressChange", event)
        } catch (e: Exception) {
            Log.e(TAG, "routeProgressObserver error", e)
        }
    }

    /**
     * Gets notified whenever the tracked routes change.
     *
     * A change can mean:
     * - routes get changed with [MapboxNavigation.setNavigationRoutes]
     * - routes annotations get refreshed (for example, congestion annotation that indicate the live traffic along the route)
     * - driver got off route and a reroute was executed
     */
    private val routesObserver = RoutesObserver { routeUpdateResult ->
        if (routeUpdateResult.navigationRoutes.isNotEmpty()) {
            // generate route geometries asynchronously and render them
            routeLineApi.setNavigationRoutes(
                routeUpdateResult.navigationRoutes
            ) { value ->
                binding.mapView.mapboxMap.style?.apply {
                    routeLineView.renderRouteDrawData(this, value)
                }
            }
            if (isActive && binding.mapView.mapboxMap.isValid()) {
                viewportDataSource.onRouteChanged(routeUpdateResult.navigationRoutes.first())
                try {
                    viewportDataSource.evaluate()
                } catch (e: Exception) {
                    Log.w(TAG, "⚠️ viewportDataSource.evaluate() failed after destroy", e)
                }
            }
        } else {
            // remove the route line and route arrow from the map
            val style = binding.mapView.mapboxMap.style
            if (style != null) {
                routeLineApi.clearRouteLine { value ->
                    routeLineView.renderClearRouteLineValue(
                        style,
                        value
                    )
                }
                routeArrowView.render(style, routeArrowApi.clearArrows())
            }

            // remove the route reference from camera position evaluations
            viewportDataSource.clearRouteData()
            viewportDataSource.evaluate()
        }
    }

    init {
        onCreate()
    }

    private fun onCreate() {
        // initialize Mapbox Navigation
        mapboxNavigation = if (MapboxNavigationProvider.isCreated()) {
            MapboxNavigationProvider.retrieve()
        } else {
            MapboxNavigationProvider.create(
                NavigationOptions.Builder(context)
                    .build()
            )
        }
        VietnameseTTS.init(context) {
            VietnameseTTS.speak("Findnear đã sẵn sàng chỉ đường")
        }
    }

    fun cameraToOverview() {
        navigationCamera.requestNavigationCameraToOverview()
    }

    fun cameraToFollowing() {
        navigationCamera.requestNavigationCameraToFollowing()
    }

    @SuppressLint("MissingPermission")
    fun initNavigation() {
        if (origin == null || destination == null) {
            Log.e("@@@@", "MapboxNavigationView.initNavigation.origin:" + origin.toString())
            Log.e(
                "@@@@",
                "MapboxNavigationView.initNavigation.destination:" + destination.toString()
            )
            sendErrorToReact("origin and destination are required")
            return
        }
        Log.e(TAG, "initNavigation")
        // Recenter Camera
        val initialCameraOptions = CameraOptions.Builder()
            .zoom(15.0)
            .center(origin)
            .build()
        binding.mapView.mapboxMap.setCamera(initialCameraOptions)
        binding.mapView.compass.enabled = true

        // Start Navigation
        startNavigation()

        // set the animations lifecycle listener to ensure the NavigationCamera stops
        // automatically following the user location when the map is interacted with
        binding.mapView.camera.addCameraAnimationsLifecycleListener(
            NavigationBasicGesturesHandler(navigationCamera)
        )
        navigationCamera.registerNavigationCameraStateChangeObserver { navigationCameraState ->
            // shows/hide the recenter button depending on the camera state

            when (navigationCameraState) {
                NavigationCameraState.TRANSITION_TO_FOLLOWING,
                NavigationCameraState.FOLLOWING -> {
                    val event = Arguments.createMap()
                    event.putBoolean("follow", true)
                    context
                        .getJSModule(RCTEventEmitter::class.java)
                        .receiveEvent(id, "onNavigationCameraState", event)
                }

                NavigationCameraState.TRANSITION_TO_OVERVIEW,
                NavigationCameraState.OVERVIEW,
                NavigationCameraState.IDLE -> {
                    val event = Arguments.createMap()
                    event.putBoolean("follow", false)
                    context
                        .getJSModule(RCTEventEmitter::class.java)
                        .receiveEvent(id, "onNavigationCameraState", event)
                }
            }

        }
        // set the padding values depending on screen orientation and visible view layout
        if (this.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            viewportDataSource.overviewPadding = landscapeOverviewPadding
        } else {
            viewportDataSource.overviewPadding = overviewPadding
        }
        if (this.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            viewportDataSource.followingPadding = landscapeFollowingPadding
        } else {
            viewportDataSource.followingPadding = followingPadding
        }

        // make sure to use the same DistanceFormatterOptions across different features
        val unitType = if (distanceUnit == "imperial") UnitType.IMPERIAL else UnitType.METRIC
        val distanceFormatterOptions = DistanceFormatterOptions.Builder(context)
            .unitType(unitType)
            .build()

        // initialize maneuver api that feeds the data to the top banner maneuver view
        maneuverApi = MapboxManeuverApi(
            MapboxDistanceFormatter(distanceFormatterOptions)
        )

        // initialize bottom progress view
        tripProgressApi = MapboxTripProgressApi(
            TripProgressUpdateFormatter.Builder(context)
                .distanceRemainingFormatter(
                    DistanceRemainingFormatter(distanceFormatterOptions)
                )
                .timeRemainingFormatter(
                    TimeRemainingFormatter(context)
                )
                .percentRouteTraveledFormatter(
                    PercentDistanceTraveledFormatter()
                )
                .estimatedTimeToArrivalFormatter(
                    EstimatedTimeToArrivalFormatter(context, TimeFormat.NONE_SPECIFIED)
                )
                .build()
        )
        // initialize voice instructions api and the voice instruction player
        speechApi = MapboxSpeechApi(
            context,
            locale.language
        )
        voiceInstructionsPlayer = MapboxVoiceInstructionsPlayer(
            context,
            locale.language
        )

        // load map style
        binding.mapView.mapboxMap.loadStyle(NavigationStyles.NAVIGATION_DAY_STYLE) {
            // Ensure that the route line related layers are present before the route arrow
            routeLineView.initializeLayers(it)
        }
        // initialize view interactions


        binding.recenter.setOnClickListener {
            navigationCamera.requestNavigationCameraToFollowing()
            binding.routeOverview.showTextAndExtend(BUTTON_ANIMATION_DURATION)
        }
        binding.routeOverview.setOnClickListener {
            navigationCamera.requestNavigationCameraToOverview()
            binding.recenter.showTextAndExtend(BUTTON_ANIMATION_DURATION)
        }
        binding.soundButton.setOnClickListener {
            // mute/unmute voice instructions
            isVoiceInstructionsMuted = !isVoiceInstructionsMuted
        }

        // Check initial muted or not
        if (this.isVoiceInstructionsMuted) {
            binding.soundButton.mute()
            voiceInstructionsPlayer?.volume(SpeechVolume(0f))
        } else {
            binding.soundButton.unmute()
            voiceInstructionsPlayer?.volume(SpeechVolume(1f))
        }
    }

    private fun onDestroy() {
        try {
            Log.e(TAG, "onDestroy")
            maneuverApi.cancel()
            routeLineApi.cancel()
            routeLineView.cancel()
            speechApi.cancel()
            voiceInstructionsPlayer?.shutdown()
            mapboxNavigation?.stopTripSession()
            mapboxNavigation = null
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error cleaning MapboxNavigationView", e)
        }
        try {
            if (MapboxNavigationProvider.isCreated()) {
                MapboxNavigationProvider.destroy()
                mapboxNavigation = null
                Log.i(TAG, "✅ MapboxNavigationProvider destroyed")
            }

            // 3. Hủy mapView để dừng native threads
            binding.mapView?.onStop()
            binding.mapView?.onDestroy()

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error cleaning MapboxNavigationView", e)
        }

    }

    fun cleanupAndDestroy() {
        try {
            isActive = false
            Log.i(TAG, "🧹 cleanupAndDestroy() called")

            // 1️⃣ Dừng navigation session trước
            mapboxNavigation?.stopTripSession()

            // 2️⃣ Hủy các API liên quan
            maneuverApi.cancel()
            routeLineApi.cancel()
            routeLineView.cancel()
            speechApi.cancel()
            voiceInstructionsPlayer?.shutdown()

            // 3️⃣ Gỡ bỏ LocationPlugin (quan trọng!)
            try {
                binding.mapView?.location?.updateSettings {
                    enabled = false
                }
            } catch (e: Exception) {
                Log.w(TAG, "⚠️ Location plugin disable failed", e)
            }

            // 4️⃣ Hủy NavigationProvider
            if (MapboxNavigationProvider.isCreated()) {
                MapboxNavigationProvider.destroy()
                Log.i(TAG, "✅ MapboxNavigationProvider destroyed")
            }

            // 5️⃣ Hủy MapView (nếu có)
            binding.mapView?.onStop()
            binding.mapView?.onDestroy()

            // 6️⃣ Giải phóng tham chiếu
            mapboxNavigation = null
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error cleaning up MapboxNavigationView", e)
        }
    }

    private fun startNavigation() {
        val puckIcon = this.puckIcon
        // initialize location puck
        binding.mapView.location.apply {
            setLocationProvider(navigationLocationProvider)
            locationPuck = CarLocationPuck.navigationPuck2D()
//            this.locationPuck = LocationPuck2D(
//                bearingImage = ImageHolder.Companion.from(
//                    puckIcon
//                ),
//                scaleExpression = "[\"literal\", 1]"
//            )
            puckBearingEnabled = true
            enabled = true
        }

        startRoute()
    }

    private val arrivalObserver = object : ArrivalObserver {

        override fun onWaypointArrival(routeProgress: RouteProgress) {
            onArrival(routeProgress)
        }

        override fun onNextRouteLegStart(routeLegProgress: RouteLegProgress) {
            // do something when the user starts a new leg
        }

        override fun onFinalDestinationArrival(routeProgress: RouteProgress) {
            onArrival(routeProgress)
        }
    }

    private fun onArrival(routeProgress: RouteProgress) {
        val leg = routeProgress.currentLegProgress
        if (leg != null) {
            val event = Arguments.createMap()
            event.putInt("index", leg.legIndex)
            event.putDouble("latitude", leg.legDestination?.location?.latitude() ?: 0.0)
            event.putDouble("longitude", leg.legDestination?.location?.longitude() ?: 0.0)
            context
                .getJSModule(RCTEventEmitter::class.java)
                .receiveEvent(id, "onArrive", event)
        }
    }

    override fun requestLayout() {
        super.requestLayout()
        post(measureAndLayout)
    }

    private val measureAndLayout = Runnable {
        measure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        )
        layout(left, top, right, bottom)
    }

    private fun getProfileForVehicle(vehicle: Int): String {
        return when (vehicle) {
            Vehicle.MOTO -> DirectionsCriteria.PROFILE_CYCLING // đi đường nhỏ/hẻm
            else -> DirectionsCriteria.PROFILE_DRIVING_TRAFFIC // ô tô
        }
    }

    private fun findRoute(coordinates: List<Point>) {
        Log.e(TAG, "findRoute-call")
        // Separate legs work
        embedding = true
        val indices = mutableListOf<Int>()
        val names = mutableListOf<String>()
        indices.add(0)
        names.add(originTitle)
        indices.addAll(waypointLegs.map { it.index })
        names.addAll(waypointLegs.map { it.name })
        indices.add(coordinates.count() - 1)
        names.add(destinationTitle)
        // val profile = getProfileForVehicle(this.vehicle)
        mapboxNavigation?.requestRoutes(
            RouteOptions.builder()
                .applyDefaultNavigationOptions()
                .applyLanguageAndVoiceUnitOptions(context)
                .coordinatesList(coordinates)
//                .profile(profile)
                .waypointIndicesList(indices)
                .waypointNamesList(names)
                .alternatives(false)// chỉ cẩn 1 đường
                .language("vi")
                .steps(true)
                .voiceInstructions(true)
                .voiceUnits(distanceUnit)
                .enableRefresh(true)
                .build(),
            object : NavigationRouterCallback {
                override fun onCanceled(
                    routeOptions: RouteOptions,
                    @RouterOrigin routerOrigin: String
                ) {
                    // no implementation
                    embedding = false
                }

                override fun onFailure(reasons: List<RouterFailure>, routeOptions: RouteOptions) {
                    sendErrorToReact("Error finding route $reasons")
                    Log.e(TAG, "findRoute-onFailure")
                    embedding = false
                }

                override fun onRoutesReady(
                    routes: List<NavigationRoute>,
                    @RouterOrigin routerOrigin: String
                ) {
                    Log.e(TAG, "findRoute-onRoutesReady")
                    Log.d(TAG, "✅ onRoutesReady - total routes: ${routes.size}")
                    routes.forEachIndexed { index, route ->
                        Log.d(
                            TAG,
                            "Route[$index] - distance: ${route.directionsRoute.distance()} meters"
                        )
                        Log.d(
                            TAG,
                            "Route[$index] - distance: ${route.directionsRoute.waypoints()} meters"
                        )
                        Log.d(
                            TAG,
                            "Route[$index] - duration: ${route.directionsRoute.duration()} seconds"
                        )
                    }
                    setRouteAndStartNavigation(routes)
                    embedding = false
                }
            }
        )
    }

    @SuppressLint("MissingPermission")
    private fun setRouteAndStartNavigation(routes: List<NavigationRoute>) {
        Log.e(TAG, "setRouteAndStartNavigation")
        // set routes, where the first route in the list is the primary route that
        // will be used for active guidance
        mapboxNavigation?.setNavigationRoutes(routes)

        // show UI elements
        binding.soundButton.visibility = visibleSound
        binding.routeOverview.visibility = visibleRouteOverview
        binding.recenter.visibility = visibleCenter
        binding.tripProgressCard.visibility = visibleTripProgressCard

        // move the camera to overview when new route is available
        navigationCamera.requestNavigationCameraToFollowing()
        mapboxNavigation?.startTripSession(withForegroundService = true)
//        mapboxNavigation?.setTripNotificationInterceptor()
    }

    private fun startRoute() {
        // register event listeners
        mapboxNavigation?.registerRoutesObserver(routesObserver)
        mapboxNavigation?.registerArrivalObserver(arrivalObserver)
        mapboxNavigation?.registerRouteProgressObserver(routeProgressObserver)
        mapboxNavigation?.registerLocationObserver(locationObserver)
        mapboxNavigation?.registerVoiceInstructionsObserver(voiceInstructionsObserver)

        // Create a list of coordinates that includes origin, destination
        val coordinatesList = mutableListOf<Point>()
        this.origin?.let { coordinatesList.add(it) }
        this.waypoints.let { coordinatesList.addAll(waypoints) }
        this.destination?.let { coordinatesList.add(it) }

        Log.e(TAG, "startRoute.findRoute")
        findRoute(coordinatesList)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        try {
            mapboxNavigation?.stopTripSession()
            mapboxNavigation?.unregisterRoutesObserver(routesObserver)
            mapboxNavigation?.unregisterArrivalObserver(arrivalObserver)
            mapboxNavigation?.unregisterLocationObserver(locationObserver)
            mapboxNavigation?.unregisterRouteProgressObserver(routeProgressObserver)
            mapboxNavigation?.unregisterVoiceInstructionsObserver(voiceInstructionsObserver)
            try {
                mapboxNavigation?.setNavigationRoutes(listOf())
            } catch (ex: Exception) {
                Log.w(TAG, "Failed to clear routes safely", ex)
            }
        } catch (e: Exception) {
            Log.w(TAG, "onDetachedFromWindow cleanup error", e)
        } finally {
            binding.soundButton.visibility = View.INVISIBLE
            binding.maneuverView.visibility = View.INVISIBLE
            binding.routeOverview.visibility = View.INVISIBLE
            binding.tripProgressCard.visibility = View.INVISIBLE
        }
    }

    private fun sendErrorToReact(error: String?) {
        val event = Arguments.createMap()
        event.putString("error", error)
        context
            .getJSModule(RCTEventEmitter::class.java)
            .receiveEvent(id, "onError", event)
    }

    fun onDropViewInstance() {
        this.onDestroy()
    }

    fun setStartOrigin(origin: Point?) {
        this.origin = origin
    }

    fun setDestination(destination: Point?) {
        this.destination = destination
        destination?.let { showDestinationMarker(it) }
    }

    fun showDestinationMarker(destinationPoint: Point) {
        val viewAnnotationManager = binding.mapView.viewAnnotationManager
        viewAnnotationManager.removeAllViewAnnotations()
        val options = viewAnnotationOptions {
            geometry(destinationPoint)      // vị trí GeoJSON Point
//            width(64.0)                     // width view (px)
//            height(64.0)                    // height view (px)
            allowOverlap(true)              // cho phép overlap với view khác
            allowOverlapWithPuck(true)      // cho phép overlap với vị trí puck (xe)
            offsets(16.0, 16.0)               // offset x, y nếu muốn điều chỉnh
            ignoreCameraPadding(true)       // bỏ qua padding camera
        }

        // Thêm view annotation
        viewAnnotationManager.addViewAnnotation(
            R.layout.destination_marker,  // layout XML chứa ImageView icon
            options = options
        )
    }

    fun setVehicle(value: Int) {
        this.vehicle = value
        if (value == Vehicle.MOTO) {
            this.puckIcon = R.drawable.ic_moto
        } else {
            this.puckIcon = R.drawable.ic_car
        }
    }


    fun setDestinationTitle(title: String) {
        this.destinationTitle = title
    }

    fun setWaypointLegs(legs: List<WaypointLegs>) {
        this.waypointLegs = legs
    }

    fun setWaypoints(waypoints: List<Point>) {
        this.waypoints = waypoints
    }

    fun setDirectionUnit(unit: String) {
        this.distanceUnit = unit
    }

    fun setLocal(language: String) {
        val locals = language.split("-")
        when (locals.size) {
            1 -> locale = Locale(locals.first())
            2 -> locale = Locale(locals.first(), locals.last())
        }
    }

    fun setMute(mute: Boolean) {
        this.isVoiceInstructionsMuted = mute
    }

    fun senEventJS(reactContext: ReactContext, name: String, params: WritableMap) {
        reactContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
            .emit(name, params)
    }

    fun senEventId(reactContext: ReactContext, name: String, params: WritableMap) {
        reactContext.getJSModule(RCTEventEmitter::class.java)
            .receiveEvent(id, name, params)
    }

    fun maneuversToReact(
        reactContext: ReactContext,
        maneuvers: Expected<ManeuverError, List<Maneuver>>
    ) {
        maneuvers.fold(
            { error ->
                Log.w("ManeuverError", "Failed to get maneuvers: ${error.throwable}")
            },
            { maneuversList ->
                val array = Arguments.createArray()
                for (m in maneuversList) {
                    val obj = Arguments.createMap()
                    obj.putString("primary.text", m.primary.text)
                    obj.putString("primary.type", m.primary?.type)
                    obj.putString("primary.drivingSide", m.primary?.drivingSide)
                    obj.putDouble("primary.degrees", m.primary?.degrees ?: 0.0)
                    obj.putString("primary.modifier", m.primary?.modifier)

                    obj.putString("secondary.text", m.secondary?.text)
                    obj.putString("secondary.type", m.secondary?.type)
                    obj.putString("secondary.drivingSide", m.secondary?.drivingSide)
                    obj.putDouble("secondary.degrees", m.secondary?.degrees ?: 0.0)
                    obj.putString("secondary.modifier", m.secondary?.modifier)

                    obj.putString("sub.text", m.sub?.text ?: "")
                    obj.putString("sub.id", m.sub?.id ?: "")

                    obj.putDouble("maneuverPoint.latitude", m.maneuverPoint.latitude())
                    obj.putDouble("maneuverPoint.longitude", m.maneuverPoint.longitude())

                    obj.putDouble(
                        "stepDistance.distanceRemaining",
                        m.stepDistance.distanceRemaining ?: 0.0
                    )
                    obj.putDouble("stepDistance.totalDistance", m.stepDistance.totalDistance)


                    val laneGuidance = m.laneGuidance
                    if (laneGuidance != null) {
                        val laneArray = Arguments.createArray()
                        for (lane in laneGuidance.allLanes) {
                            val laneObj = Arguments.createMap()
                            laneObj.putBoolean("isActive", lane.isActive)
                            laneObj.putString("drivingSide", lane.drivingSide)
                            laneObj.putString("activeDirection", lane.activeDirection)

                            // convert directions list
                            val dirArray = Arguments.createArray()
                            for (dir in lane.directions) {
                                dirArray.pushString(dir)
                            }
                            laneObj.putArray("directions", dirArray)

                            laneArray.pushMap(laneObj)
                        }
                        obj.putArray("laneGuidance", laneArray)
                    }


                    array.pushMap(obj)
                }

                reactContext
                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                    .emit("onManeuversUpdate", array.toString())


                val event = Arguments.createMap()
                event.putArray("maneuvers", array)

                reactContext.getJSModule(RCTEventEmitter::class.java)
                    .receiveEvent(id, "onManeuversUpdate", event)
            })

    }
}
