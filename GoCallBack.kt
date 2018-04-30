package com.wordpress.clinetworking.hashtree_mobile

import com.wordpress.clinetworking.hashtree_mobile.MainActivity
import hashfunc.JavaCallback
import android.app.Activity
import android.content.Context
import android.widget.ScrollView
import android.widget.TextView
import com.wordpress.clinetworking.hashtree_mobile.R.id.textView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class GoCallback(internal var context: Activity, internal var commoncontext: CommonPool) : JavaCallback {

    override final fun sendString(data: String) {
        try {
            // add a thread sleep or we will get random crashes
            // if the text being fed is too rapid
            //Thread.sleep(100)
            var d: CharSequence
            if (!data.contains("\n")) {
                d = data + "\n"
            } else {
                d = data
            }
            val i = data.length
            println("TXT: $data : $i")

            context.runOnUiThread(java.lang.Runnable {
                context.textView.append(d)
            })

        } catch (e: Exception) {
            print("Error updating UI: ")
            println(e)
        }
    }
}