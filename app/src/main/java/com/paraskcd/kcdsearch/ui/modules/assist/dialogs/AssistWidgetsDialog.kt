package com.paraskcd.kcdsearch.ui.modules.assist.dialogs

import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Context
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.WindowCompat
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.paraskcd.kcdsearch.R
import com.paraskcd.kcdsearch.constants.AssistConstants
import com.paraskcd.kcdsearch.data.api.weather.dataSources.PirateWeatherResponse
import com.paraskcd.kcdsearch.data.local.entities.RecentAppEntity
import com.paraskcd.kcdsearch.ui.modules.assist.components.assistWidgets.AssistWidgets
import com.paraskcd.kcdsearch.ui.modules.assist.components.assistWidgets.AssistWidgetsParams
import com.paraskcd.kcdsearch.ui.theme.KCDSearchTheme
import com.paraskcd.kcdsearch.utils.extensionMethods.isWindowBlurSupported
import kotlinx.coroutines.flow.StateFlow

object AssistWidgetsDialog {
    private var dialog: Dialog? = null
    private var blurAnimator: ValueAnimator? = null
    private var currentBlurRadius: Int = 0
    private var searchBarLayoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null

    fun show(
        context: Context,
        forecastFlow: StateFlow<PirateWeatherResponse?>,
        weatherIsLoadingFlow: StateFlow<Boolean>,
        cityNameFlow: StateFlow<String?>,
        useFahrenheitFlow: StateFlow<Boolean>,
        recentAppsFlow: StateFlow<List<RecentAppEntity>>,
        getAppIcon: (String) -> ImageBitmap?,
        onAppClick: (String) -> Unit,
    ) {
        val activity = context as? ComponentActivity ?: return
        val supportsBlur = context.isWindowBlurSupported()

        if (dialog != null) return

        dialog = Dialog(activity, R.style.Theme_KCDSearch_BlurDialog).apply {
            val compose = ComposeView(activity).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setViewTreeLifecycleOwner(activity)
                setViewTreeViewModelStoreOwner(activity)
                setViewTreeSavedStateRegistryOwner(activity)
                setContent {
                    KCDSearchTheme {
                        val (visible, setVisible) = remember { mutableStateOf(false) }
                        val forecast by forecastFlow.collectAsState()
                        val weatherIsLoading by weatherIsLoadingFlow.collectAsState()
                        val cityName by cityNameFlow.collectAsState()
                        val useFahrenheit by useFahrenheitFlow.collectAsState()
                        val recentApps by recentAppsFlow.collectAsState()

                        LaunchedEffect(Unit) {
                            setVisible(true)
                        }

                        AnimatedVisibility(
                            visible = visible,
                            enter = slideInVertically(initialOffsetY = { -it / 3 }) + fadeIn(),
                            exit = slideOutVertically(targetOffsetY = { -it / 3 }) + fadeOut()
                        ) {
                            AssistWidgets(
                                params = AssistWidgetsParams(
                                    forecast = forecast,
                                    weatherIsLoading = weatherIsLoading,
                                    cityName = cityName,
                                    useFahrenheit = useFahrenheit,
                                    recentApps = recentApps,
                                    getAppIcon = getAppIcon,
                                    onAppClick = onAppClick,
                                    supportsBlur = supportsBlur,
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            setContentView(compose)

            window?.let { win ->
                val dm: DisplayMetrics = activity.resources.displayMetrics
                val desiredWidth = (dm.widthPixels * 0.95f).toInt()

                win.setLayout(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
                win.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL)
                win.setBackgroundDrawableResource(R.drawable.blur_dialog_bg)
                win.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                win.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
                win.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
                WindowCompat.setDecorFitsSystemWindows(win, false)
                setWindowBlur(0)

                ensureYOffsetWithSearchBar(win, activity)
            }

            setCancelable(true)
            setCanceledOnTouchOutside(true)

            setOnDismissListener {
                cleanupSearchBarListener()
                blurAnimator?.cancel()
                blurAnimator = null
                setWindowBlur(0)
                currentBlurRadius = 0
                dialog = null
            }

            show()

            animateWindowBlurTo(target = AssistConstants.BLUR_RADIUS_MAX, duration = 250L)
        }
    }

    fun close() {
        dialog?.dismiss()
        dialog = null
    }

    fun getWidgetsHeight(): Int? = dialog?.window?.decorView?.height?.takeIf { it > 0 }
    fun getWidgetsYOffset(): Int? = dialog?.window?.attributes?.y
    fun getDecorView(): View? = dialog?.window?.decorView
    fun isShowing(): Boolean = dialog != null

    private fun ensureYOffsetWithSearchBar(win: android.view.Window, activity: ComponentActivity) {
        val dm: DisplayMetrics = activity.resources.displayMetrics
        val desiredWidth = (dm.widthPixels * 0.95f).toInt()
        val gapPx = (dm.density * AssistConstants.RESULTS_GAP_DP).toInt()

        fun applyOffsetIfPossible(): Boolean {
            val searchBarY = AssistSearchBarDialog.getSearchBarYOffset() ?: return false
            val searchBarH = AssistSearchBarDialog.getSearchBarHeight() ?: return false
            if (searchBarH <= 0) return false

            val yOffset = searchBarY + searchBarH + gapPx
            win.attributes = win.attributes.apply { y = yOffset }
            win.setLayout(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
            return true
        }

        if (applyOffsetIfPossible()) {
            observeSearchBarLayoutChanges(activity, win)
            return
        }

        val searchBarView = AssistSearchBarDialog.getDecorView() ?: return
        val vto = searchBarView.viewTreeObserver
        val listener = object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (applyOffsetIfPossible()) {
                    if (vto.isAlive) vto.removeOnGlobalLayoutListener(this)
                    else searchBarView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    observeSearchBarLayoutChanges(activity, win)
                }
            }
        }
        vto.addOnGlobalLayoutListener(listener)
    }

    private fun observeSearchBarLayoutChanges(activity: ComponentActivity, win: android.view.Window) {
        val searchBarView = AssistSearchBarDialog.getDecorView() ?: return
        val dm = activity.resources.displayMetrics
        val desiredWidth = (dm.widthPixels * 0.95f).toInt()
        val gapPx = (dm.density * AssistConstants.RESULTS_GAP_DP).toInt()

        searchBarLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
            val searchBarY = AssistSearchBarDialog.getSearchBarYOffset() ?: return@OnGlobalLayoutListener
            val searchBarH = AssistSearchBarDialog.getSearchBarHeight() ?: return@OnGlobalLayoutListener
            if (searchBarH <= 0) return@OnGlobalLayoutListener

            val yOffset = searchBarY + searchBarH + gapPx
            win.attributes = win.attributes.apply { y = yOffset }
            win.setLayout(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        searchBarView.viewTreeObserver.addOnGlobalLayoutListener(searchBarLayoutListener)
    }

    private fun cleanupSearchBarListener() {
        searchBarLayoutListener?.let { listener ->
            AssistSearchBarDialog.getDecorView()?.viewTreeObserver?.removeOnGlobalLayoutListener(listener)
        }
        searchBarLayoutListener = null
    }

    private fun setWindowBlur(radius: Int) {
        dialog?.window?.let { win ->
            runCatching { win.setBackgroundBlurRadius(radius) }
        }
    }

    private fun animateWindowBlurTo(target: Int, duration: Long) {
        blurAnimator?.cancel()
        val start = currentBlurRadius.coerceAtLeast(0)
        if (start == target) return
        blurAnimator = ValueAnimator.ofInt(start, target).apply {
            this.duration = duration
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { anim ->
                val value = anim.animatedValue as Int
                currentBlurRadius = value
                setWindowBlur(value)
            }
            start()
        }
    }
}
