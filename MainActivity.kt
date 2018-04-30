package com.wordpress.clinetworking.hashtree_mobile

import com.wordpress.clinetworking.hashtree_mobile.GoCallback
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
import hashfunc.Hashfunc
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

import java.io.*
import java.nio.file.Paths


class MainActivity : AppCompatActivity() {
    // make categories global
    val categories = ArrayList<String>()
    // get external dir
    var extStore = Environment.getExternalStorageDirectory();
    var w = extStore.toString() + "/Workspace"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // get permissions
        val permissions = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        ActivityCompat.requestPermissions(this, permissions, 0)
        // spinner
        val spinner: Spinner = findViewById(R.id.spinner)
        categories.add("PRESS LIST")
        val dataAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        spinner.adapter = dataAdapter
        //// end

        // scrollview
        val tview: TextView = findViewById(R.id.textView)
        val smm = ScrollingMovementMethod()
        tview.movementMethod = smm
        // scroll.setMovement

        // callback
        val gocb = GoCallback(this, CommonPool)
        Hashfunc.registerJavaCallback(gocb)

        // progress thingy
        val prog: ProgressBar = findViewById(R.id.progressBar)
        prog.visibility = View.INVISIBLE

        // setting menu
        button.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        button2.setOnClickListener {
            if (setupPermissions()) {
                button2.setEnabled(false)
                button3.setEnabled(false)
                button4.setEnabled(false)
                prog.visibility = View.VISIBLE

                var server = PreferenceManager.getDefaultSharedPreferences(this).getString("text_server", "")
                var accesskey = PreferenceManager.getDefaultSharedPreferences(this).getString("text_accesskey", "")
                var secretkey = PreferenceManager.getDefaultSharedPreferences(this).getString("text_secretkey", "")
                var enckey = PreferenceManager.getDefaultSharedPreferences(this).getString("text_enckey", "")
                var bucket = PreferenceManager.getDefaultSharedPreferences(this).getString("text_bucket", "")
                var secure = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("example_switch", true)
                button2.setEnabled(false)
                toast("Fetching snapshotlist...")
                List(server, accesskey, secretkey, enckey, bucket, secure, w)
            } else {
                toast("Need storage permission enabled!")
            }
        }
        button3.setOnClickListener {
            if (setupPermissions()) {
                toast("Starting download...")
                button2.setEnabled(false)
                button3.setEnabled(false)
                button4.setEnabled(false)
                prog.visibility = View.VISIBLE
                // make sure dest dir exists
                val parent = File(w)
                if (!parent.exists() && !parent.mkdirs()) {
                    toast("Couldn't create dir for $w.")
                    throw IllegalStateException("Couldn't create dir: " + parent);
                } else {
                    // variables
                    var server = PreferenceManager.getDefaultSharedPreferences(this).getString("text_server", "")
                    var accesskey = PreferenceManager.getDefaultSharedPreferences(this).getString("text_accesskey", "")
                    var secretkey = PreferenceManager.getDefaultSharedPreferences(this).getString("text_secretkey", "")
                    var enckey = PreferenceManager.getDefaultSharedPreferences(this).getString("text_enckey", "")
                    var bucket = PreferenceManager.getDefaultSharedPreferences(this).getString("text_bucket", "")
                    var secure = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("example_switch", true)
                    var nuke = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("example_switch2", false)
                    var snapshot_name: String
                    snapshot_name = spinner.selectedItem.toString()
                    val fileName: String = w + "/" + snapshot_name
                    Download(server, accesskey, secretkey, enckey, bucket, snapshot_name, fileName, secure, w, nuke)
                }

            } else {
                toast("Need storage permission enabled!")

            }
        }
        button4.setOnClickListener {
            if (setupPermissions()) {
                val parent = File(w)
                if (!parent.exists() && !parent.mkdirs()) {
                    toast("Couldn't create dir for $w.")
                    throw IllegalStateException("Couldn't create dir: " + parent);
                } else {
                    toast("Starting upload...")
                    button2.setEnabled(false)
                    button3.setEnabled(false)
                    button4.setEnabled(false)
                    prog.visibility = View.VISIBLE

                    // variables
                    var server = PreferenceManager.getDefaultSharedPreferences(this).getString("text_server", "")
                    var accesskey = PreferenceManager.getDefaultSharedPreferences(this).getString("text_accesskey", "")
                    var secretkey = PreferenceManager.getDefaultSharedPreferences(this).getString("text_secretkey", "")
                    var enckey = PreferenceManager.getDefaultSharedPreferences(this).getString("text_enckey", "")
                    var bucket = PreferenceManager.getDefaultSharedPreferences(this).getString("text_bucket", "")
                    var secure = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("example_switch", true)
                    var snapshot_name: String
                    snapshot_name = spinner.selectedItem.toString()
                    val fileName: String = w + "/" + snapshot_name
                    Upload(server, accesskey, secretkey, enckey, bucket, snapshot_name, fileName, secure, w)
                }
            } else {
                toast("Need storage permission enabled!")
            }

        }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {}
        }
    }

    // creates toast messages to be displayed
    public fun Context.toast(message: CharSequence) =
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    // needed to get spinner to select from generated list
    private fun getTaskType(spinner: Spinner): String {

        var tasks = "no text"

        // Spinner click listener
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                tasks = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                //do nothing
            }
        }

        return tasks
    }

    public fun scrollToBottom() {
        //scrollbar
        /// auto scroll
        var mTextStatus: TextView = findViewById(R.id.textView)
        var mScrollView: ScrollView = findViewById(R.id.scrollView)
        mScrollView.smoothScrollBy(0, mTextStatus.getBottom())
        mScrollView.post(Runnable() {
            fun run() {
                mScrollView.smoothScrollBy(0, mTextStatus.getBottom())
            }
        })
    }

    private fun setupPermissions(): Boolean {
        val permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            return false
            toast("ERROR: Permission Denied!")
        } else {
            return true
        }
    }

    fun Context.test() {
        fun sleeper(i: Int): String {
            Thread.sleep(100)
            return "foo $i\n"
        }
        for (i in 0..100) {
            var f = sleeper(i)
            textView.append(f)
        }
        button3.setEnabled(true)
    }

    fun test() = launch(UI) {

        for (i in 0..100) {
            Thread.sleep(100)
            textView.append("counting $i\n")
        }
        toast("job done")
    }

    fun Context.Download(server: String, accesskey: String, secretkey: String, enckey: String, bucket: String, snapshot_name: String, fileName: String, secure: Boolean, w: String, nuke: Boolean) {
        try {
            launch(UI) {
                val i = withContext(CommonPool) {
                    Hashfunc.hashseed(server, accesskey, secretkey, enckey, snapshot_name, bucket, secure, w, nuke)
               }
                if (i) {
                    toast("Downloads successful!")
                } else {
                    toast("Downloads failed!")
                }
                button2.setEnabled(true)
                button3.setEnabled(true)
                button4.setEnabled(true)
                progressBar.visibility = View.INVISIBLE
            }
        } catch (e: Exception) {
            print(e.toString())
        }

    }

    fun Context.List(server: String, accesskey: String, secretkey: String, enckey: String, bucket: String, secure: Boolean, w: String) = async(UI) {
        try {
            launch(UI) {
                var h = withContext(CommonPool) { Hashfunc.hashlist(server, secure, accesskey, secretkey, bucket) }
                if (h != "ERROR") {
                    var snapshots = h.lines()
                    snapshots.forEach {
                        var fpath = w + "/" + it
                        if (!it.isBlank()) {
                            categories.add(it)
                        }
                    }
                }
                button2.setEnabled(true)
                button3.setEnabled(true)
                button4.setEnabled(true)
                progressBar.visibility = View.INVISIBLE
            }
        } catch (e: Exception) {
            print(e.toString())
        }
    }

    fun Context.readdb(fileName: String): MutableMap<String, MutableList<String>> {
// using extension function readLines
        var hash = ""
        var fpath = ""
        var remotedb = mutableMapOf<String, MutableList<String>>()
        var dec = 0
        try {
            File(fileName).readLines().forEach {
                var regex = "^--- .*".toRegex()
                var regex1 = "^---".toRegex()
                var regex2 = "^- .*".toRegex()

                if (regex.matches(it)) {
                    val re = "^--- ".toRegex()
                    hash = re.replace(it, "")
                    // reset decrement
                    dec = 0
                } else if (regex1.matches(it)) {
                    // set decrement
                    dec = 1;
                }
                // only processs if decremented
                if (dec == 1) {
                    if (regex2.matches(it)) {
                        // remove formatting
                        val re = "^- ".toRegex()
                        fpath = re.replace(it, "")
                        // append to map
                        remotedb[hash] = mutableListOf()
                        remotedb[hash]?.add(fpath)
                    }

                }
            }
            return remotedb
        } catch (t: Throwable) {
            toast(t.toString())
        }
        return remotedb
    }

    fun Context.Upload(server: String, accesskey: String, secretkey: String, enckey: String, bucket: String, snapshot_name: String, fileName: String, secure: Boolean, w: String) = async(UI) {
        try {
            launch(UI) {
                val i = withContext(CommonPool) { Hashfunc.hashtree(server, accesskey, secretkey, enckey, bucket, secure, w) }
                if (i) {
                    toast("Uploads successful!")
                } else {
                    toast("Failed to upload!")
                }
                button2.setEnabled(true)
                button3.setEnabled(true)
                button4.setEnabled(true)
                progressBar.visibility = View.INVISIBLE
            }
        } catch (e: Exception) {
            print(e.toString())
        }
    }
}

