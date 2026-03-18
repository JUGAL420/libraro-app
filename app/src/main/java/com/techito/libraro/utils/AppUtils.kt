package com.techito.libraro.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Rect
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
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.techito.libraro.R
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import java.util.UUID
import kotlin.math.abs

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
        val bottomSheet =
            bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
                ?: return
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
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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

    /**
     * Shows a MaterialDatePicker and returns the selected date in dd-MM-yyyy format.
     *
     * @param fragmentManager The supportFragmentManager to show the dialog.
     * @param title The title for the date picker.
     * @param onDateSelected Callback function that receives the formatted date string.
     */
    fun showDatePicker(
        fragmentManager: FragmentManager,
        title: String,
        onDateSelected: (String) -> Unit
    ) {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTheme(R.style.CustomDatePickerTheme)
            .setTitleText(title)
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.timeInMillis = selection
            val formattedDate =
                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(calendar.time)
            onDateSelected(formattedDate)
        }

        datePicker.show(fragmentManager, "MATERIAL_DATE_PICKER")
    }

    /**
     * Shows a MaterialTimePicker and returns the selected time in hh:mm a format.
     *
     * @param fragmentManager The FragmentManager to show the dialog.
     * @param title The title for the time picker.
     * @param onTimeSelected Callback function that receives the formatted time string.
     */
    fun showTimePicker(
        fragmentManager: FragmentManager,
        title: String,
        onTimeSelected: (String) -> Unit
    ) {
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(0)
            .setTitleText(title)
            .build()

        picker.addOnPositiveButtonClickListener {
            val hour24 = picker.hour
            val minute = picker.minute
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, hour24)
            calendar.set(Calendar.MINUTE, minute)

            val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val formattedTime = sdf.format(calendar.time)
            onTimeSelected(formattedTime)
        }

        picker.show(fragmentManager, "MATERIAL_TIME_PICKER")
    }

    /**
     * Dynamically handles system bar insets for a given view by applying appropriate padding.
     * This ensures content isn't obscured by status or navigation bars when using edge-to-edge layouts.
     *
     * @param view The target view to apply padding to.
     * @param applyTop True to apply top inset padding (Status Bar). Default is true.
     * @param applyBottom True to apply bottom inset padding (Navigation Bar). Default is true.
     */
    fun handleSystemBars(view: View, applyTop: Boolean = true, applyBottom: Boolean = true) {
        val originalPadding = Rect(
            view.paddingLeft,
            view.paddingTop,
            view.paddingRight,
            view.paddingBottom
        )

        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                originalPadding.left,
                if (applyTop) originalPadding.top + systemBars.top else originalPadding.top,
                originalPadding.right,
                if (applyBottom) originalPadding.bottom + systemBars.bottom else originalPadding.bottom
            )
            insets
        }
    }

    /**
     * Extracts initials from a given name string.
     * If there are two or more words, it takes the first letter of the first and second words.
     * If there is only one word, it takes the first two letters of that word.
     *
     * @param name The name string to extract initials from.
     * @return A capitalized string containing the initials.
     */
    fun getInitials(name: String): String {
        val words = name.trim()
            .split("\\s+".toRegex())
            .filter { it.isNotEmpty() }

        return when {
            words.size >= 2 -> {
                "${words[0].first()}${words[1].first()}".uppercase()
            }
            words.size == 1 -> {
                words[0].take(2).uppercase()
            }
            else -> ""
        }
    }

    /**
     * Deterministically picks a background color for an avatar based on the provided name.
     *
     * @param name The name string used to calculate the color index.
     * @return An integer representing the chosen color.
     */
    fun getAvatarColor(name: String): Int {
        val colors = listOf(
            "#1B1464", "#2E7D32", "#C62828", "#1565C0",
            "#6A1B9A", "#EF6C00", "#00838F", "#4E342E"
        )

        val index = abs(name.hashCode()) % colors.size
        return colors[index].toColorInt()
    }

    /**
     * Determines whether white or black text should be used based on the brightness of the background color.
     * Uses the standard formula for luminance.
     *
     * @param bgColor The background color integer.
     * @return Color.WHITE if the background is dark, Color.BLACK otherwise.
     */
    fun getTextColor(bgColor: Int): Int {
        val darkness =
            1 - (0.299 * Color.red(bgColor) +
                    0.587 * Color.green(bgColor) +
                    0.114 * Color.blue(bgColor)) / 255

        return if (darkness >= 0.5) Color.WHITE else Color.BLACK
    }

    /**
     * Configures a TextView to display a user's avatar with their initials, 
     * a background color based on their name, and an appropriately contrasting text color.
     *
     * @param view The TextView to configure.
     * @param name The name to generate initials and color for.
     */
    fun setAvatar(view: TextView, name: String) {

        val bgColor = getAvatarColor(name)
        val textColor = getTextColor(bgColor)

        view.text = getInitials(name)
        view.setTextColor(textColor)
        view.setBackgroundColor(bgColor)
    }
}
