package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.Serializable


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private val dataMap = HashMap<Long, NotificationData>()

    private lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            custom_button.buttonState = ButtonState.Clicked
            when (optionsGroup.checkedRadioButtonId) {
                R.id.downloadGlide -> {
                    download(URL_1, downloadGlide.text.toString())
                }
                R.id.downloadLoadApp -> {
                    download(URL_2, downloadLoadApp.text.toString())
                }
                R.id.downloadRetrofit -> {
                    download(URL_3, downloadRetrofit.text.toString())
                }
                else -> {
                    Toast.makeText(this, getString(R.string.label_no_selection_error), Toast.LENGTH_LONG).show()
                }
            }
        }

        notificationManager.createChannel(this, CHANNEL_ID, getString(R.string.app_name))
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            custom_button.buttonState = ButtonState.Completed

            val extras = if (dataMap.containsKey(id)) {
                dataMap[id]?.status = "Success"
                Bundle().apply {
                    putSerializable("data", dataMap[id])
                }
            } else {
                null
            }

            notificationManager.sendNotification(extras, id?.toInt()!!, context?.applicationContext!!)
        }
    }

    private fun download(url: String, text: String) {
        val request = DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        custom_button.buttonState = ButtonState.Loading
        downloadID = downloadManager.enqueue(request)
        dataMap[downloadID] = NotificationData(
            text, "Downloading"
        )
    }

    companion object {
        private const val URL_1 = "https://github.com/bumptech/glide"
        private const val URL_2 = "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val URL_3 = "https://github.com/square/retrofit"

        private const val CHANNEL_ID = "channelId"
    }
}

data class NotificationData(
    val name: String,
    var status: String
): Serializable
