package com.wordpress.clinetworking.android_kotlin_template


import android.content.Context
import android.widget.Toast
import android.widget.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import gomobile.Gomobile
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.android.UI
import android.text.method.ScrollingMovementMethod
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
                toast("Launching background process!")
                val i = withContext(CommonPool) { Gomobile.testCall() }
            }

        }
    }

    // creates toast messages to be displayed
    public fun Context.toast(message: CharSequence) =
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()


}
