package com.ingloriousengineers.workmanagerexample

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters

class FilterWorker(context: Context, param: WorkerParameters) : Worker(context, param) {

    private val TAG: String = FilterWorker::class.java.simpleName

    companion object {
        private const val LOG_TAG = "FilterWorker"
        const val KEY_IMAGE_URI = "IMAGE_URI"
        const val KEY_IMAGE_INDEX = "IMAGE_INDEX"
        private const val IMAGE_PATH_PREFIX = "IMAGE_PATH_"
    }

    override fun doWork(): Result = try{
        Thread.sleep(3000)
        Log.e(TAG, "in side success")
        val imageUri = inputData.getString(KEY_IMAGE_URI)
        val imageIndex = inputData.getInt(KEY_IMAGE_INDEX, 0)

        val bitmap = MediaStore.Images.Media.getBitmap(applicationContext.contentResolver, Uri.parse(imageUri))
        val filterBitmap = ImageUtils.applySepiaFilter(bitmap)
        val filterImageUri = ImageUtils.writeBitmapToFile(applicationContext, filterBitmap)

        val outputData = Data.Builder()
            .putString(IMAGE_PATH_PREFIX+imageIndex, filterImageUri.toString())
            .build()
        Log.e(TAG, "success")
        Result.success()
    } catch (e: Throwable) {
        Log.e(TAG, e.message)
        Result.failure()
    }
}
