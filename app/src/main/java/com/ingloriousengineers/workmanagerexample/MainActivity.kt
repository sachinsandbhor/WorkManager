package com.ingloriousengineers.workmanagerexample

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.ingloriousengineers.workmanagerexample.FilterWorker.Companion.KEY_IMAGE_INDEX
import com.ingloriousengineers.workmanagerexample.FilterWorker.Companion.KEY_IMAGE_URI
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val GALLERY_REQUEST_CODE = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button.setOnClickListener {
            val intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, GALLERY_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val uri: Uri = data.getData();
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            imageView.setImageBitmap(bitmap)

            val applySepiaFilter = buildSepiaFilterRequests(data)
            val workManager = WorkManager.getInstance()
            workManager.beginWith(applySepiaFilter).enqueue()
        }
    }

    private fun buildSepiaFilterRequests(data: Intent): List<OneTimeWorkRequest> {
        val filterRequests = mutableListOf<OneTimeWorkRequest>()

        data.clipData?.run {
            for(i in 0 until itemCount) {
                val imageUri = getItemAt(i)

                val filterRequest = OneTimeWorkRequest.Builder(FilterWorker::class.java)
                    .setInputData(buildInputDataForFilter(imageUri as Uri, i))
                    .build()

                filterRequests.add(filterRequest)
            }
        }

        data.data?.run {
            val filterWorkRequest = OneTimeWorkRequest.Builder(FilterWorker::class.java)
                .setInputData(buildInputDataForFilter(this, 0))
                .build()

            filterRequests.add(filterWorkRequest)
        }
        return filterRequests
    }

    private fun buildInputDataForFilter(uri: Uri?, i: Int): Data {
        val builder = Data.Builder()
        if(uri != null) {
            builder.putString(KEY_IMAGE_URI, uri.toString())
            builder.putInt(KEY_IMAGE_INDEX, i)
        }
        return builder.build()
    }
}
