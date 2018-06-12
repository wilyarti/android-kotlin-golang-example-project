# android-kotlin-golang-example-project
A example project ready to build that has a basic call back and multithreaded UI model in place.

To build model go into the gomobile/ directory and type "gomobile bind" to generate the .aar library. 
You will need to do this every time you make modifications to the gomobile/gomobile.go file.

For more info on gomobile visit: https://godoc.org/golang.org/x/mobile/cmd/gomobile

The template currently uses Kotlin coroutines to launch the Go process:
```
button.setOnClickListener { //button listener
            // launch process on the UI thread
            // we need to do this to present the "toast"
            launch(UI) { 
                toast("Launching background process!") 
                // get our return code
                // run our process on a background thread (CommonPool)
                // we need to do this because if our process is blocking
                // the UI thread will freeze until it returns.
                val i = withContext(CommonPool) { Gomobile.testCall() }
            }

}

```

In order to get the UI to update we need to shuffle data from the CommonPool thread to the UI thread.
We need to do this because the Go process is launched on the CommonPool thread and cannot access the UI.

To do this the GoCallBack.kt file has the following code:
```
// run on UI thread
 context.runOnUiThread(java.lang.Runnable {
                // update textView inside the UI thread
                context.textView.append(d)
})
```

That is all there is to it. The code is not really sophistated but it is good enough to port CLI apps to Android
