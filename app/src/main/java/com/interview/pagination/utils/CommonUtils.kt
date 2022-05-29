package com.interview.pagination.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.interview.pagination.R
import java.util.*
import kotlin.collections.ArrayList


//for visible and invisible of an view
fun changeVisibility(status: Boolean, vararg views: View) {
    views.forEach {
        if (status) {
            it.visibility = View.VISIBLE
        } else {
            it.visibility = View.GONE
        }
    }
}

//for internet check available
fun isInternetAvailable(context: Context): Boolean {
    var result = false
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    cm?.run {
        cm.getNetworkCapabilities(cm.activeNetwork)?.run {
            result = when {
                hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }
    }
    return result
}


fun <T> List<T>.toArrayList(): ArrayList<T> {
    return ArrayList(this)
}

//load svg Image
fun ImageView.loadUrl(url: String) {

    val imageLoader = ImageLoader.Builder(this.context)
        .componentRegistry { add(SvgDecoder(this@loadUrl.context)) }
        .build()

    val request = ImageRequest.Builder(this.context)
        .crossfade(false)
        .crossfade(500)
        .placeholder(R.drawable.ic_placeholder)
        .data(url)
        .diskCachePolicy(CachePolicy.ENABLED)
        .target(this)
        .build()
    imageLoader.enqueue(request)
}


fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}


/* Methods for Progress Dialog */
private var loadingScreen: Dialog? = null

fun showLoadingScreen(context: Context) {
    // Prevent the dialog from showing if the context of the activity is finishing
    try {
        if (!(context as Activity).isFinishing) {
            loadingScreen = buildProgressBarDialog(context)
            if (loadingScreen != null) {
                loadingScreen!!.show()
            }
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({ makeLoadingVisible(loadingScreen!!) }, 10)
        } else {
            if (loadingScreen != null) {
                loadingScreen!!.dismiss()
                loadingScreen = null
            }
        }
    } catch (ignored: ClassCastException) {
    }

}

fun dismissLoadingScreen() {
    if (loadingScreen == null) {
        // Do nothing as no progress dialog is present
    } else {
        loadingScreen!!.dismiss()
    }
}

private fun buildProgressBarDialog(context: Context): Dialog {
    val dialog = Dialog(context)
    dialog.setContentView(R.layout.layout_progress_bar)
    val progressBar = dialog.findViewById(R.id.progress_bar) as ProgressBar
    progressBar.visibility = View.GONE
    val layoutParams = WindowManager.LayoutParams()
    layoutParams.copyFrom(Objects.requireNonNull<Window>(dialog.window).attributes)
    layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
    val drawable = ColorDrawable(Color.TRANSPARENT)
    drawable.alpha = 0
    dialog.window!!.attributes = layoutParams
    dialog.window!!.setBackgroundDrawable(drawable)
    dialog.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    return dialog
}

private fun makeLoadingVisible(dialog: Dialog) {
    val progressBar = dialog.findViewById(R.id.progress_bar) as ProgressBar
    progressBar.visibility = View.VISIBLE
    val layoutParams = WindowManager.LayoutParams()
    layoutParams.copyFrom(Objects.requireNonNull<Window>(dialog.window).attributes)
    layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
    val drawable = ColorDrawable(Color.TRANSPARENT)
    drawable.alpha = 0
    dialog.window!!.attributes = layoutParams
    dialog.window!!.setBackgroundDrawable(drawable)
}

fun convertKelvinToCelsius(kelvin: Double): Double {
    return (kelvin - 273.15)
}







