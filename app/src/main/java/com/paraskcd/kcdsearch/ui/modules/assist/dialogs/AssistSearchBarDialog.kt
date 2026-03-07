package com.paraskcd.kcdsearch.ui.modules.assist.dialogs

import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Context
import android.graphics.Rect
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.activity.ComponentActivity
import androidx.core.view.WindowCompat
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.paraskcd.kcdsearch.R
import com.paraskcd.kcdsearch.constants.AssistConstants
import com.paraskcd.kcdsearch.model.SuggestionItem
import com.paraskcd.kcdsearch.ui.modules.assist.components.assistSearchBar.AssistSearchBar
import com.paraskcd.kcdsearch.ui.modules.assist.components.assistSearchBar.AssistSearchBarParams
import com.paraskcd.kcdsearch.ui.theme.KCDSearchTheme
import com.paraskcd.kcdsearch.utils.extensionMethods.isWindowBlurSupported

object AssistSearchBarDialog {
    private var dialog: Dialog? = null
    private const val FLOAT_DISTANCE_DP = 24
    private const val KEYBOARD_THRESHOLD_RATIO = 0.15

    private var blurAnimator: ValueAnimator? = null
    private var yAnimator: ValueAnimator? = null
    private var currentBlurRadius: Int = 0
    private var currentYOffset: Int = 0
    private var layoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null

    fun show(
        context: Context,
        onQueryChange: (String) -> Unit,
        onQuerySubmit: (String) -> Unit,
        onSuggestionClick: (SuggestionItem) -> Unit,
        onDismiss: () -> Unit
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
                        var localQuery by remember { mutableStateOf("") }

                        LaunchedEffect(Unit) {
                            setVisible(true)
                        }

                        AnimatedVisibility(
                            visible = visible,
                            enter = slideInVertically(initialOffsetY = { it / 3 }) + fadeIn(),
                            exit = slideOutVertically(targetOffsetY = { it / 3 }) + fadeOut()
                        ) {
                            AssistSearchBar(
                                params = AssistSearchBarParams(
                                    query = localQuery,
                                    onQueryChange = {
                                        localQuery = it
                                        onQueryChange(it)
                                    },
                                    onClear = {
                                        localQuery = ""
                                        onQueryChange("")
                                    },
                                    onSearchSubmit = { q ->
                                        if (q.isNotBlank()) {
                                            onQuerySubmit(q)
                                            close()
                                        }
                                    },
                                    supportsBlur = supportsBlur,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            )
                        }
                    }
                }
            }

            setContentView(compose)

            val dm: DisplayMetrics = activity.resources.displayMetrics
            val baseOffset = (dm.density * FLOAT_DISTANCE_DP).toInt()

            window?.let { win ->
                val desiredWidth = (dm.widthPixels * 0.95f).toInt()

                win.setLayout(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
                win.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL)
                win.setBackgroundDrawableResource(R.drawable.blur_dialog_bg)
                win.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                win.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
                WindowCompat.setDecorFitsSystemWindows(win, false)
                win.attributes = win.attributes.apply { y = baseOffset }
                setWindowBlur(0)
            }

            currentYOffset = baseOffset

            val rootView = activity.window.decorView.rootView
            val visibleRect = Rect()
            layoutListener = ViewTreeObserver.OnGlobalLayoutListener {
                rootView.getWindowVisibleDisplayFrame(visibleRect)
                val screenHeight = rootView.height
                val keypadHeight = screenHeight - visibleRect.bottom
                val keyboardOffset = if (keypadHeight > screenHeight * KEYBOARD_THRESHOLD_RATIO) {
                    keypadHeight
                } else {
                    0
                }
                val targetY = baseOffset + keyboardOffset
                if (targetY != currentYOffset) {
                    animateYOffset(targetY)
                }
            }
            rootView.viewTreeObserver.addOnGlobalLayoutListener(layoutListener)

            setCancelable(true)
            setCanceledOnTouchOutside(true)

            setOnDismissListener {
                rootView.viewTreeObserver.removeOnGlobalLayoutListener(layoutListener)
                layoutListener = null
                blurAnimator?.cancel()
                blurAnimator = null
                yAnimator?.cancel()
                yAnimator = null
                setWindowBlur(0)
                currentBlurRadius = 0
                currentYOffset = 0
                dialog = null
                onDismiss()
            }

            show()

            animateWindowBlurTo(target = AssistConstants.BLUR_RADIUS_MAX, duration = 250L)
        }
    }

    fun close() {
        dialog?.dismiss()
        dialog = null
    }

    fun getSearchBarHeight(): Int? = dialog?.window?.decorView?.height?.takeIf { it > 0 }
    fun getSearchBarYOffset(): Int? = dialog?.window?.attributes?.y
    fun getDecorView(): View? = dialog?.window?.decorView

    private fun animateYOffset(target: Int) {
        yAnimator?.cancel()
        val start = currentYOffset
        yAnimator = ValueAnimator.ofInt(start, target).apply {
            duration = 250L
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { anim ->
                val value = anim.animatedValue as Int
                currentYOffset = value
                dialog?.window?.attributes = dialog?.window?.attributes?.apply { y = value }
            }
            start()
        }
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
