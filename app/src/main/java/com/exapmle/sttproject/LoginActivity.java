package com.exapmle.sttproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    TextView tvRegister;
    TextInputEditText etUserName;
    TextInputEditText etPassword;
    TextInputLayout txtUserName;
    TextInputLayout txtPassword;
    TextToSpeech textToSpeech;
    int MY_PERMISSIONS_RECORD_AUDIO = 1;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference(Constants.USER);
    ArrayList<UserModel> userList = new ArrayList<>();
    String userNameQuestion = "Enter username";
    String passwordQuestion = "Enter password";
    String submit = "Do you want to submit";
    String invalidCredentials = "Invalid username or password";
    String question;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        etUserName = findViewById(R.id.etUserName);
        etPassword = findViewById(R.id.etPassword);
        txtUserName = findViewById(R.id.txtUserName);
        txtPassword = findViewById(R.id.txtPassword);
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.getDefault());
                    question = userNameQuestion;
                    speakOut(userNameQuestion);
                }
            }
        });

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("Login", "Value is: " + dataSnapshot.getValue());
                Log.e("Login", "DataSnapshot Key: " + dataSnapshot.getKey());

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    myRef.child(key).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            HashMap<String, Object> value = (HashMap<String, Object>) snapshot.getValue();

                            String email = value.get("email").toString();
                            String password = value.get("password").toString();
                            String name = value.get("name").toString();
                            String id = value.get("id").toString();
                            UserModel model = new UserModel(name, email, password, id);
                            userList.add(model);
                            Log.e("Login", "Email: " + email + " Password: " + password);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Login", "Failed to read value.", error.toException());
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textToSpeech.stop();
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void speakOut(String question) {
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String s) {

            }

            @Override
            public void onDone(String s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (s.equalsIgnoreCase(userNameQuestion)) {
                            requestAudioPermissions(etUserName);
                            etUserName.requestFocus();
                        }
                        if (s.equalsIgnoreCase(passwordQuestion)) {
                            requestAudioPermissions(etPassword);
                            etPassword.requestFocus();
                        }
                        if (s.equalsIgnoreCase(submit)) {
                            requestAudioPermissions(submit);
                        }
                        if (s.equalsIgnoreCase(invalidCredentials)) {
                            speakOut(userNameQuestion);
                        }
                    }
                });
            }

            @Override
            public void onError(String s) {

            }
        });
        Bundle params = new Bundle();
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "");
        textToSpeech.speak(question, TextToSpeech.QUEUE_FLUSH, params, question);
        this.question = question;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private Boolean validate() {
        if (Objects.requireNonNull(etUserName.getText()).toString().isEmpty()) {
            txtUserName.setError("Enter Username");
            txtPassword.setError("");
            return false;
        }

        if (Objects.requireNonNull(etPassword.getText()).toString().isEmpty()) {
            txtPassword.setError("Enter Password");
            txtUserName.setError("");
            return false;
        }

        return true;
    }

    private void requestAudioPermissions(Object textInputEditText) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.RECORD_AUDIO
            )
            ) {
                Toast.makeText(this, "Please grant permissions to record audio", Toast.LENGTH_LONG)
                        .show();
                //Give user option to still opt-in the permissions
            } else {
                // Show user dialog to grant permission to record audio
            }
            ActivityCompat.requestPermissions(
                    this, new String[]{
                            Manifest.permission.RECORD_AUDIO
                    },
                    MY_PERMISSIONS_RECORD_AUDIO);
        } else if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
        )
                == PackageManager.PERMISSION_GRANTED
        ) {
            startSpeechToText(textInputEditText);
        }
    }

    private void startSpeechToText(Object textInputEditText) {
        SpeechRecognizer speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        );
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onError(int i) {
                if (question.equalsIgnoreCase(invalidCredentials)) {
                    speakOut(userNameQuestion);
                } else {
                    speakOut(question);
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null) {
                    List<String> words = Arrays.asList(matches.get(0).split(" "));
                    if (textInputEditText instanceof TextInputEditText) {
                        ((TextInputEditText) textInputEditText).setText(words.get(0));
                        speechRecognizer.stopListening();

                        if (textInputEditText == etUserName) {
                            speakOut(passwordQuestion);
                        }
                        if (textInputEditText == etPassword) {
                            speakOut(submit);
                        }
                    } else if (textInputEditText instanceof String) {
                        if (textInputEditText == submit) {
                            if (words.get(0).equalsIgnoreCase("yes")) {
                                if (!etUserName.getText().toString().isEmpty() && !etPassword.getText().toString().isEmpty()) {

                                    if (!userList.isEmpty()) {
                                        for (int i = 0; i < userList.size(); i++) {
                                            if (userList.get(i).name.equalsIgnoreCase(etUserName.getText().toString()) && userList.get(i).password.equalsIgnoreCase(etPassword.getText().toString())) {

                                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                intent.putExtra(Constants.USER, userList.get(i));
                                                startActivity(intent);
                                                return;
                                            }
                                        }
                                    }
                                    speakOut(invalidCredentials);
                                    Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                speakOut(submit);
                            }
                        }

                        if (textInputEditText == invalidCredentials) {
                            speakOut(userNameQuestion);
                        }
                    }
                }
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });
        speechRecognizer.startListening(speechRecognizerIntent);
    }
}