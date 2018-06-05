package com.leon.common.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast


/**
 * Created by leon on 2017/6/5.
 */

object AppUtil {
    /**
     * 获取APP版本号
     *
     * @param context
     * @return
     */
    fun getVersionCode(context: Context): String {
        val packageManager = context.packageManager
        val packageInfo: PackageInfo
        var versionCode = ""
        try {
            packageInfo = packageManager.getPackageInfo(context.packageName, 0)
            versionCode = packageInfo.versionCode.toString() + ""
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return versionCode
    }

    /**
     * 获取APP版本名字
     *
     * @param context
     * @return
     */
    fun getVersionName(context: Context): String {
        val packageManager = context.packageManager
        val packageInfo: PackageInfo
        var versionName = ""
        try {
            packageInfo = packageManager.getPackageInfo(context.packageName, 0)
            versionName = packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return versionName
    }


    /**
     * 获取application中指定的meta-data。本例中，调用方法时key就是UMENG_CHANNEL
     *
     * @return 如果没有获取成功(没有对应值，或者异常)，则返回值为空
     */
    fun getAppMetaData(ctx: Context?, key: String): String? {
        if (ctx == null || TextUtils.isEmpty(key)) {
            return null
        }
        var resultData: String? = null
        try {
            val packageManager = ctx.packageManager
            if (packageManager != null) {
                val applicationInfo = packageManager.getApplicationInfo(ctx.packageName, PackageManager.GET_META_DATA)
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        resultData = applicationInfo.metaData.getString(key)
                    }
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return resultData
    }

    /**
     *  判断当前应用是否是debug状态
     */
    fun isApkInDebug(context: Context): Boolean {
        return try {
            val applicationInfo = context.applicationInfo
            (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 打开指定的App
     * @param packageName "需要打开的包名"
     * @param url
     * @param className "打开指定的类"
     */
    fun openOtherApp(context: Context, packageName: String, url: String = "", className: String = ""): Boolean {
        return try {
            val packageManager = context.packageManager
            val intent = packageManager.getLaunchIntentForPackage(packageName) //这里参数就是你要打开的app的包名
            intent.run {
                if (url.isNotEmpty()) data = Uri.parse(url)
                if (className.isNotEmpty()) setClassName(packageName, className)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            LogUtil.e("打开另外一个应用出错", e.message)   //未打开，可能要打开的app没有安装，需要再此进行处理
            false
        }
    }

    /**
     * 检查指定包名是否存在
     */
    fun checkPackage(context: Context, packageName: String?): Boolean {
        if (packageName.isNullOrEmpty()) {
            return false
        }
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                context.packageManager.getApplicationInfo(packageName, PackageManager.MATCH_UNINSTALLED_PACKAGES)
            } else {
                context.packageManager.getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES)
            }
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    /**
     * @param context
     * @return -1--无网络   0--手机网络   1--WIFI网络
     */
    fun getNetWorkState(context: Context): Int {
        getNetworkInfo(context).let {
            if (it != null && it.isAvailable) {
                return when (it.type) {
                    ConnectivityManager.TYPE_MOBILE -> 0
                    ConnectivityManager.TYPE_WIFI -> 1
                    else -> -1
                }
            }
            return -1
        }
    }

    /**
     * 获取网络信息
     */
    private fun getNetworkInfo(context: Context): NetworkInfo? {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo
    }

    /**
     * 检查是否有网络
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val cm = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        //如果仅仅是用来判断网络连接
        //则可以使用 cm.getActiveNetworkInfo().isAvailable();
        @SuppressLint("MissingPermission")
        val info = cm.allNetworkInfo
        info?.indices?.filter { info[it].state == NetworkInfo.State.CONNECTED }?.forEach { return true }
        return false
    }

    /**
     * 使用浏览器打开指定网址
     */
    fun openBrowser(activity: Activity, targetUrl: String) {
        if (TextUtils.isEmpty(targetUrl) || targetUrl.startsWith("file://")) {
            Toast.makeText(activity, "$targetUrl 该链接无法使用浏览器打开。", Toast.LENGTH_SHORT).show()
            return
        }
        Intent().run {
            action = "android.intent.action.VIEW"
            data = Uri.parse(targetUrl)
            activity.startActivity(this)
        }
    }

    /**
     * @param number 目标QQ
     */
    fun openQQchat(activity: Activity, number: String) {
        val url = "mqqwpa://im/chat?chat_type=wpa&uin=$number"//uin是发送过去的qq号码
        activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    /**
     * 背景亮度恢复
     */
    fun setScreenBgLight(activity: Activity) {
        val attributes = activity.window.attributes
        attributes.alpha = 1.0f
        activity.window.attributes = attributes
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }

    /**
     * 背景变暗
     */
    fun setScreenBgDarken(activity: Activity, alpha: Float = 0.7f) {
        val attributes = activity.window.attributes
        attributes.alpha = alpha
        activity.window.attributes = attributes
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }

    /**
     * 获取当前进程名字
     */
    fun getCurProcessName(context: Context): String? {
        val pid = android.os.Process.myPid()
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (appProcess in activityManager.runningAppProcesses) {
            if (appProcess.pid == pid) {
                return appProcess.processName
            }
        }
        return null
    }
}
