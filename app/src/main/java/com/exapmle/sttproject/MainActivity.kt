package com.exapmle.sttproject

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    private val MY_PERMISSIONS_RECORD_AUDIO = 1

    private val TAG = "ArcoreMeasurement"
    private val buttonArrayList = ArrayList<String>()
    private lateinit var toMeasurement: Button
    private lateinit var textInputEditText: TextInputEditText
    var userModel: UserModel? = null

    var database = FirebaseDatabase.getInstance()
    var myRef: DatabaseReference? = null
    var textToSpeech: TextToSpeech? = null
    private val question1 = "SWITCH 1 DETAIL:\n" +
            "1. SWITCH 1 LAST CONDITION IS CRITICAL,\n" +
            "2. SWITCH 1 HAS METALLIC BODY\n" +
            "3. SWITCH 1 HAS CROSS BADGE PARTS"

    private val question2 = "Do you need more info ?"
    private val question3 = "What is the condition of SWITCH 1 ?"
    private val question4 = "What action you have done ?"
    private val question5 = "Do you want to provide more info ?"
    private val question6 = "Do you want to sync inspection data ?"
    private var question: String? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = intent
        userModel = intent.getSerializableExtra(Constants.USER) as UserModel?
        myRef = database.getReference(Constants.VOICE_RESPONSE).child(userModel!!.getId())

        textToSpeech = TextToSpeech(this, TextToSpeech.OnInitListener {
            if (it != TextToSpeech.ERROR) {
                textToSpeech!!.language = Locale.getDefault()
                question = question1
                speakOut(question1)
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun speakOut(question: String) {
        textToSpeech!!.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(s: String) {
                runOnUiThread {

                }
            }

            override fun onDone(s: String) {
                runOnUiThread {
//                    Toast.makeText(applicationContext, "Done ", Toast.LENGTH_SHORT).show()
                    Log.e("OnDone", s)
                    when (s) {
                        question1 -> {
                            requestAudioPermissions(etSwitch1Detail)
                        }
                        question2 -> {
                            requestAudioPermissions(question2)
                        }
                        question3 -> {
                            requestAudioPermissions(etSwitch1Condition)
                        }
                        question4 -> {
                            requestAudioPermissions(etAction)
                        }
                        question5 -> {
                            requestAudioPermissions(question5)
                        }
                        question6 -> {
                            requestAudioPermissions(question6)
                        }
                    }
                }
            }

            override fun onError(s: String) {
                runOnUiThread {
                    Toast.makeText(applicationContext, "Error ", Toast.LENGTH_SHORT).show()
                }
            }
        })
        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "")
        textToSpeech!!.speak(question, TextToSpeech.QUEUE_FLUSH, params, question)
        this.question = question
    }

    private fun startSpeechToText(textInputEditText: Any) {
        val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(bundle: Bundle) {
                Log.e("MainActivity", "onReadyForSpeech")
            }

            override fun onBeginningOfSpeech() {
                Log.e("MainActivity", "onBeginningOfSpeech")
            }

            override fun onRmsChanged(v: Float) {
                Log.e("MainActivity", "onRmsChanged")
            }

            override fun onBufferReceived(bytes: ByteArray) {
                Log.e("MainActivity", "onBufferReceived")
            }

            override fun onEndOfSpeech() {
                Log.e("MainActivity", "onEndOfSpeech")
            }

            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onError(i: Int) {
                Log.e("MainActivity", "onError")
                speakOut(question!!)
            }

            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onResults(bundle: Bundle) {
                val matches =
                    bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)//getting all the matches
                Log.e("Words", matches.toString())
                //displaying the first match
                if (matches != null) {
                    val words = matches[0].split(' ')
                    if (textInputEditText is TextInputEditText) {
                        textInputEditText.setText("${words[0]}")
                        speechRecognizer.stopListening()
                        when (textInputEditText) {
                            etSwitch1Detail -> {
                                speakOut(question2)
                            }
                            etSwitch1Condition -> {
                                speakOut(question4)
                            }
                            etAction -> {
                                speakOut(question5)
                            }
                        }
                    } else if (textInputEditText is String) {
                        if (textInputEditText == question2) {
                            if (words[0].equals("no", true)) {
                                speakOut(question3)
                            }
                        } else if (textInputEditText == question5) {
                            if (words[0].equals("no", true)) {
                                speakOut(question6)
                            }
                        } else if (textInputEditText == question6) {
                            if (words[0].equals("yes", true)) {
                                val switchDetail = etSwitch1Detail.text.toString()
                                val switchCondition = etSwitch1Condition.text.toString()
                                val action = etAction.text.toString()
                                val id = myRef!!.push().key
                                val voiceCommand =
                                    VoiceCommandModel(id, switchDetail, switchCondition, action)
                                val re = myRef!!.child(id!!).setValue(voiceCommand)
                                Log.e("OnDatabaseOperation","${re.isSuccessful}")
                            }
                        }
                    }
                } else {
                    speakOut(question!!)
                }
            }

            override fun onPartialResults(bundle: Bundle) {
                Log.e("MainActivity", "onPartialResults")
            }

            override fun onEvent(i: Int, bundle: Bundle) {
                Log.e("MainActivity", "onEvent")
            }
        })
        speechRecognizer.startListening(speechRecognizerIntent)
    }

    private fun requestAudioPermissions(textInputEditText: Any) {
        if (textInputEditText is TextInputEditText) {
            this.textInputEditText = textInputEditText
        }
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
            startSpeechToText(textInputEditText)
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
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startSpeechToText(textInputEditText)
                } else {
                    Toast.makeText(this, "Permissions Denied to record audio", Toast.LENGTH_LONG)
                        .show()
                }
                return
            }
        }
    }
}