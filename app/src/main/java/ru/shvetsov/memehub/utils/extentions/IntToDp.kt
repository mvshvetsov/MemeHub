package ru.shvetsov.memehub.utils.extentions

import android.content.Context

fun Int.toDp(context: Context): Int {
    return (this * context.resources.displayMetrics.density).toInt()
}