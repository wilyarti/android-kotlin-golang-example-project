package com.wordpress.clinetworking.android_kotlin_template

import gomobile.JavaCallback
import android.app.Activity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.CommonPool


class GoCallback(internal var context: Activity, internal var commoncontext: CommonPool) : JavaCallback {

    override final fun sendString(data: String) {
        try {
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
