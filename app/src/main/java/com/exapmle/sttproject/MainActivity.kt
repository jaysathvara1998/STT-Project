package com.exapmle.sttproject

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    private val MY_PERMISSIONS_RECORD_AUDIO = 1

    private val TAG = "ArcoreMeasurement"
    private val buttonArrayList = ArrayList<String>()
    private lateinit var toMeasurement: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonArray = resources
            .getStringArray(R.array.arcore_measurement_buttons)

        buttonArray.map{it->
            buttonArrayList.add(it)
        }
        toMeasurement = findViewById(R.id.to_measurement)
        toMeasurement.text = buttonArrayList[0]
        toMeasurement.setOnClickListener {
            val intent = Intent(this, Measurement::class.java)
            startActivity(intent)
        }

        requestAudioPermissions()
        name.requestFocus()
    }

    private fun startSpeechToText() {
        val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(bundle: Bundle) {}

            override fun onBeginningOfSpeech() {}

            override fun onRmsChanged(v: Float) {}

            override fun onBufferReceived(bytes: ByteArray) {}

            override fun onEndOfSpeech() {}

            override fun onError(i: Int) {}

            override fun onResults(bundle: Bundle) {
                val matches =
                    bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)//getting all the matches
                Log.e("Words", matches.toString())
                //displaying the first match
                if (matches != null) {
                    val words = matches[0].split(' ')
                    Log.e("MainActivity", "$words")
                    Log.e("MainActivity Focus", "${name.isFocusable}")
                    when {
                        name.isFocusable -> {
                            name.setText("${words[0]}")
                            startSpeechToText()
                            name.isFocusable = false
                            surname.requestFocus()
                            return
                        }
                        surname.isFocusable -> {
                            surname.setText("${words[0]}")
                            startSpeechToText()
                            surname.isFocusable = false
                            age.requestFocus()
                            return
                        }
                        age.isFocusable -> {
                            age.setText("${words[0]}")
                            startSpeechToText()
                            age.isFocusable = false
                            return
                        }

                        words.indexOf("submit") != -1 -> {
                            Toast.makeText(this@MainActivity, "Form submit", Toast.LENGTH_LONG)
                                .show()
                            speechRecognizer.stopListening()
                        }
                    }

                }

            }

            override fun onPartialResults(bundle: Bundle) {}

            override fun onEvent(i: Int, bundle: Bundle) {}
        })
        speechRecognizer.startListening(speechRecognizerIntent)

//        button.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
//            when (motionEvent.action) {
//                MotionEvent.ACTION_UP -> {
//                    Toast.makeText(this, "up", Toast.LENGTH_LONG).show()
//                    speechRecognizer.stopListening()
//                    name.hint = "You will see text here.."
//                    age.hint = "You will see text here.."
//                    surname.hint = "You will see text here.."
//                }
//
//                MotionEvent.ACTION_DOWN -> {
//                    Toast.makeText(this, "down", Toast.LENGTH_LONG).show()
//                    speechRecognizer.startListening(speechRecognizerIntent)
//                }
//            }
//            false
//        })
    }

    private fun checkPermission(): Boolean {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                true;
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
                    Toast.makeText(
                        this,
                        "App required access to audio", Toast.LENGTH_SHORT
                    ).show();
                }
                requestPermissions(
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    MY_PERMISSIONS_RECORD_AUDIO
                )
                false;
            }

        } else {
            // put your code for Version < Marshmallow
            return true;
        }
    }

    private fun requestAudioPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            //When permission is not granted by user, show them message why this permission is needed.
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.RECORD_AUDIO
                )
            ) {
                Toast.makeText(this, "Please grant permissions to record audio", Toast.LENGTH_LONG)
                    .show()
                //Give user option to still opt-in the permissions
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.RECORD_AUDIO),
                    MY_PERMISSIONS_RECORD_AUDIO
                )
            } else {
                // Show user dialog to grant permission to record audio
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.RECORD_AUDIO),
                    MY_PERMISSIONS_RECORD_AUDIO
                )
            }
        } else if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            startSpeechToText()
        }
    }

    //Handling callback
    override fun onRequestPermissionsResult(
        requestCode: Int,
        p1: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_RECORD_AUDIO -> {
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    startSpeechToText()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permissions Denied to record audio", Toast.LENGTH_LONG)
                        .show()
                }
                return
            }
        }
    }
}