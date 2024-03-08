package com.pablo.tetris.infra.settings

import java.io.Serializable

data class SettingsData(
    var lastSeenGalleryImageIndex: Int = 0,
    var imagesWon: List<String> = listOf(),
    var adCounter: Int = 0,
    var showAdsEveryGalleryAction: Int = 2,
    var showRateAppPopupActionCount: Int = 0,
    var showRateAppPopupEveryXActions: Int = 2
) : Serializable