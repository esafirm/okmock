package nolambda.gadb.okmock.sample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_sample.*
import nolambda.gadb.okmock.OkMock
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.concurrent.thread

class SampleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        val client = OkHttpClient.Builder()
            .addInterceptor(OkMock())
            .build()

        btnRequest.setOnClickListener {
            client.request("https://google.com")
        }
    }

    private fun OkHttpClient.request(url: String) {
        val request = Request.Builder()
            .url(url)
            .build()

        thread {
            log("Requesting…")
            try {
                val response = newCall(request).execute()
                setText(response.body?.string() ?: "Response null")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setText(text: String) = runOnUiThread {
        txtLog.text = text
    }

    private fun log(message: String) {
        Log.d("OkMock", message)
    }
}