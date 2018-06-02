package com.leon.common.utils

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.provider.MediaStore
import android.text.TextUtils
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel

/**
 * Created by 千里 on 2016/9/7.
 */
object FileUtil {

    /**
     * 检查SD卡是否存在
     */
    fun checkSdCard(): Boolean = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

    /**
     * 获取应用专属缓存目录
     * android 4.4及以上系统不需要申请SD卡读写权限
     * 因此也不用考虑6.0系统动态申请SD卡读写权限问题，切随应用被卸载后自动清空 不会污染用户存储空间

     * @param context 上下文
     * *
     * @param type    文件夹类型 可以为空，为空则返回API得到的一级目录
     * *
     * @return 缓存文件夹 如果没有SD卡或SD卡有问题则返回内存缓存目录，否则优先返回SD卡缓存目录
     */
    fun getCacheDirectory(context: Context, type: String = ""): File {
        var appCacheDir: File? = getExternalCacheDirectory(context, type)
        if (appCacheDir == null) {
            appCacheDir = getInternalCacheDirectory(context, type)
        }
        if (!appCacheDir.exists() && !appCacheDir.mkdirs()) {
            LogUtil.e("getCacheDirectory", "getCacheDirectory fail ,the reason is make directory fail !")
        }
        return appCacheDir
    }

