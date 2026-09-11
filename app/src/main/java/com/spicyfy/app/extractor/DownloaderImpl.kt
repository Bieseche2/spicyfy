package com.spicyfy.app.extractor

import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.schabi.newpipe.extractor.downloader.Downloader
import org.schabi.newpipe.extractor.downloader.Request
import org.schabi.newpipe.extractor.downloader.Response
import org.schabi.newpipe.extractor.exceptions.ReCaptchaException
import java.util.concurrent.TimeUnit

class DownloaderImpl : Downloader() {

    private val userAgent =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:140.0) Gecko/20100101 Firefox/140.0"

    private val client = OkHttpClient.Builder()
        .readTimeout(30, TimeUnit.SECONDS)
        .connectionSpecs(listOf(ConnectionSpec.RESTRICTED_TLS))
        .build()

    override fun execute(request: Request): Response {
        val httpMethod = request.httpMethod()
        val url = request.url()
        val headers = request.headers()
        val dataToSend = request.dataToSend()

        val requestBody = dataToSend?.toRequestBody()

        val requestBuilder = okhttp3.Request.Builder()
            .method(httpMethod, requestBody)
            .url(url)
            .addHeader("User-Agent", userAgent)

        headers.forEach { (headerName, headerValueList) ->
            requestBuilder.removeHeader(headerName)
            headerValueList.forEach { headerValue ->
                requestBuilder.addHeader(headerName, headerValue)
            }
        }

        client.newCall(requestBuilder.build()).execute().use { response ->
            if (response.code == 429) {
                throw ReCaptchaException("reCaptcha Challenge requested", url)
            }

            val responseBodyToReturn = response.body?.use { it.string() }

            return Response(
                response.code,
                response.message,
                response.headers.toMultimap(),
                responseBodyToReturn,
                response.request.url.toString()
            )
        }
    }
}
