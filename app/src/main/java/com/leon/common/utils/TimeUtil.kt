package com.zhizhimei.shiyi.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by hcc on 16/9/20 16:49
 * 100332338@qq.com
 *
 *
 * 常用时间工具类
 */
object TimeUtil {

    private val formatDate = SimpleDateFormat("yyyy-MM-dd",
            Locale.getDefault())

    private val formatDay = SimpleDateFormat("d", Locale.getDefault())

    private val formatMonthDay = SimpleDateFormat("M-d", Locale.getDefault())

    private val formatDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
            Locale.getDefault())


    fun restTime(time: String): String = time.replace("T", " ")

    /**
     * 格式化日期
     *
     * @return 年月日
     */
    private fun formatDate(date: Date): String {

        return formatDate.format(date)
    }


    /**
     * 格式化日期
     *
     * @return 年月日 时分秒
     */
    private fun formatDateTime(date: Date): String {

        return formatDateTime.format(date)
    }


    /**
     * 将时间戳解析成日期
     *
     * @return 年月日
     */
    fun parseDate(timeInMillis: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        val date = calendar.time
        return formatDate(date)
    }


    /**
     * 将时间戳解析成日期
     *
     * @return 年月日 时分秒
     */
    fun parseDateTime(timeInMillis: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        val date = calendar.time
        return formatDateTime(date)
    }


    /**
     * 解析日期
     */
    fun parseDate(date: String): Date? {
        var mDate: Date? = null
        try {
            mDate = formatDate.parse(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return mDate
    }


    /**
     * 解析日期
     */
    private fun parseDateTime(datetime: String): Date? {

        var mDate: Date? = null
        try {
            mDate = formatDateTime.parse(datetime)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return mDate
    }


    /**
     * 以友好的方式显示时间
     */
    fun friendlyTime(sdate: String): String {

        val time = parseDateTime(sdate) ?: return "Unknown"
        var ftime = ""
        val cal = Calendar.getInstance()

        // 判断是否是同一天
        val curDate = formatDate.format(cal.time)
        val paramDate = formatDate.format(time)
        if (curDate == paramDate) {
            val hour = ((cal.timeInMillis - time.time) / 3600000).toInt()
            ftime = if (hour == 0) {
                Math.max(
                        (cal.timeInMillis - time.time) / 60000, 1).toString() + "分钟前"
            } else {
                hour.toString() + "小时前"
            }
            return ftime
        }

        val lt = time.time / 86400000
        val ct = cal.timeInMillis / 86400000
        val days = (ct - lt).toInt()
        when {
            days == 0 -> {
                val hour = ((cal.timeInMillis - time.time) / 3600000).toInt()
                ftime = if (hour == 0) {
                    Math.max((cal.timeInMillis - time.time) / 60000, 1).toString() + "分钟前"
                } else hour.toString() + "小时前"
            }
            days == 1 -> ftime = "昨天"
            days == 2 -> ftime = "前天"
            days in 3..10 -> ftime = days.toString() + "天前"
            days > 10 -> ftime = formatDate.format(time)
        }
        return ftime
    }

    /**
     * 计算时间差
     *
     * @param starTime 开始时间
     * @param endTime  结束时间
     * @param d        返回类型 0：天时   1：年
     * @return 返回时间差
     */
    fun getTimeDifference(starTime: String, endTime: String, type: Int = -1): String {
        var timeString = ""
        try {
            val parse = parseDate(starTime)
            val parse1 = parseDate(endTime)
            val diff = parse1?.time!! - parse?.time!!
            val day = diff / (24 * 60 * 60 * 1000)
            val hour = diff / (60 * 60 * 1000) - day * 24
//            val min = diff / (60 * 1000) - day * 24 * 60 - hour * 60
            timeString = when (type) {
                0 -> "${day}天${hour}小时"
                1 -> {//年
                    if (day % 365 >= 180) {
                        "${(day / 365) + 1}"
                    } else "${day / 365}"
                }
                else -> "$day"
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return timeString

    }

    /**
     * 根据日期获取当期是周几
     */
    fun getWeek(date: Date): String {

        val weeks = arrayOf("周日", "周一", "周二", "周三", "周四", "周五", "周六")
        val cal = Calendar.getInstance()
        cal.time = date
        var week_index = cal.get(Calendar.DAY_OF_WEEK) - 1
        if (week_index < 0) {
            week_index = 0
        }
        return weeks[week_index]
    }

    fun getTimeWithIntervalDay(day : Int): Date{
        val theCa = Calendar.getInstance()
        theCa.time = Date()
        theCa.add(Calendar.DATE, day)//最后一个数字30可改，30天的意思

        return theCa.time
    }
}
