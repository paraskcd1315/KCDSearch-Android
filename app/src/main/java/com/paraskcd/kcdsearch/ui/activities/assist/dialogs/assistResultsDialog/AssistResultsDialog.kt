package com.paraskcd.kcdsearch.ui.activities.assist.dialogs.assistResultsDialog

import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.paraskcd.kcdsearch.constants.AssistConstants
import com.paraskcd.kcdsearch.ui.activities.assist.dialogs.AssistDialog
import com.paraskcd.kcdsearch.ui.activities.assist.dialogs.assistResultsDialog.components.assistResultsList.AssistResultsList
import com.paraskcd.kcdsearch.ui.activities.assist.dialogs.assistResultsDialog.components.assistResultsList.AssistResultsListParams
import com.paraskcd.kcdsearch.ui.activities.assist.dialogs.assistSearchBarDialog.AssistSearchbarDialog
import com.paraskcd.kcdsearch.ui.theme.KCDSearchTheme
import com.paraskcd.kcdsearch.utils.extensionMethods.isWindowBlurSupported
import java.lang.ref.WeakReference

object AssistResultsDialog : AssistDialog() {
    private var searchBarLayoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null
    private var searchBarDecorViewRef: WeakReference<View>? = null
    private val maxAvailableHeightPx = mutableIntStateOf(0)

    fun show(params: AssistResultsDialogParams) {
        val activity = params.context as? ComponentActivity ?: return
        if (dialog != null) return

        val supportsBlur = params.context.isWindowBlurSupported()

        dialog = createDialog(activity, isFocusable = false) {
            KCDSearchTheme {
                val suggestions by params.suggestionsFlow.collectAsState()
                val isLoading by params.isLoadingFlow.collectAsState()
                var visible by remember { mutableStateOf(false) }

                val hasSuggestions = suggestions.isNotEmpty() || isLoading

                LaunchedEffect(hasSuggestions) {
                    visible = hasSuggestions
                }

                val maxHeightPx by maxAvailableHeightPx
                val maxHeightDp = if (maxHeightPx > 0) {
                    with(LocalDensity.current) { maxHeightPx.toDp() }
                } else {
                    600.dp
                }

                AnimatedVisibility(
                    visible = visible,
                    enter = slideInVertically(initialOffsetY = { -it / 3 }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { -it / 3 }) + fadeOut()
                ) {
                    AssistResultsList(
                        params = AssistResultsListParams(
                            suggestions = suggestions,
                            isLoading = isLoading,
                            getAppIcon = params.getAppIcon,
                            onSuggestionClick = params.onSuggestionClick,
                            onCallClick = params.onCallClick,
                            onMessageClick = params.onMessageClick,
                            onWhatsAppClick = params.onWhatsAppClick,
                            isWhatsappInstalled = params.isWhatsappInstalled,
                            supportsBlur = supportsBlur,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = maxHeightDp)
                        )
                    )
                }
            }
        }

        dialog?.window?.let { win ->
            ensureYOffsetWithSearchBar(win, activity)
        }

        setOnDismissAction { cleanupSearchBarListener() }
        showWithBlur()
    }

    override fun close() {
        cleanupSearchBarListener()
        super.close()
    }

    private fun computeYOffset(dm: DisplayMetrics): Int? {
        val gapPx = (dm.density * AssistConstants.RESULTS_GAP_DP).toInt()
        val searchBarY = AssistSearchbarDialog.getYOffset() ?: return null
        val searchBarH = AssistSearchbarDialog.getHeight() ?: return null
        if (searchBarH <= 0) return null
        return searchBarY + searchBarH + gapPx
    }

    private fun ensureYOffsetWithSearchBar(win: Window, activity: ComponentActivity) {
        val dm = activity.resources.displayMetrics
        val desiredWidth = (dm.widthPixels * 0.95f).toInt()
        val statusBarTopInset = getStatusBarInsetTopPx(activity)

        fun applyOffsetIfPossible(): Boolean {
            val yOffset = computeYOffset(dm) ?: return false
            win.attributes = win.attributes.apply { y = yOffset }
            val maxHeight = (dm.heightPixels * AssistConstants.RESULTS_MAX_HEIGHT_RATIO).toInt()
            val availableHeight = (dm.heightPixels - statusBarTopInset - yOffset)
                .coerceAtMost(maxHeight)
                .coerceAtLeast(1)
            maxAvailableHeightPx.intValue = availableHeight
            win.setLayout(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
            return true
        }

        if (applyOffsetIfPossible()) {
            observeSearchBarLayoutChanges(activity, win)
            return
        }

        val searchBarView = AssistSearchbarDialog.getDecorView() ?: return
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

    private fun observeSearchBarLayoutChanges(activity: ComponentActivity, win: Window) {
        val searchBarView = AssistSearchbarDialog.getDecorView() ?: return
        searchBarDecorViewRef = WeakReference(searchBarView)
        val dm = activity.resources.displayMetrics
        val desiredWidth = (dm.widthPixels * 0.95f).toInt()
        val statusBarTopInset = getStatusBarInsetTopPx(activity)

        searchBarLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
            val yOffset = computeYOffset(dm) ?: return@OnGlobalLayoutListener
            win.attributes = win.attributes.apply { y = yOffset }
            val maxHeight = (dm.heightPixels * AssistConstants.RESULTS_MAX_HEIGHT_RATIO).toInt()
            val availableHeight = (dm.heightPixels - statusBarTopInset - yOffset)
                .coerceAtMost(maxHeight)
                .coerceAtLeast(1)
            maxAvailableHeightPx.intValue = availableHeight
            win.setLayout(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        searchBarView.viewTreeObserver.addOnGlobalLayoutListener(searchBarLayoutListener)
    }

    private fun cleanupSearchBarListener() {
        searchBarLayoutListener?.let { listener ->
            searchBarDecorViewRef?.get()?.viewTreeObserver?.removeOnGlobalLayoutListener(listener)
        }
        searchBarLayoutListener = null
        searchBarDecorViewRef = null
    }

    private fun getStatusBarInsetTopPx(activity: ComponentActivity): Int {
        val view = activity.window?.decorView ?: return guessStatusBarHeight(activity)
        val insets = ViewCompat.getRootWindowInsets(view)
        val top = insets
            ?.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.statusBars())
            ?.top
        return top ?: guessStatusBarHeight(activity)
    }

    private fun guessStatusBarHeight(ctx: Context): Int {
        val resId = ctx.resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resId > 0) ctx.resources.getDimensionPixelSize(resId) else 0
    }
}
