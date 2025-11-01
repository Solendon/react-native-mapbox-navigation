package com.mapboxnavigation

import com.mapbox.maps.ImageHolder
import com.mapbox.maps.plugin.LocationPuck2D

object CarLocationPuck {
    fun navigationPuck2D() = LocationPuck2D(
        bearingImage = ImageHolder.from(R.drawable.mapbox_navigation_puck_icon),
        scaleExpression = "[\"literal\", 1]"
    )

    fun puckIcon2D(puckIcon: Int) = LocationPuck2D(
        bearingImage = ImageHolder.Companion.from(
            puckIcon
        ),
        scaleExpression = "[\"literal\", 1]"
    )
}
