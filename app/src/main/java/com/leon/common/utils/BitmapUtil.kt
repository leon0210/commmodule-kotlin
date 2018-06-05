package com.leon.common.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.ref.WeakReference

/**
 * 位图工具类
 *
 * @author jst
 */
object BitmapUtil {

    /**
     * 根据路径加载bitmap
     *
     * @param path
     * 路径
     * @param w
     * 款
     * @param h
     * 长
     * @return
     */
    fun convertToBitmap(path: String, w: Int, h: Int): Bitmap? {
        try {
            val opts = BitmapFactory.Options()
            // 设置为ture只获取图片大小
            opts.inJustDecodeBounds = true
            opts.inPreferredConfig = Bitmap.Config.ARGB_8888
            // 返回为空
            BitmapFactory.decodeFile(path, opts)
            val width = opts.outWidth
            val height = opts.outHeight
            var scaleWidth = 0f
            var scaleHeight = 0f
            if (width > w || height > h) {
                // 缩放
                scaleWidth = width.toFloat() / w
                scaleHeight = height.toFloat() / h
            }
            opts.inJustDecodeBounds = false
            val scale = Math.max(scaleWidth, scaleHeight)
            opts.inSampleSize = scale.toInt()
            val weak = WeakReference(BitmapFactory.decodeFile(path, opts))
            return Bitmap.createBitmap(weak.get(), 0, 0, weak.get()?.width!!, weak.get()!!.height, null, true)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }

    /**
     * 图片等比例压缩
     *
     * @param filePath
     * @param targetSize 期望的大小
     * @param reqWidth 期望的宽
     * @param reqHeight 期望的高
     * @return
     */
    fun decodeSampledBitmap(filePath: String,
                            reqWidth: Int = 1080,
                            reqHeight: Int = 1920
    ): Bitmap {
        // First decode with inJustDecodeBounds=true to check dimensions
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filePath, options)

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight)
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(filePath, options)
    }

    /**
     * 计算InSampleSize
     * 宽的压缩比和高的压缩比的较小值  取接近的2的次幂的值
     * 比如宽的压缩比是3 高的压缩比是5 取较小值3  而InSampleSize必须是2的次幂，取接近的2的次幂4
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private fun calculateInSampleSize(options: BitmapFactory.Options,
                                      reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            // Calculate ratios of height and width to requested height and
            // width
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            val ratio = if (heightRatio < widthRatio) heightRatio else widthRatio
            // inSampleSize只能是2的次幂  将ratio就近取2的次幂的值
            inSampleSize = when {
                ratio < 3 -> ratio
                ratio < 6.5 -> 4
                ratio < 8 -> 8
                else -> ratio
            }
        }
        return inSampleSize
    }

    /**
     * 图片缩放到指定宽高
     *
     * 非等比例压缩，图片会被拉伸
     *
     * @param bitmap 源位图对象
     * @param w 要缩放的宽度
     * @param h 要缩放的高度
     * @return 新Bitmap对象
     */
    fun zoomBitmap(bitmap: Bitmap, w: Int, h: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val matrix = Matrix()
        val scaleWidth = w.toFloat() / width
        val scaleHeight = h.toFloat() / height
        matrix.postScale(scaleWidth, scaleHeight)
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false)
    }

    /**
     * 质量压缩方法
     * @param image
     * @return file
     */
    fun compressBitmap(context: Context, bitmap: Bitmap, targetSize: Int = 1024): File {
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        var options = 90
        while (bos.toByteArray().size / 1024 > targetSize) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            bos.reset() // 重置baos即清空baos
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, bos)// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10// 每次都减少10
        }
//        BitmapFactory.decodeByteArray(bos.toByteArray(), 0, bos.toByteArray().size)//返回Bitmap

        val file = FileUtil.getCacheDirectory(context)
        val imgFile = File(file, "${System.currentTimeMillis()}.png")
        try {
            val out = FileOutputStream(imgFile)
            out.write(bos.toByteArray())
            out.flush()
            out.close()
            recycleBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return imgFile
    }

    //释放
    private fun recycleBitmap(vararg bitmaps: Bitmap) {
        for (bm in bitmaps) {
            if (!bm.isRecycled) {
                bm.recycle()
            }
        }
    }
}
