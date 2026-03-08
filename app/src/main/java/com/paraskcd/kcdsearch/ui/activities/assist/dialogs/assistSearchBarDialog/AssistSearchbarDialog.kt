package com.paraskcd.kcdsearch.ui.activities.assist.dialogs.assistSearchBarDialog

import android.animation.ValueAnimator
import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.activity.ComponentActivity
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
import com.paraskcd.kcdsearch.ui.activities.assist.dialogs.AssistDialog
import com.paraskcd.kcdsearch.ui.activities.assist.dialogs.assistSearchBarDialog.components.assistSearchbar.AssistSearchbar
import com.paraskcd.kcdsearch.ui.activities.assist.dialogs.assistSearchBarDialog.components.assistSearchbar.AssistSearchbarParams
import com.paraskcd.kcdsearch.ui.theme.KCDSearchTheme
import com.paraskcd.kcdsearch.utils.extensionMethods.isWindowBlurSupported
import java.lang.ref.WeakReference

object AssistSearchbarDialog : AssistDialog() {
    private const val FLOAT_DISTANCE_DP = 24
    private const val KEYBOARD_THRESHOLD_RATIO = 0.15

    private var yAnimator: ValueAnimator? = null
    private var currentYOffset: Int = 0
    private var layoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null
    private var activityRootViewRef: WeakReference<View>? = null

    fun show(params: AssistSearchbarDialogParams) {
        val activity = params.context as? ComponentActivity ?: return
        if (dialog != null) return

        val supportsBlur = params.context.isWindowBlurSupported()
        val dm = activity.resources.displayMetrics
        val baseOffset = (dm.density * FLOAT_DISTANCE_DP).toInt()

        dialog = createDialog(activity, isFocusable = true) {
            KCDSearchTheme {
                var visible by remember { mutableStateOf(false) }
                var localQuery by remember { mutableStateOf("") }

                LaunchedEffect(Unit) { visible = true }

                AnimatedVisibility(
                    visible = visible,
                    enter = slideInVertically(initialOffsetY = { it / 3 }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it / 3 }) + fadeOut()
                ) {
                    AssistSearchbar(
                        params = AssistSearchbarParams(
                            query = localQuery,
                            onQueryChange = {
                                localQuery = it
                                params.onQueryChange(it)
                            },
                            onClear = {
                                localQuery = ""
                                params.onQueryChange("")
                            },
                            onSearchSubmit = { q ->
                                if (q.isNotBlank()) {
                                    params.onQuerySubmit(q)
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

        dialog?.window?.attributes = dialog?.window?.attributes?.apply { y = baseOffset }
        currentYOffset = baseOffset

        // Setup keyboard tracking BEFORE show — exactly like old code
        val rootView = activity.window.decorView.rootView
        activityRootViewRef = WeakReference(rootView)
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
                animateYTo(targetY)
            }
        }
        rootView.viewTreeObserver.addOnGlobalLayoutListener(layoutListener)

        setOnDismissAction {
            cleanupKeyboardTracking()
            params.onDismiss()
        }

        showWithBlur()
    }

    override fun close() {
        cleanupKeyboardTracking()
        super.close()
    }

    private fun animateYTo(target: Int) {
        yAnimator?.cancel()
        yAnimator = ValueAnimator.ofInt(currentYOffset, target).apply {
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

    private fun cleanupKeyboardTracking() {
        layoutListener?.let { listener ->
            activityRootViewRef?.get()?.viewTreeObserver?.removeOnGlobalLayoutListener(listener)
        }
        layoutListener = null
        activityRootViewRef = null
        yAnimator?.cancel()
        yAnimator = null
    }
}
