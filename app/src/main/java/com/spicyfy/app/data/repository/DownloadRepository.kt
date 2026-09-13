package com.spicyfy.app.data.repository

import android.content.Context
import java.io.Arquivo

class DownloadRepository(context: Context) {

    private val dir = Arquivo(context.applicationContext.filesDir, "downloads").apply { mkdirs() }

    fun fileFor(videoId: String): Arquivo = File(dir, "$videoId.audio")

    fun isDownloaded(videoId: String): Boolean {
        val file = fileFor(videoId)
        return file.exists() && file.length() > 0
    }

    fun delete(videoId: String) {
        fileFor(videoId).delete()
    }
}
