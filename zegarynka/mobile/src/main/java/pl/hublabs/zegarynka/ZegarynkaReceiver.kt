package pl.hublabs.zegarynka

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CountDownLatch

/**
 * Created by hubert on 28/12/2017.
 */
class ZegarynkaReceiver: BroadcastReceiver() {

    val Tag = ZegarynkaReceiver::class.simpleName

    lateinit var tts: TextToSpeech
    lateinit var latch: CountDownLatch


    override fun onReceive(context: Context?, intent: Intent?) {
        Log.w(Tag, "onReceived!")
        latch = CountDownLatch(1)
        tts = TextToSpeech(context, {
            Log.i(Tag, "TTS initialized successfully!")
            Log.i(Tag, "We won't set up the language. Let's use the default one")
            tts.setOnUtteranceProgressListener(FinishTts())
            tellTheTime(context!!)
        })
        latch.await()
        if(::tts.isInitialized) tts.shutdown()

    }

    @SuppressLint("SimpleDateFormat")
    fun tellTheTime(context: Context) {
        val currentTimeString = SimpleDateFormat("HH:mm").format(Calendar.getInstance().time)
        val message = context.getString(R.string.its_o_clock, currentTimeString)
        speak(message)
    }

    fun speak(message: String) {
        Log.i("TTS", message)
        tts.speak(message, TextToSpeech.QUEUE_FLUSH, MainActivity.ttsBundleParams, UUID.randomUUID().toString())
    }


    inner class FinishTts : UtteranceProgressListener() {
        override fun onError(utteranceId: String?) {
            Log.e("FinishTts", "onError")
            latch.countDown()
        }

        override fun onStart(utteranceId: String?) {
            Log.e("FinishTts", "onStart")
        }

        override fun onDone(utteranceId: String?) {
            Log.e("FinishTts", "onDone")
            latch.countDown()
        }
    }

}