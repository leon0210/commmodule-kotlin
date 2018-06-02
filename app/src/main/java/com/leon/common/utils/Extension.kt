package com.leon.common.utils

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.annotation.LayoutRes
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.*
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast


/**
 * Created by leonhwang on 2017/8/31.
 */
/**
 * 使用浏览器打开指定网址
 */
fun Activity.openBrowser(targetUrl: String) {
    if (TextUtils.isEmpty(targetUrl) || targetUrl.startsWith("file://")) {
        Toast.makeText(this, "$targetUrl 该链接无法使用浏览器打开。", Toast.LENGTH_SHORT).show()
        return
    }
    Intent().run {
        action = "android.intent.action.VIEW"
        data = Uri.parse(targetUrl)
        startActivity(this)
    }
}


/**
 * @param requestCode 请求码
 */
inline fun <reified T : Activity> Activity.newIntent(bundle: Bundle? = null, requestCode: Int = -100) {
    val intent = Intent(this, T::class.java)
    if (bundle != null) {
        intent.putExtras(bundle)
    }
    if (requestCode != -100) startActivityForResult(intent, requestCode)
    else startActivity(intent)
}

inline fun <reified T : Activity> Fragment.newIntent(bundle: Bundle? = null, requestCode: Int = -100) {
    val intent = Intent(activity, T::class.java)
    if (bundle != null) {
        intent.putExtras(bundle)
    }
    if (requestCode != -100) startActivityForResult(intent, requestCode)
    else startActivity(intent)
}


/**
 * 动画共享 使用于5.0版本以上
 */
inline fun <reified T : Activity> Activity.newTransitionIntent(view: View?, bundle: Bundle? = null, requestCode: Int = -100) {
    val intent = Intent(this, T::class.java)
    if (bundle != null) {
        intent.putExtras(bundle)
    }
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        if (requestCode != -100) startActivityForResult(intent, requestCode)
        else startActivity(intent)
    } else {
        val transitionAnimation = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view!!, "secondSharedView")
        if (requestCode != -100) ActivityCompat.startActivityForResult(this, intent, requestCode, transitionAnimation.toBundle())
        else ActivityCompat.startActivity(this, intent, transitionAnimation.toBundle())
    }
}

inline fun <reified T : Activity> Fragment.newTransitionIntent(view: View?, bundle: Bundle? = null, requestCode: Int = -100) {
    val intent = Intent(activity, T::class.java)
    if (bundle != null) {
        intent.putExtras(bundle)
    }
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        if (requestCode != -100) startActivityForResult(intent, requestCode)
        else startActivity(intent)
    } else {
        val transitionAnimation = ActivityOptionsCompat.makeSceneTransitionAnimation(activity!!, view!!, "secondSharedView")
        if (requestCode != -100) ActivityCompat.startActivityForResult(activity!!, intent, requestCode, transitionAnimation.toBundle())
        else ActivityCompat.startActivity(activity!!, intent, transitionAnimation.toBundle())
    }
}


inline fun tryCatch(tryBlock: () -> Unit, catchBlock: (Throwable) -> Unit) {
    try {
        tryBlock()
    } catch (e: Exception) {
        e.printStackTrace()
        catchBlock(e)
    }
}

/**
 * 图片加载
 */
/*fun ImageView.loadImg(path: Any?, error: Int = R.mipmap.head_portrait_default) {
    GlideApp.with(this.context)
            .load(path)
            .placeholder(error)
            .error(error)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(this)
}*/

/**
 * 复制文字到剪切板
 */
fun String.copy(context: Context) {
    val mClipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    mClipboardManager.primaryClip = ClipData.newPlainText(null, this)
}

/**
 * 首行缩进两个字符
 */
fun TextView.textIndent2Char(content: String?) {
    if (content == null) {
        this.text = ""
        return
    }
    val span = SpannableStringBuilder("缩进$content")
    span.setSpan(ForegroundColorSpan(Color.TRANSPARENT), 0, 2,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
    this.text = span
}

/**
 * view是否显示 true 显示 false 隐藏
 */
fun View.setVisible(isVisible: Boolean, inVisible: Boolean = false) {
    if (inVisible) {
        this.visibility = View.INVISIBLE
        return
    }
    this.visibility = if (isVisible) View.VISIBLE else View.GONE
}

/**
 * 导入view
 */
fun Context.getQuickLayoutInflater(@LayoutRes resId: Int): View? = LayoutInflater.from(this).inflate(resId, null)


/**
 * 获取图片
 */
fun Context.getQuickDrawable(@DrawableRes resId: Int): Drawable? = ContextCompat.getDrawable(this, resId)

/**
 * 获取颜色
 */
fun Context.getQuickColor(@ColorRes resId: Int): Int = ContextCompat.getColor(this, resId)

fun EditText.limitMaxCount(max: Int, text: TextView, fromat: String = "已输入%d字,还剩%d字可输入") {

    text.text = String.format(fromat, this.text?.length, max - this.text!!.length)
    this.filters = arrayOf(InputFilter.LengthFilter(max))

    this.addTextChangedListener(object : TextWatcher {

        override fun afterTextChanged(s: Editable?) {

            text.text = String.format(fromat, s?.length, max - s!!.length)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    })
}

