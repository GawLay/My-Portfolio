package com.kyrie.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.io.InputStream

/**
 * Reads public objects out of Supabase Storage over plain HTTP.
 *
 * The bucket is public and the app never writes, so a deterministic URL plus OkHttp
 * covers everything the Supabase SDK would have given us.
 */
class SupabaseStorageClient(private val okHttpClient: OkHttpClient) {
    suspend fun exists(url: String): Boolean =
        withContext(Dispatchers.IO) {
            val request = Request.Builder().url(url).head().build()
            okHttpClient.newCall(request).execute().use { it.isSuccessful }
        }

    /**
     * Streams [url] into [consume] instead of buffering it, so a large PDF never has to
     * fit in memory. The stream is closed once [consume] returns.
     */
    suspend fun <T> download(
        url: String,
        consume: (InputStream) -> T,
    ): T =
        withContext(Dispatchers.IO) {
            val request = Request.Builder().url(url).get().build()
            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException("Download failed with ${response.code} for $url")
                }
                val body = response.body ?: throw IOException("Empty response body for $url")
                consume(body.byteStream())
            }
        }
}
