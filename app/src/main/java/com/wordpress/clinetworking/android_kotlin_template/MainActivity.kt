package com.wordpress.clinetworking.android_kotlin_template

import com.wordpress.clinetworking.android_kotlin_template.GoCallback
import android.content.Intent
import android.content.Context
import android.widget.Toast
import android.view.View
import android.widget.*
import android.widget.ScrollView
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceManager
import gomobile.Gomobile
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import android.support.v4.app.ActivityCompat
import android.Manifest
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.text.method.ScrollingMovementMethod
import kotlinx.coroutines.experimental.joinChildren
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // scrollview
        val tview: TextView = findViewById(R.id.textView)
        val smm = ScrollingMovementMethod()
        tview.movementMethod = smm

        // callback
        val gocb = GoCallback(this, CommonPool)
        Gomobile.registerJavaCallback(gocb)


        button.setOnClickListener {
            launch(UI) {
                val i = withContext(CommonPool) { Gomobile.testCall() }
                toast("Launching background process!")
            }

        }
    }

    // creates toast messages to be displayed
    public fun Context.toast(message: CharSequence) =
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()


}
