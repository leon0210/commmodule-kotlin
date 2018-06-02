package com.leon.common.utils

import android.util.Log

/**
 * Log统一管理类
 *
 * @author way
 */
class LogUtil private constructor() {

    init {
        throw UnsupportedOperationException("LogUtils cannot be instantiated")
    }

    companion object {
        var isDebug = true// 是否需要打印bug
        private const val TAG = "LOG"

        // 下面四个是默认tag的函数
        fun i(msg: String?) {
            if (isDebug)
                Log.i(TAG, msg)
        }

        fun d(msg: String?) {
            if (isDebug)
                Log.d(TAG, msg)
        }

        fun e(msg: String?) {
            if (isDebug)
                Log.e(TAG, msg)
        }

        fun v(msg: String?) {
            if (isDebug)
                Log.v(TAG, msg)
        }

        // 下面是传入自定义tag的函数
        fun i(tag: String, msg: String?) {
            if (isDebug)
                Log.i(tag, msg)
        }

        fun d(tag: String, msg: String?) {
            if (isDebug)
                Log.d(tag, msg)
        }

        fun e(tag: String, msg: String?) {
            if (isDebug)
                Log.e(tag, msg)
        }

        fun v(tag: String, msg: String?) {
            if (isDebug)
                Log.v(tag, msg)
        }
    }
}