package com.paraskcd.kcdsearch.ui.activities.assist.dialogs

import android.animation.ValueAnimator
import android.app.Dialog
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.WindowCompat
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.paraskcd.kcdsearch.R
import com.paraskcd.kcdsearch.constants.AssistConstants

abstract class AssistDialog {
    protected var dialog: Dialog? = null
    private var blurAnimator: ValueAnimator? = null
    private var currentBlurRadius: Int = 0

    protected fun createDialog(
        activity: ComponentActivity,
        isFocusable: Boolean = true,
        content: @Composable () -> Unit,
    ): Dialog {
        return Dialog(activity, R.style.Theme_KCDSearch_BlurDialog).apply {
            setContentView(createComposeView(activity, content))
            window?.let { configureWindow(it, activity, isFocusable) }
            setCancelable(true)
            setCanceledOnTouchOutside(true)
        }
    }

    open fun close() {
        cleanupBlur()
        dialog?.dismiss()
        dialog = null
    }

    fun getHeight(): Int? = dialog?.window?.decorView?.height?.takeIf { it > 0 }
    fun getYOffset(): Int? = dialog?.window?.attributes?.y
    fun getDecorView(): View? = dialog?.window?.decorView
    fun isShowing(): Boolean = dialog != null

    protected fun setOnDismissAction(onExtra: (() -> Unit)? = null) {
        dialog?.setOnDismissListener {
            cleanupBlur()
            onExtra?.invoke()
            dialog = null
        }
    }

    protected fun showWithBlur() {
        dialog?.show()
        animateWindowBlurTo(target = AssistConstants.BLUR_RADIUS_MAX, duration = 250L)
    }

    private fun cleanupBlur() {
        blurAnimator?.cancel()
        blurAnimator = null
        setWindowBlur(0)
        currentBlurRadius = 0
    }

    private fun createComposeView(
        activity: ComponentActivity,
        content: @Composable () -> Unit
    ): ComposeView {
        return ComposeView(activity).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setViewTreeLifecycleOwner(activity)
            setViewTreeViewModelStoreOwner(activity)
            setViewTreeSavedStateRegistryOwner(activity)
            setContent { content() }
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

    private fun configureWindow(win: Window, activity: ComponentActivity, isFocusable: Boolean) {
        val dm = activity.resources.displayMetrics
        val desiredWidth = (dm.widthPixels * 0.95f).toInt()

        win.setLayout(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
        win.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL)
        win.setBackgroundDrawableResource(R.drawable.blur_dialog_bg)
        win.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        if (!isFocusable) {
            win.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        }
        win.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        WindowCompat.setDecorFitsSystemWindows(win, false)
        setWindowBlur(0)
    }
}
