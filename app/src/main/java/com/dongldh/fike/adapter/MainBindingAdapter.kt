package com.dongldh.fike.adapter

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter(value = ["bind:distance", "bind:remain"])
fun bindDistanceAndRemains(view: TextView, distance: Int, remain: Int) {
    val string = "xm / y대"
    val newString = string.replace("x", distance.toString()).replace("y", remain.toString())

    val spannable = SpannableString(newString)
    val slash = newString.indexOf("/")
    spannable.setSpan(ForegroundColorSpan(Color.parseColor("#dddddd")), slash, slash+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    view.text = spannable
}