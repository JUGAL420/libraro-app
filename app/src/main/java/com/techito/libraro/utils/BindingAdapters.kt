package com.techito.libraro.utils

import android.text.Html
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

object BindingAdapters {
    @JvmStatic
    @BindingAdapter("htmlText")
    fun setHtmlText(view: TextView, html: String?) {
        if (html != null) {
            view.text = Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT)
        } else {
            view.text = ""
        }
    }

    @JvmStatic
    @BindingAdapter("app:refreshing")
    fun setRefreshing(view: SwipeRefreshLayout, isRefreshing: Boolean?) {
        view.isRefreshing = isRefreshing == true
    }
}