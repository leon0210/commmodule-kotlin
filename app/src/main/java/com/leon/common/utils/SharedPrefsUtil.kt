package com.leon.common.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.text.TextUtils
import android.util.Base64
import java.io.*

/**
 * Created by Administrator on 2017/3/13.
 */

object SharedPrefsUtil {

    /**
     * 向SharedPreferences中写入int类型数据
     *
     * @param context 上下文环境
     * @param name    对应的xml文件名称
     * @param key     键
     * @param value   值
     */
    fun putValue(context: Context, name: String, key: String,
                 value: Int) {
        val sp = getEditor(context.applicationContext, name)
        sp.putInt(key, value)
        sp.commit()
    }

    /**
     * 向SharedPreferences中写入boolean类型的数据
     *
     * @param context 上下文环境
     * @param name    对应的xml文件名称
     * @param key     键
     * @param value   值
     */
    fun putValue(context: Context, name: String, key: String,
                 value: Boolean) {
        val sp = getEditor(context.applicationContext, name)
        sp.putBoolean(key, value)
        sp.commit()
    }

    /**
     * 向SharedPreferences中写入String类型的数据
     *
     * @param context 上下文环境
     * @param name    对应的xml文件名称
     * @param key     键
     * @param value   值
     */
    fun putValue(context: Context, name: String, key: String,
                 value: String) {
        val sp = getEditor(context.applicationContext, name)
        sp.putString(key, value)
        sp.commit()
    }

    /**
     * 向SharedPreferences中写入float类型的数据
     *
     * @param context 上下文环境
     * @param name    对应的xml文件名称
     * @param key     键
     * @param value   值
     */
    fun putValue(context: Context, name: String, key: String,
                 value: Float) {
        val sp = getEditor(context.applicationContext, name)
        sp.putFloat(key, value)
        sp.commit()
    }

    /**
     * 向SharedPreferences中写入long类型的数据
     *
     * @param context 上下文环境
     * @param name    对应的xml文件名称
     * @param key     键
     * @param value   值
     */
    fun putValue(context: Context, name: String, key: String,
                 value: Long) {
        val sp = getEditor(context.applicationContext, name)
        sp.putLong(key, value)
        sp.commit()
    }


