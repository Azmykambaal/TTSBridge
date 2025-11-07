package com.azezo.ttsbridge

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import java.io.File
import java.util.*

class MainActivity : Activity(), TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var text: String? = null
    private var lang: String? = null
    private var voiceName: String? = null
    private var outPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Read intent extras
        text = intent.getStringExtra("text")
        lang = intent.getStringExtra("lang") ?: "en-US"
        voiceName = intent.getStringExtra("voice")
        outPath = intent.getStringExtra("path")

        if (text.isNullOrEmpty()) {
            Toast.makeText(this, "❌ No text received", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Initialize TTS
        tts = TextToSpeech(this, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            try {
                val locale = Locale.forLanguageTag(lang ?: "en-US")
                tts?.language = locale

                if (!voiceName.isNullOrEmpty()) {
                    val selectedVoice = tts?.voices?.find { it.name == voiceName }
                    if (selectedVoice != null) {
                        tts?.voice = selectedVoice
                    }
                }

                val file = File(outPath ?: "${externalCacheDir}/output.wav")
                val params = Bundle()
                tts?.synthesizeToFile(text!!, params, file, "ttsOutput")

                Toast.makeText(this, "✅ Audio saved to: ${file.absolutePath}", Toast.LENGTH_LONG).show()
                Log.i("TTSBridge", "Saved to: ${file.absolutePath}")

            } catch (e: Exception) {
                Log.e("TTSBridge", "Error: ${e.message}")
                Toast.makeText(this, "❌ Error: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                tts?.shutdown()
                finish()
            }
        } else {
            Toast.makeText(this, "❌ TTS Initialization failed", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}