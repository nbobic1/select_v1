package ba.etf.us.myapplication

import android.R.attr.label
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener

class RecieveText : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_recieve_text)
        val text = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)
        Log.d("tekst1", text.toString())
        val readonly = intent.getBooleanExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, false)
        GlobalScope.launch {
            //result je ono sto chatgpt vrati
            val result = apiCall(text.toString())
            val jsonObject = JSONTokener(result).nextValue() as JSONObject
            val choices1 = jsonObject.getString("choices")
            val choices = JSONTokener(choices1).nextValue() as JSONArray
            val response=choices.getJSONObject(0).getString("text")
            Log.i("tekst res: ", response)
            Log.d("tekst",result)

            val clipboard: ClipboardManager =getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Typi", response)
            clipboard.setPrimaryClip(clip)

            val intent = Intent()
            intent.putExtra(Intent.EXTRA_PROCESS_TEXT, "result")

        }
        setResult(RESULT_OK, intent)
        finish()
        }
    private suspend fun apiCall(text:String):String
    {
        val mediaType = "application/json".toMediaType()
        val requestBody = """{ "model": "text-davinci-003",
    "prompt": "$text",
    "max_tokens": 7,
    "temperature": 0.7,
    "frequency_penalty": 0.5 }""".toRequestBody(mediaType)
Log.d("key",requestBody.toString())
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