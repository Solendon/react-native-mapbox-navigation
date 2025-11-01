package com.mapboxnavigation

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

object VietnameseTTS {
    private const val TAG = "VietnameseTTS"

    private var tts: TextToSpeech? = null
    private var isReady = false

    /**
     * Khởi tạo TTS cho tiếng Việt.
     * Gọi hàm này trong onCreate() hoặc khi app khởi động.
     */
    fun init(context: Context, onReady: (() -> Unit)? = null) {
        if (tts != null && isReady) {
            Log.d(TAG, "TTS đã được khởi tạo trước đó.")
            onReady?.invoke()
            return
        }

        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale("vi", "VN"))
                when (result) {
                    TextToSpeech.LANG_MISSING_DATA -> {
                        Log.w(TAG, "⚠️ Thiếu gói ngôn ngữ tiếng Việt.")
                        isReady = false
                    }
                    TextToSpeech.LANG_NOT_SUPPORTED -> {
                        Log.w(TAG, "⚠️ Thiết bị không hỗ trợ tiếng Việt.")
                        isReady = false
                    }
                    else -> {
                        Log.d(TAG, "✅ TTS tiếng Việt sẵn sàng.")
                        tts?.setSpeechRate(1.0f)
                        tts?.setPitch(1.0f)
                        isReady = true
                        onReady?.invoke()
                    }
                }
            } else {
                Log.e(TAG, "❌ Khởi tạo TTS thất bại (status=$status)")
                isReady = false
            }
        }
    }

    /**
     * Phát âm một câu tiếng Việt.
     */
    fun speak(text: String) {
        if (!isReady) {
            Log.w(TAG, "⚠️ TTS chưa sẵn sàng, hãy gọi init() trước.")
            return
        }
        Log.d(TAG, "Đọc: $text")
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "VN_SPEAK")
    }

    /**
     * Dừng và giải phóng TTS khi không cần nữa (ví dụ trong onDestroy()).
     */
    fun shutdown() {
        Log.d(TAG, "🛑 Dừng TTS.")
        tts?.stop()
        tts?.shutdown()
        tts = null
        isReady = false
    }
}
