package dev.iskandev.message_obtain_app.repository

import android.util.Log
import dev.iskandev.message_obtain_app.exception.BadResponseStatusException
import dev.iskandev.message_obtain_app.exception.MessageNotFoundException
import dev.iskandev.message_obtain_app.model.Message

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import org.json.JSONTokener
import java.lang.IllegalStateException
import java.net.HttpURLConnection
import java.net.URL

interface MessageRepository {
    suspend fun createMessage(): Result<Nothing?>
    suspend fun getMessage(): Result<Message>
}

private fun parse(json: String): Message {
    val jsonObj = JSONTokener(json).nextValue() as JSONObject
    val messageValue = jsonObj.get("value") as String
    val sha256 = jsonObj.get("sha2") as String
    return Message(messageValue, sha256)
}

class MessageRepositoryImpl : MessageRepository {
    // IP address of the server's page
    private val urlStr = "http://192.168.1.187:8080/message"

    override suspend fun createMessage(): Result<Nothing?> = withContext(Dispatchers.IO) {
        try {
            val client = OkHttpClient()
            val url = URL(urlStr)
            val request = Request.Builder()
                .url(url)
                .post("".toRequestBody())
                .build() // POST request
            val response = client.newCall(request).execute()

            if (response.code !in 200..299)
                Result.Error(BadResponseStatusException(response.code))

            Result.Success(null)

        } catch (e: Exception) {
            Log.e(MessageRepositoryImpl::class.qualifiedName, e.stackTraceToString())
            Result.Error(e)
        }
    }

    override suspend fun getMessage(): Result<Message> = withContext(Dispatchers.IO) {
        try {
            val client = OkHttpClient()
            val url = URL(urlStr)
            val request = Request.Builder()
                .url(url)
                .get()
                .build()
            val response = client.newCall(request).execute()

            if (response.code == HttpURLConnection.HTTP_NOT_FOUND)
                Result.Error(MessageNotFoundException())
            if (response.code !in 200..299)
                Result.Error(BadResponseStatusException(response.code))

            Result.Success(parse(response.body?.string() ?: ""))

        } catch(e: Exception) {
            Log.e(MessageRepositoryImpl::class.qualifiedName, e.stackTraceToString())
            Result.Error(IllegalStateException("Cannot connect to the server"))
        }
    }
}