package com.batodev.tetris.infra.helpers

import android.app.Activity
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import com.batodev.tetris.R
import com.batodev.tetris.infra.settings.SettingsData
import com.batodev.tetris.infra.settings.SettingsHelper
import com.google.android.play.core.review.ReviewException
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.model.ReviewErrorCode


const val WHEN_TO_SHOW_APP_RATE_POPUP_DEFAULT = 10
private const val DO_NOT_SHOW_ADS_EVER = Integer.MIN_VALUE

object RateAppHelper {
    fun increaseRateAppCounterAndShowDialogIfApplicable(
        activity: Activity
    ) {
        val settings = SettingsHelper.load(activity)
        increaseRateAppCounterAndShowDialogIfApplicable(activity, settings)
        SettingsHelper.save(activity, settings)
    }

    fun increaseRateAppCounterAndShowDialogIfApplicable(
        activity: Activity,
        settings: SettingsData
    ): Boolean {
        Log.d(RateAppHelper.javaClass.simpleName, "increaseRateAppCounterAndShowDialogIfApplicable")
        val whenToShow = settings.whenToShowRateAppPopup
        var actionCount = settings.actionCount
        if (actionCount == DO_NOT_SHOW_ADS_EVER) {
            Log.d(
                RateAppHelper.javaClass.simpleName,
                "doing nothing since actionCount: $actionCount"
            )
            return true
        }
        Log.d(
            RateAppHelper.javaClass.simpleName,
            "whenToShow: $whenToShow, actionCount: $actionCount"
        )
        if (++actionCount >= whenToShow) {
            showRateAppPopup(activity, settings)
        }
        settings.actionCount = actionCount
        return false
    }

    fun showRateAppPopup(activity: Activity, prefs: SettingsData) {
        val inflater = activity.layoutInflater
        val popupView = inflater.inflate(
            R.layout.rate_app_popup,
            activity.findViewById(R.id.gallery_activity_layout),
            false
        )
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        val btnRateNow = popupView.findViewById<Button>(R.id.btnRateNow)
        btnRateNow.setOnClickListener {
            onNeverPressed(activity, prefs)
            val manager = ReviewManagerFactory.create(activity)
            val request = manager.requestReviewFlow()
            request.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val reviewInfo = task.result
                    val flow = manager.launchReviewFlow(activity, reviewInfo)
                    flow.addOnCompleteListener { _ ->
                        // The flow has finished. The API does not indicate whether the user
                        // reviewed or not, or even whether the review dialog was shown. Thus, no
                        // matter the result, we continue our app flow.
                        Log.d(RateAppHelper.javaClass.simpleName, "Review ok")
                    }

                } else {
                    // There was some problem, log or handle the error code.
                    @ReviewErrorCode val reviewErrorCode =
                        (task.exception as ReviewException).errorCode
                    Log.d(RateAppHelper.javaClass.simpleName, "Review error: $reviewErrorCode")
                }
            }

            popupWindow.dismiss()
            prefs.whenToShowAds = WHEN_TO_SHOW_ADS_DEFAULT * 2
            SettingsHelper.save(activity, prefs)
            Log.d(RateAppHelper.javaClass.simpleName, "saved WHEN_TO_SHOW_ADS: ${WHEN_TO_SHOW_ADS_DEFAULT * 2}"
            )
        }
        val btnRateLater = popupView.findViewById<Button>(R.id.btnRateLater)
        btnRateLater.setOnClickListener {
            onLaterPressed(activity, prefs)
            popupWindow.dismiss()
        }
        val btnNeverRate = popupView.findViewById<Button>(R.id.btnNeverRate)
        btnNeverRate.setOnClickListener {
            onNeverPressed(activity, prefs)
            popupWindow.dismiss()
        }
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)
        popupWindow.setOnDismissListener {
            if (prefs.actionCount != DO_NOT_SHOW_ADS_EVER) {
                onLaterPressed(activity, prefs)
            }
        }
    }

    private fun onNeverPressed(activity: Activity, prefs: SettingsData) {
        prefs.actionCount = DO_NOT_SHOW_ADS_EVER
        SettingsHelper.save(activity, prefs)
        Log.d(RateAppHelper.javaClass.simpleName, "saved actionCount: $DO_NOT_SHOW_ADS_EVER")
    }

    private fun onLaterPressed(activity: Activity, prefs: SettingsData) {
        prefs.actionCount = 0
        SettingsHelper.save(activity, prefs)
        Log.d(RateAppHelper.javaClass.simpleName, "saved actionCount: 0")
    }
}