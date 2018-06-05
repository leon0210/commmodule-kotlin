package com.leon.common.widget

import android.app.Activity
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.support.annotation.LayoutRes
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import com.leon.common.utils.DisplayUtil
import com.leon.common.utils.getQuickLayoutInflater

class CommonPopupWindow : PopupWindow() {
    private var builder: Builder? = null

    override fun getWidth(): Int {
        return builder?.view?.measuredWidth!!
    }

    override fun getHeight(): Int {
        return builder?.view?.measuredHeight!!
    }

    override fun setOutsideTouchable(touchable: Boolean) {
        super.setOutsideTouchable(touchable)
        setBackgroundDrawable(ColorDrawable(0x00000000))//设置透明背景
        isFocusable = touchable
    }

    override fun dismiss() {
        super.dismiss()
        setScreenBgLight(this.contentView.context)
    }

    override fun showAsDropDown(anchor: View?) {
        super.showAsDropDown(anchor)
        setScreenBgDarken(this.contentView.context)
    }

    override fun showAsDropDown(anchor: View?, xoff: Int, yoff: Int) {
        super.showAsDropDown(anchor, xoff, yoff)
        setScreenBgDarken(this.contentView.context)
    }

    override fun showAsDropDown(anchor: View?, xoff: Int, yoff: Int, gravity: Int) {
        super.showAsDropDown(anchor, xoff, yoff, gravity)
        setScreenBgDarken(this.contentView.context)
    }

    override fun showAtLocation(parent: View?, gravity: Int, x: Int, y: Int) {
        super.showAtLocation(parent, gravity, x, y)
        setScreenBgDarken(this.contentView.context)
    }

    fun setBuilder(builder: Builder) {
        this.builder = builder
    }

    /**
     * 背景变暗
     */
    private fun setScreenBgDarken(context: Context) {
        if (builder != null && builder?.backgroundAlpha!! < 1) {
            val activity = context as Activity
            val attributes = activity.window.attributes
            attributes.alpha = builder?.backgroundAlpha!!
            activity.window.attributes = attributes
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }
    }

    /**
     * 背景亮度恢复
     */
    private fun setScreenBgLight(context: Context?) {
        val activity = context as Activity
        val attributes = activity.window.attributes
        attributes.alpha = 1.0f
        activity.window.attributes = attributes
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }

    /**
     * 全屏下方弹出
     */
    fun showParentButtom(activity: Activity) {
        showAtLocation(activity.findViewById(android.R.id.content), Gravity.BOTTOM, 0, 0)
    }

    class Builder(private val context: Context?) {
        var view: View? = null
            private set
        var backgroundAlpha = 1f//背景透明度
            private set
        private var width = 0//0-wrap 1-match
        private var height = 0

        private var isTouchable = true
        private var animalStyle = 0
        private var onChildView: ((view: View, popupWindow: PopupWindow) -> Unit) = { view, popupWindow -> }

        init {
            if (context == null) throw NullPointerException("context is null")
        }

        fun setOnChildListener(on: (parentView: View, popupWindow: PopupWindow) -> Unit): Builder {
            this.onChildView = on
            return this
        }

        //填充View
        fun setView(view: View): Builder {
            this.view = view
            return this
        }

        //填充View
        fun setView(@LayoutRes layoutResId: Int): Builder {
            this.view = context?.getQuickLayoutInflater(layoutResId)
            return this
        }

        //设置宽和高
        fun setWidthAndHeight(width: Int = 0, height: Int = 0): Builder {
            this.width = width
            this.height = height
            return this
        }

        //show()时设置背景透明度
        fun setBackGroundAlpha(alpha: Float): Builder {
            this.backgroundAlpha = alpha
            return this
        }

        //外部是否可点击
        fun setOutsideTouchable(isTouchable: Boolean): Builder {
            this.isTouchable = isTouchable
            return this
        }

        //设置动画
        fun setAnimationStyle(animal: Int): Builder {
            this.animalStyle = animal
            return this
        }

        fun create(): CommonPopupWindow {
            val commonPopup = CommonPopupWindow()
            commonPopup.apply {
                setBuilder(this@Builder)
                if (view != null) contentView = view
                if (animalStyle != 0) animationStyle = animalStyle
                setPopupWindowLayoutParams(this)
                isOutsideTouchable = isTouchable
                onChildView.invoke(view!!, this)
            }
            DisplayUtil.measureWidthAndHeight(view!!)
            return commonPopup
        }

        /**
         * 设置宽高
         */
        private fun setPopupWindowLayoutParams(commonPopup: CommonPopupWindow) {
            val newWidth = when (width) {
                0 -> ViewGroup.LayoutParams.WRAP_CONTENT
                1 -> ViewGroup.LayoutParams.MATCH_PARENT
                else -> DisplayUtil.dip2px(context!!, width.toFloat())
            }
            val newHeight = when (height) {
                0 -> ViewGroup.LayoutParams.WRAP_CONTENT
                1 -> ViewGroup.LayoutParams.MATCH_PARENT
                else -> DisplayUtil.dip2px(context!!, height.toFloat())
            }
            commonPopup.width = newWidth
            commonPopup.height = newHeight
        }
    }
}