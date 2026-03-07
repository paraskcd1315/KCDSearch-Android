package com.paraskcd.kcdsearch.ui.modules.assist.dialogs

import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Context
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.Window
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.paraskcd.kcdsearch.R
import com.paraskcd.kcdsearch.constants.AssistConstants
import com.paraskcd.kcdsearch.model.SuggestionItem
import com.paraskcd.kcdsearch.ui.modules.assist.components.assistResults.AssistResultsList
import com.paraskcd.kcdsearch.ui.modules.assist.components.assistResults.AssistResultsListParams
import com.paraskcd.kcdsearch.ui.theme.KCDSearchTheme
import com.paraskcd.kcdsearch.utils.extensionMethods.isWindowBlurSupported
import kotlinx.coroutines.flow.StateFlow

object AssistResultsDialog {
    private var dialog: Dialog? = null
    private var blurAnimator: ValueAnimator? = null
    private var currentBlurRadius: Int = 0
    private var searchBarLayoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null
    private var maxAvailableHeightPx = androidx.compose.runtime.mutableIntStateOf(0)

    fun show(
        context: Context,
        suggestionsFlow: StateFlow<List<SuggestionItem>>,
        isLoadingFlow: StateFlow<Boolean>,
        getAppIcon: (String) -> ImageBitmap?,
        onSuggestionClick: (SuggestionItem) -> Unit,
        onCallClick: (String) -> Unit = {},
        onMessageClick: (String) -> Unit = {},
        onWhatsAppClick: (String) -> Unit = {},
        isWhatsappInstalled: Boolean = false
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
                        val suggestions by suggestionsFlow.collectAsState()
                        val isLoading by isLoadingFlow.collectAsState()
                        val maxHeightPx by maxAvailableHeightPx
                        val density = LocalDensity.current
                        val maxHeightDp = if (maxHeightPx > 0) {
                            with(density) { maxHeightPx.toDp() }
                        } else {
                            600.dp
                        }

                        LaunchedEffect(Unit) {
                            setVisible(true)
                        }

                        AnimatedVisibility(
                            visible = visible && (suggestions.isNotEmpty() || isLoading),
                            enter = slideInVertically(initialOffsetY = { -it / 3 }) + fadeIn(),
                            exit = slideOutVertically(targetOffsetY = { -it / 3 }) + fadeOut()
                        ) {
                            AssistResultsList(
                                params = AssistResultsListParams(
                                    suggestions = suggestions,
                                    isLoading = isLoading,
                                    getAppIcon = getAppIcon,
                                    onSuggestionClick = onSuggestionClick,
                                    supportsBlur = supportsBlur,
                                    onCallClick = onCallClick,
                                    onMessageClick = onMessageClick,
                                    onWhatsAppClick = onWhatsAppClick,
                                    isWhatsappInstalled = isWhatsappInstalled
                                ),
                                modifier = Modifier.heightIn(max = maxHeightDp)
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

    private fun computeYOffset(dm: DisplayMetrics): Int? {
        val gapPx = (dm.density * AssistConstants.RESULTS_GAP_DP).toInt()

        val searchBarY = AssistSearchBarDialog.getSearchBarYOffset() ?: return null
        val searchBarH = AssistSearchBarDialog.getSearchBarHeight() ?: return null
        if (searchBarH <= 0) return null

        return searchBarY + searchBarH + gapPx
    }

    private fun ensureYOffsetWithSearchBar(win: Window, activity: ComponentActivity) {
        val dm: DisplayMetrics = activity.resources.displayMetrics
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

    private fun observeSearchBarLayoutChanges(activity: ComponentActivity, win: Window) {
        val searchBarView = AssistSearchBarDialog.getDecorView() ?: return
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
            AssistSearchBarDialog.getDecorView()?.viewTreeObserver?.removeOnGlobalLayoutListener(listener)
        }
        searchBarLayoutListener = null
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

    fun getDecorView(): View? = dialog?.window?.decorView
}
