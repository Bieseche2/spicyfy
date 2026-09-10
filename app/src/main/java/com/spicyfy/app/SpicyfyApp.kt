package com.spicyfy.app

import android.app.Application
import com.spicyfy.app.extractor.DownloaderImpl
import org.schabi.newpipe.extractor.NewPipe

class SpicyfyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        NewPipe.init(DownloaderImpl())
    }
}
