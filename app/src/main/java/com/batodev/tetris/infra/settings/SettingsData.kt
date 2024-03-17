package com.batodev.tetris.infra.settings

import com.batodev.tetris.infra.helpers.WHEN_TO_SHOW_ADS_DEFAULT
import com.batodev.tetris.infra.helpers.WHEN_TO_SHOW_APP_RATE_POPUP_DEFAULT
import java.io.Serializable

data class SettingsData(
    var lastSeenGalleryImageIndex: Int = 0,
    var imagesWon: MutableList<String> = mutableListOf(),
    var adCounter: Int = 0,
    var showAdsEveryGalleryAction: Int = 2,
    var showRateAppPopupActionCount: Int = 0,
    var showRateAppPopupEveryXActions: Int = 2,
    var whenToShowAds: Int = WHEN_TO_SHOW_ADS_DEFAULT,
    var actionCount: Int = 0,
    var whenToShowRateAppPopup: Int = WHEN_TO_SHOW_APP_RATE_POPUP_DEFAULT
) : Serializable