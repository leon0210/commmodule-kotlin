package com.leon.common.utils

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.support.annotation.StringRes
import android.widget.Toast

object ToastUtil {

    private var toast: Toast? = null
    @SuppressLint("ToastUtil.showToast", "ShowToast")
    fun showToast(string: String, duration: Int = Toast.LENGTH_SHORT) {
        if (toast == null) {
            toast = Toast.makeText(CommonModuleInit.context, string, duration)
        } else {
            toast?.setText(string)
            toast?.duration = duration
        }
        Handler(Looper.getMainLooper()).post { toast?.show() }
    }

    @SuppressLint("ToastUtil.showToast", "ShowToast")
    fun showToast(@StringRes strId: Int, duration: Int = Toast.LENGTH_SHORT) {
        if (toast == null) {
            toast = Toast.makeText(CommonModuleInit.context, strId, duration)
        } else {
            toast?.setText(strId)
            toast?.duration = duration
        }
        Handler(Looper.getMainLooper()).post { toast?.show() }
    }

    fun cancel() {
        if (toast != null) toast?.cancel()
    }
}