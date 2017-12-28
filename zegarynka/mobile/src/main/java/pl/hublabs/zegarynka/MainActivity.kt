package pl.hublabs.zegarynka

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View

import kotlinx.android.synthetic.main.activity_main.*
import android.speech.tts.TextToSpeech
import android.content.Intent
import android.speech.tts.TextToSpeech.QUEUE_ADD
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val Tag: String = MainActivity::class.simpleName!!

    private lateinit var tts: TextToSpeech

    private val MY_DATA_CHECK_CODE = 1005

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val checkIntent = Intent()
        checkIntent.action = TextToSpeech.Engine.ACTION_CHECK_TTS_DATA
        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // success, create the TTS instance
                tts = TextToSpeech(this, {
                    Log.i(Tag, "TTS initialized successfully!")
                    Log.i(Tag, "We won't set up the language. Let's use the default one")
                })
            } else {
                // missing data, install it
                val installIntent = Intent()
                installIntent.action = TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA
                startActivity(installIntent)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun buttonTellTimeClick(v: View) {
        Log.w(Tag, "here is me!")
        tellTheTime()
    }

    fun tellTheTime() {
        val currentTimeString = SimpleDateFormat("HH:mm", Locale.GERMAN).format(Calendar.getInstance().time)
        val message = getString(R.string.its_o_clock, currentTimeString)
        speak(message)
    }

    fun speak(message: String){
        Log.i("TTS", message)
        tts.speak(message, QUEUE_ADD, ttsBundleParams, UUID.randomUUID().toString())
    }

    companion object {
        val ttsBundleParams = Bundle().apply{
            putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 0.5f)
        }

    }
}
