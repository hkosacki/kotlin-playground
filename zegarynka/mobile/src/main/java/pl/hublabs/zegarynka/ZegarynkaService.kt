package pl.hublabs.zegarynka

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.IntentService
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CountDownLatch

class ZegarynkaService : IntentService("ZegarynkaService") {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    val Tag = ZegarynkaService::class.simpleName
    lateinit var latch: CountDownLatch

    lateinit var tts: TextToSpeech

    companion object {
        lateinit public var alarmManager: AlarmManager
        val KEY_SHOULD_TALK = "should_talk"
    }

//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        Log.w(Tag, "Starting onStartCommand...")
//        val myIntent: Intent = Intent(this, ZegarynkaService::class.java)
//        myIntent.putExtra(KEY_SHOULD_TALK, true)
//        val pendingIntent = PendingIntent.getService(this, 0, myIntent, PendingIntent.FLAG_ONE_SHOT)

//        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        alarmManager.set(AlarmManager.RTC_WAKEUP, getNextFullHour(), pendingIntent)
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, getNextFullHour(), AlarmManager.INTERVAL_HOUR / 60, pendingIntent)
//        return super.onStartCommand(intent, flags, startId)
//    }

//    fun getNextFullHour(): Long {
//        Log.w(Tag, "get next full hour...")
//        return System.currentTimeMillis() + 3000
//    }

    override fun onHandleIntent(intent: Intent?) {
        Log.w(Tag, "on handle intent...")
//        if(intent?.getBooleanExtra(KEY_SHOULD_TALK, false)!!) {
        latch = CountDownLatch(1)
        tts = TextToSpeech(this, {
            Log.i(Tag, "TTS initialized successfully!")
            Log.i(Tag, "We won't set up the language. Let's use the default one")
            tts.setOnUtteranceProgressListener(FinishTts())
            tellTheTime()
        })
        latch.await()
//        }
    }

    @SuppressLint("SimpleDateFormat")
    fun tellTheTime() {
        val currentTimeString = SimpleDateFormat("HH:mm").format(Calendar.getInstance().time)
        val message = getString(R.string.its_o_clock, currentTimeString)
        speak(message)
    }

    fun speak(message: String) {
        Log.i("TTS", message)
        tts.speak(message, TextToSpeech.QUEUE_FLUSH, MainActivity.ttsBundleParams, UUID.randomUUID().toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.w(Tag, "onDestroy...")
        if (::tts.isInitialized) tts.shutdown()
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