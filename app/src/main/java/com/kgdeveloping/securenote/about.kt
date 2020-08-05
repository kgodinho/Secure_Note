package com.kgdeveloping.securenote

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.activity_note_update.*
import kotlinx.android.synthetic.main.activity_note_update.noteUpdateToolbar

class about : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        //Code for top bar
        noteUpdateToolbar.setNavigationIcon(R.drawable.ic_arrow_back_24px)
        noteUpdateToolbar.setNavigationOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        //make Text views clickable links
        tvURL.movementMethod = LinkMovementMethod.getInstance()
        tvLicenseURL.movementMethod = LinkMovementMethod.getInstance()
    }
}