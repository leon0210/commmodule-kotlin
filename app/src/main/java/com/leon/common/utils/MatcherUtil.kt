package com.leon.common.utils

import android.text.TextUtils
import java.util.regex.Pattern

/**
 * Created by leon on 2017/6/2.
 */

object MatcherUtil {
    fun isPhone(phone: String): Boolean {
        val p = "^[1][345789]\\d{9}"
        if (TextUtils.isEmpty(phone)) {
            return false
        }
        return phone.matches(p.toRegex())
    }

    /**
     *@param inPutStr 需要匹配的内容
     * @param contentStr 总的内容
     */
    fun isExist(inPutStr: String, contentStr: String): Boolean {
        val pattern = Pattern.compile(inPutStr)
        val matcher = pattern.matcher(contentStr)
        return matcher.find()
    }
}
