package com.leon.common.utils

import android.app.Activity
import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

/**
 * 软键盘工具类
 */
object KeyBoardUtil {
    /**
     * 打开软键盘
     *
     * @param mEditText
     * @param mContext
     */
    fun openKeybord(mEditText: EditText, mContext: Context) {
        val imm = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN)
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    /**
     * 关闭软键盘
     *
     * @param mEditText
     * @param mContext
     */
    fun closeKeyBord(mEditText: EditText, mContext: Context) {
        val imm = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(mEditText.windowToken, 0)
    }

    /**
     * 关闭软键盘
     *
     * @param activity
     */
    fun closeKeyBord(activity: Activity) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (isSoftInputShow(activity))
            imm.hideSoftInputFromWindow(activity.window.decorView.windowToken, 0)
    }

    /**
     * 判断当前软键盘是否打开
     *
     * @param activity
     * @return
     */
    fun isSoftInputShow(activity: Activity): Boolean {
        // 虚拟键盘 判断view是否为空
        val view = activity.window.peekDecorView()
        if (view != null) {
            // 获取虚拟键盘
            val inputmanger = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            return inputmanger.isActive && activity.window.currentFocus != null
        }
        return false
    }

    /**
     * 为了实现点击软件盘之外的区域关闭软键盘
     * 此方法用于判断软键盘是否应该被关闭
     * * @param v
     *
     * @param event
     * @return
     */
    fun isShouldHideInput(v: View?, event: MotionEvent): Boolean {
        if (v != null && v is EditText) {
            val leftTop = intArrayOf(0, 0)
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop)
            val left = leftTop[0]
            val top = leftTop[1]
            val bottom = top + v.height
            val right = left + v.width
            return !(event.x > left && event.x < right
                    && event.y > top && event.y < bottom)
        }
        return false
    }

    /**
     * onCreate()方法添加此方法
     * 禁止自动弹出软键盘
     *
     * @param activity
     */
    fun forbidAutoOpen(activity: Activity) {
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }
}