    /**
     * 存储List集合
     *
     * @param context 上下文
     * @param key     存储的键
     * @param value   存储的集合
     */
    fun putList(context: Context, name: String, key: String, value: List<Serializable>) {
        try {
            put(context, name, key, value)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 存储Map集合
     *
     * @param context 上下文
     * @param key     键
     * @param map     存储的集合
     * @param <K>     指定Map的键
     * @param <V>     指定Map的值
    </V></K> */
    fun <K : Serializable, V : Serializable> putMap(context: Context, name: String, key: String, map: Map<K, V>) {
        try {
            put(context, name, key, map)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 存储对象
     */
    @Throws(IOException::class)
    fun put(context: Context, name: String, key: String, obj: Any?) {
        val sp = getEditor(context.applicationContext, name)
        if (obj == null) {//判断对象是否为空
            return
        }
        val baos = ByteArrayOutputStream()
        val oos: ObjectOutputStream?
        oos = ObjectOutputStream(baos)
        oos.writeObject(obj)
        // 将对象放到OutputStream中
        // 将对象转换成byte数组，并将其进行base64编码
        val objectStr = String(Base64.encode(baos.toByteArray(), Base64.DEFAULT))
        baos.close()
        oos.close()
        sp.putString(key, objectStr)
        sp.commit()
    }

    /**
     * 获取List集合
     *
     * @param context 上下文
     * @param key     键
     * @param <E>     指定泛型
     * @return List集合
    </E> */
    fun <E : Serializable> getList(context: Context, name: String, key: String): List<*>? {
        try {
            return get(context, name, key) as List<*>?
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    /**
     * 获取Map集合
     *
     * @param context
     * @param name
     * @param key
     * @param <K>
     * @param <V>
     * @return
    </V></K> */
    fun <K : Serializable, V : Serializable> getMap(context: Context, name: String, key: String): Map<*, *>? {
        try {
            return get(context, name, key) as Map<*, *>?
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    /**
     * 获取对象
     */
    @Throws(IOException::class, ClassNotFoundException::class)
    fun get(context: Context, name: String, key: String): Any? {
        try {
            val wordBase64 = getValue(context, name, key, "")
            // 将base64格式字符串还原成byte数组
            if (TextUtils.isEmpty(wordBase64)) { //不可少，否则在下面会报java.io.StreamCorruptedException
                return null
            }
            val objBytes = Base64.decode(wordBase64.toByteArray(), Base64.DEFAULT)
            val bais = ByteArrayInputStream(objBytes)
            val ois = ObjectInputStream(bais)
            // 将byte数组转换成product对象
            val obj = ois.readObject()
            bais.close()
            ois.close()
            return obj
        } catch (e: Exception) {
            return null
        }
    }

    /**
     * 从SharedPreferences中读取int类型的数据
     *
     * @param context  上下文环境
     * @param name     对应的xml文件名称
     * @param key      键
     * @param defValue 如果读取不成功则使用默认值
     * @return 返回读取的值
     */
    fun getValue(context: Context, name: String, key: String,
                 defValue: Int): Int {
        val sp = getSharedPreferences(context.applicationContext, name)
        return sp.getInt(key, defValue)
    }

    /**
     * 从SharedPreferences中读取boolean类型的数据
     *
     * @param context  上下文环境
     * @param name     对应的xml文件名称
     * @param key      键
     * @param defValue 如果读取不成功则使用默认值
     * @return 返回读取的值
     */
    fun getValue(context: Context, name: String, key: String,
                 defValue: Boolean): Boolean {
        val sp = getSharedPreferences(context.applicationContext, name)
        return sp.getBoolean(key, defValue)
    }

    /**
     * 从SharedPreferences中读取String类型的数据
     *
     * @param context  上下文环境
     * @param name     对应的xml文件名称
     * @param key      键
     * @param defValue 如果读取不成功则使用默认值
     * @return 返回读取的值
     */
    fun getValue(context: Context, name: String, key: String,
                 defValue: String): String {
        val sp = getSharedPreferences(context.applicationContext, name)
        return sp.getString(key, defValue)
    }

    /**
     * 从SharedPreferences中读取float类型的数据
     *
     * @param context  上下文环境
     * @param name     对应的xml文件名称
     * @param key      键
     * @param defValue 如果读取不成功则使用默认值
     * @return 返回读取的值
     */
    fun getValue(context: Context, name: String, key: String,
                 defValue: Float): Float {
        val sp = getSharedPreferences(context.applicationContext, name)
        return sp.getFloat(key, defValue)
    }

    /**
     * 从SharedPreferences中读取long类型的数据
     *
     * @param context  上下文环境
     * @param name     对应的xml文件名称
     * @param key      键
     * @param defValue 如果读取不成功则使用默认值
     * @return 返回读取的值
     */
    fun getValue(context: Context, name: String, key: String,
                 defValue: Long): Long {
        val sp = getSharedPreferences(context.applicationContext, name)
        return sp.getLong(key, defValue)
    }


    /**
     * 清除指定文件对应的key
     * @param context
     * @param name
     * @param key
     */
    fun clearValue(context: Context, name: String, key: String) {
        val sp = getEditor(context.applicationContext, name)
        //sp.putLong(key, value);
        sp.remove(key)
        sp.commit()
    }

    /**
     * 清空指定文件
     * @param context
     * @param name
     */
    fun deletelFile(context: Context, name: String) {
        val sp = getEditor(context.applicationContext, name)
        //sp.putLong(key, value);
        sp.clear().commit()
    }

    //获取Editor实例
    private fun getEditor(context: Context, name: String): Editor {
        return getSharedPreferences(context.applicationContext, name).edit()
    }

    //获取SharedPreferences实例
    private fun getSharedPreferences(context: Context, name: String): SharedPreferences {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE)
    }
}
