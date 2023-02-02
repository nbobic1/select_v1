package ba.etf.us.myapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class TextReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        val text = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)
        Log.d("tekst1", text.toString())
        val readonly = intent.getBooleanExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, false)
        GlobalScope.launch {
            val result = apiCall(text.toString())
            Log.d("tekst",result)
            val intent = Intent()
            intent.putExtra(Intent.EXTRA_PROCESS_TEXT, result)
            setResult(AppCompatActivity.RESULT_OK,result,null)

        }
    }
    private suspend fun apiCall(text:String):String
    {
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = """{ "model": "text-davinci-003",
    pro"mpt": "$text",
    max"_tokens": "7",
    tem"perature": "0.7",
    "frequency_penalty": "0.5", }""".toRequestBody(mediaType)

        val request = Request.Builder()
            .url("https://api.openai.com/v1/completions")
            .addHeader("Authorization", "Bearer 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")
            .post(requestBody)
            .build()
        return withContext(Dispatchers.IO) {
            OkHttpClient().newCall(request).execute().use {
                it.body?.string() ?: ""
            }
        }
    }
}