    /**
     * 获取SD卡缓存目录

     * @param context 上下文
     * *
     * @param type    文件夹类型 如果为空则返回 /storage/emulated/0/Android/data/app_package_name/cache
     * *                否则返回对应类型的文件夹如Environment.DIRECTORY_PICTURES 对应的文件夹为 .../data/app_package_name/files/Pictures
     * *                [Environment.DIRECTORY_MUSIC],
     * *                [Environment.DIRECTORY_PODCASTS],
     * *                [Environment.DIRECTORY_RINGTONES],
     * *                [Environment.DIRECTORY_ALARMS],
     * *                [Environment.DIRECTORY_NOTIFICATIONS],
     * *                [Environment.DIRECTORY_PICTURES], or
     * *                [Environment.DIRECTORY_MOVIES].or 自定义文件夹名称
     * *
     * @return 缓存目录文件夹 或 null（无SD卡或SD卡挂载失败）
     */
    private fun getExternalCacheDirectory(context: Context, type: String): File? {
        var appCacheDir: File? = null
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            appCacheDir = if (TextUtils.isEmpty(type)) {
                context.externalCacheDir
            } else {
                context.getExternalFilesDir(type)
            }

            if (appCacheDir == null) {// 有些手机需要通过自定义目录
                appCacheDir = File(Environment.getExternalStorageDirectory(), "Android/data/" + context.packageName + "/cache/" + type)
            }

            if (!appCacheDir.exists() && !appCacheDir.mkdirs()) {
                LogUtil.e("getExternalDirectory", "getExternalDirectory fail ,the reason is make directory fail !")
            }
        } else {
            LogUtil.e("getExternalDirectory", "getExternalDirectory fail ,the reason is sdCard nonexistence or sdCard mount fail !")
        }
        return appCacheDir
    }

    /**
     * 获取内存缓存目录

     * @param type 子目录，可以为空，为空直接返回一级目录
     * *
     * @return 缓存目录文件夹 或 null（创建目录文件失败）
     * * 注：该方法获取的目录是能供当前应用自己使用，外部应用没有读写权限，如 系统相机应用
     */
    private fun getInternalCacheDirectory(context: Context, type: String): File {
        val appCacheDir: File? = if (TextUtils.isEmpty(type)) {
            context.cacheDir// /data/data/app_package_name/cache
        } else {
            File(context.filesDir, type)// /data/data/app_package_name/files/type
        }

        if (!appCacheDir!!.exists() && !appCacheDir.mkdirs()) {
            LogUtil.e("getInternalDirectory", "getInternalDirectory fail ,the reason is make directory fail !")
        }
        return appCacheDir
    }

    /**
     * 获取指定文件夹

     * @param files
     * *
     * @return
     * *
     * @throws Exception
     */
    @Throws(Exception::class)
    fun getFileSizes(files: File): Long {
        val flist = files.listFiles()
        return flist.indices
                .map {
                    if (flist[it].isDirectory) {
                        getFileSizes(flist[it])
                    } else {
                        getFileSize(flist[it])
                    }
                }
                .sum()
    }

    /**
     * 获取指定文件大小

     * @param file
     * *
     * @return
     * *
     * @throws Exception
     */
    @Throws(Exception::class)
    fun getFileSize(file: File): Long {
        var size: Long = 0
        if (file.exists()) {
            val fis: FileInputStream?
            fis = FileInputStream(file)
            size = fis.available().toLong()
        } else {
//            file.createNewFile()
            LogUtil.e("file", "文件不存在!")
        }
        return size
    }

    /**
     * 剪切单个文件
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * *
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * *
     * @return boolean
     */
    fun cutFile(oldPath: String, newPath: String) {
        var fi: FileInputStream? = null
        var fo: FileOutputStream? = null
        var `in`: FileChannel? = null
        var out: FileChannel? = null
        var oldFile: File? = null
        try {
            oldFile = File(oldPath)
            if (!oldFile.exists()) {
                LogUtil.d("file", "文件不存在")
                return
            }
            fi = FileInputStream(oldPath)
            fo = FileOutputStream(newPath)
            `in` = fi.channel//得到对应的文件通道
            out = fo.channel//得到对应的文件通道
            `in`!!.transferTo(0, `in`.size(), out)//连接两个通道，并且从in通道读取，然后写入out通道
        } catch (e: IOException) {
            e.printStackTrace()
            LogUtil.d("file", e.message)
        } finally {
            try {
                fi!!.close()
                `in`!!.close()
                fo!!.close()
                out!!.close()
                if (oldFile != null) {
                    oldFile.delete()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 删除文件
     * @param path文件路径
     */
    fun delFile(path: String?) {
        try {
            val file = File(path)
            if (file.exists()) file.delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 获取视频文件的缩略图

     * @param filePath
     * *
     * @param width
     * *
     * @param height
     * *
     * @param kind
     * *
     * @return
     */
    fun getVidioBitmap(filePath: String, width: Int, height: Int,
                       kind: Int): Bitmap {
        //定義一個Bitmap對象bitmap；
        var bitmap: Bitmap? = ThumbnailUtils.createVideoThumbnail(filePath, kind)

        //ThumbnailUtils類的截取的圖片是保持原始比例的，但是本人發現顯示在ImageView控件上有时候有部分沒顯示出來；
        //調用ThumbnailUtils類的靜態方法createVideoThumbnail獲取視頻的截圖；

        //調用ThumbnailUtils類的靜態方法extractThumbnail將原圖片（即上方截取的圖片）轉化為指定大小；
        //最後一個參數的具體含義我也不太清楚，因為是閉源的；
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT)
        //放回bitmap对象；
        return bitmap
    }

    /**
     * 获取手机SD卡总空间
     * 版本号大于18
     */
    private val sDcardTotalSize: Long
        get() {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                if (checkSdCard()) {
                    val path = Environment.getExternalStorageDirectory()
                    val mStatFs = StatFs(path.path)
                    val blockSizeLong = mStatFs.blockSizeLong
                    val blockCountLong = mStatFs.blockCountLong
                    blockSizeLong * blockCountLong
                } else 0
            } else -1
        }
    /**
     * 获取SDka可用空间
     */
    private val sdCardAvailableSize: Long
        get() {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                if (checkSdCard()) {
                    val path = Environment.getExternalStorageDirectory()
                    val mStatFs = StatFs(path.path)
                    val blockSizeLong = mStatFs.blockSizeLong
                    val availableBlocksLong = mStatFs.availableBlocksLong
                    blockSizeLong * availableBlocksLong
                } else 0
            } else -1
        }
    /**
     * 获取手机存储总空间
     */
    val phoneTotalSize: Long
        get() {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                return if (!checkSdCard()) {
                    val path = Environment.getDataDirectory()
                    val mStatFs = StatFs(path.path)
                    val blockSizeLong = mStatFs.blockSizeLong
                    val blockCountLong = mStatFs.blockCountLong
                    blockSizeLong * blockCountLong
                } else {
                    sDcardTotalSize
                }
            } else -1
        }
    /**
     * 获取手机存储可用空间
     */
    val phoneAvailableSize: Long
        get() {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                if (!checkSdCard()) {
                    val path = Environment.getDataDirectory()
                    val mStatFs = StatFs(path.path)
                    val blockSizeLong = mStatFs.blockSizeLong
                    val availableBlocksLong = mStatFs.availableBlocksLong
                    blockSizeLong * availableBlocksLong
                } else sdCardAvailableSize
            } else -1
        }


    /**
     * uri转换为文件path

     * @param context
     * *
     * @param uri
     * *
     * @return
     */
    fun getRealFilePath(context: Context, uri: Uri?): String? {
        if (null == uri) return null
        val scheme = uri.scheme
        var data: String? = null
        if (scheme == null)
            data = uri.path
        else if (ContentResolver.SCHEME_FILE == scheme) {
            data = uri.path
        } else if (ContentResolver.SCHEME_CONTENT == scheme) {
            val cursor = context.contentResolver.query(uri, arrayOf(MediaStore.Images.ImageColumns.DATA), null, null, null)
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                    if (index > -1) {
                        data = cursor.getString(index)
                    }
                }
                cursor.close()
            }
        }
        return data
    }

    /**
     * 文件图片路径转为Uri

     * @param context
     * *
     * @param imageFile
     * *
     * @return
     */
    fun getImageFileUri(context: Context, imageFile: File): Uri? {
        val filePath = imageFile.absolutePath
        val cursor = context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Images.Media._ID), MediaStore.Images.Media.DATA + "=? ",
                arrayOf(filePath), null)
        return if (cursor != null && cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID))
            val baseUri = Uri.parse("content://media/external/images/media")
            Uri.withAppendedPath(baseUri, "" + id)
        } else {
            if (imageFile.exists()) {
                val values = ContentValues()
                values.put(MediaStore.Images.Media.DATA, filePath)
                context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            } else {
                null
            }
        }
    }

    /**
     * 转换文件大小为字符串
     */
    fun transformFileSize(size: Long): String {
        when {
            size >= 1024 * 1024 * 1024 -> (size.toDouble() / (1024 * 1024 * 1024))
                    .let { return String.format("%.1f", it) + "G" }
            size >= 1024 * 1024 -> (size.toDouble() / (1024 * 1024))
                    .let { return String.format("%.1f", it) + "M" }
            size >= 1024 -> (size.toDouble() / 1024)
                    .let { return String.format("%.1f", it) + "K" }
        }
        return size.toInt().toString() + "B"
    }
}
