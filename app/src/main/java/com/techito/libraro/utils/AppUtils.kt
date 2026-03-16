package com.techito.libraro.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.LinearGradient
import android.graphics.Shader
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Parcelable
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.techito.libraro.R
import java.io.Serializable
import java.util.UUID

/**
 * Utility object containing helper methods for UI manipulations, animations,
 * device information, and connectivity checks throughout the Libraro application.
 */
object AppUtils {

    /**
     * Changes the appearance of the status bar icons (light or dark) and background color.
     * Uses WindowInsetsControllerCompat for compatibility across different Android versions.
     *
     * @param activity The activity context where the status bar should be modified.
     * @param colorResId The color resource ID for the status bar background.
     * @param isLight True to set dark icons (for light backgrounds), false for white icons (for dark backgrounds).
     */
    fun changeStatusBarColor(activity: Activity, @ColorRes colorResId: Int, isLight: Boolean) {
        val window = activity.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        val color = ContextCompat.getColor(activity, colorResId)
        window.statusBarColor = color

        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = isLight
    }

    /**
     * Extension function to convert DP (Density-independent Pixels) to PX (Pixels).
     *
     * @param context The context used to retrieve display metrics.
     * @return The pixel value as an Integer.
     */
    fun Int.dpToPx(context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }

    /**
     * Displays a short duration Toast message.
     *
     * @param context The context to show the toast.
     * @param message The string message to be displayed.
     */
    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Performs an entrance animation on a view, combining fade-in, scaling, and translation.
     * Once finished, it automatically triggers a continuous pulse loop.
     *
     * @param view The view to animate.
     */
    fun startEntranceAnimation(view: View) {
        view.translationY = 40f
        view.alpha = 0f
        view.scaleX = 0.85f
        view.scaleY = 0.85f

        val fadeIn = ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f)
        val scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, 0.85f, 1.05f, 1f)
        val scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 0.85f, 1.05f, 1f)
        val translate = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 40f, 0f)

        AnimatorSet().apply {
            playTogether(fadeIn, scaleX, scaleY, translate)
            duration = 600
            interpolator = OvershootInterpolator()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    startPulseLoop(view)
                }
            })
            start()
        }
    }

    /**
     * Starts an infinite pulsing animation (scaling up and down) on a view.
     * Includes safety checks to ensure the view is still attached to the window.
     *
     * @param view The view to apply the pulse effect to.
     */
    fun startPulseLoop(view: View) {
        // Safety check to prevent leaks or crashes if the view is detached
        if (!view.isAttachedToWindow) return

        val scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, 1f, 1.06f, 1f)
        val scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1f, 1.06f, 1f)

        AnimatorSet().apply {
            playTogether(scaleX, scaleY)
            duration = 1200
            interpolator = AccelerateDecelerateInterpolator()
            startDelay = 1500
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    // Loop the animation safely
                    if (view.isAttachedToWindow) {
                        startPulseLoop(view)
                    }
                }
            })
            start()
        }
    }

    /**
     * Applies a horizontal linear gradient to the text of a TextView.
     *
     * @param textView The TextView to apply the gradient to.
     * @param colors Vararg list of color resource IDs to form the gradient.
     */
    fun applyGradientToTextView(textView: TextView, vararg colors: Int) {
        if (colors.isEmpty()) return

        textView.post {
            val paint = textView.paint
            val width = paint.measureText(textView.text.toString())
            if (width <= 0f) return@post
            
            val context = textView.context
            val colorInts = colors.map { ContextCompat.getColor(context, it) }.toIntArray()

            val shader = LinearGradient(
                0f, 0f, width, textView.textSize,
                colorInts,
                null, Shader.TileMode.CLAMP
            )
            textView.paint.shader = shader
            textView.invalidate()
        }
    }

    /**
     * Configures a BottomSheetDialog to occupy a specific percentage of the screen height
     * and applies a custom background.
     *
     * @param bottomSheetDialog The dialog instance to configure.
     * @param activity The activity context used to calculate window height.
     */
    fun setupFullHeight(bottomSheetDialog: BottomSheetDialog, activity: Activity) {
        val bottomSheet = bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet) ?: return
        val behavior = BottomSheetBehavior.from(bottomSheet)
        val layoutParams = bottomSheet.layoutParams
        val windowHeight = getWindowHeight(activity)
        if (windowHeight != 0) {
            layoutParams.height = windowHeight
        }

        bottomSheet.layoutParams = layoutParams
        bottomSheet.background = ContextCompat.getDrawable(activity, R.drawable.bg_bottom_sheet)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.isDraggable = false
    }

    /**
     * Calculates the available window height (excluding system bars) and returns a specific percentage.
     * Handles differences between legacy and modern Android (API 30+) display APIs.
     *
     * @param activity The activity context.
     * @return The calculated height in pixels (90% of screen height).
     */
    fun getWindowHeight(activity: Activity): Int {
        val percentage = 90
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = activity.windowManager.currentWindowMetrics
            val insets = windowMetrics.windowInsets.getInsets(WindowInsets.Type.systemBars())
            (windowMetrics.bounds.height() - insets.top - insets.bottom) * percentage / 100
        } else {
            val displayMetrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.heightPixels * percentage / 100
        }
    }

    /**
     * Retrieves a unique device ID (ANDROID_ID). 
     * If the ID is unavailable or unreliable, returns a random UUID.
     *
     * @param context The application context.
     * @return A string representing the unique device identifier.
     */
    @SuppressLint("HardwareIds")
    fun getDeviceId(context: Context): String {
        val id = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        return if (id.isNullOrEmpty() || id == "9774d56d682e549c") {
            UUID.randomUUID().toString()
        } else {
            id
        }
    }

    /**
     * Checks if the device has an active internet connection (WiFi, Cellular, or Ethernet).
     *
     * @param context The application context.
     * @return True if internet is available, false otherwise.
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    /**
     * Programmatically hides the soft keyboard from the screen.
     *
     * @param view The view that currently has focus.
     */
    fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * Helper extension to get a Parcelable extra from an Intent in a backward-compatible way.
     * Handles the deprecation of the standard getParcelableExtra in API 33+.
     *
     * @param key The name of the extra.
     * @return The Parcelable object or null if not found.
     */
    inline fun <reified T : Parcelable> Intent.getParcelableExtraCompat(key: String): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getParcelableExtra(key, T::class.java)
        } else {
            @Suppress("DEPRECATION")
            getParcelableExtra(key)
        }
    }

    /**
     * Helper extension to get a Serializable extra from an Intent in a backward-compatible way.
     * Handles the deprecation of the standard getSerializableExtra in API 33+.
     *
     * @param key The name of the extra.
     * @return The Serializable object cast to type T, or null if not found.
     */
    inline fun <reified T> Intent.getSerializableCompat(key: String): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getSerializableExtra(key, Serializable::class.java) as? T
        } else {
            @Suppress("DEPRECATION")
            getSerializableExtra(key) as? T
        }
    }
}
