package com.udacity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val extrasData = intent.getBundleExtra("data")?.getSerializable("data")
        if (extrasData != null) {
            fileName.text = (extrasData as NotificationData).name
            statusName.text = extrasData.status
        }

        actionDone.setOnClickListener {
            finish()
        }
    }

}
