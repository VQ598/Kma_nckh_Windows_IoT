package com.example.danht.p03;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.danht.p03.R;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;



public class MainActivity extends AppCompatActivity implements RecognitionListener {


    private ProgressBar progressBar;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private SeekBar skTocDo;
    MqttMessage message;


    MqttAndroidClient client;
    static String host = "tcp://m14.cloudmqtt.com:14953";
    static String username = "eqzcaosj";
    static String password = "NFmNNSuRgQS-";
    String topic = "Robot2";
    String topicTocDo = "TocDo";
    TextView txtvTrangthai ,tvResult , txtvHuongdan ,_txtvTocDo;
    ImageButton btnSpeak;
    CheckBox cbRaspberryMqtt , cbAndroidMqtt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        AnhXa();
        MqttConnect();
        nhanTinNhan();
        cbRaspberryMqtt.setEnabled(false);
        cbAndroidMqtt.setEnabled(false);
        txtvHuongdan.setText("Hướng dẫn :\n Tiến lên \n Lùi xuống\n Rẽ trái \n Rẽ phải" +
                "\n Quay trái \n Quay Phải \n Dừng lại ");
        progressBar.setVisibility(View.INVISIBLE);
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "vi");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

        /*
        Minimum time to listen in millis. Here 5 seconds
         */
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 5000);
        recognizerIntent.putExtra("android.speech.extra.DICTATION_MODE", true);




        btnSpeak.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View p1)
            {
                progressBar.setVisibility(View.VISIBLE);
                speech.startListening(recognizerIntent);
                btnSpeak.setEnabled(false);
                tvResult.setText("Đang nhận dạng...");

                /*To stop listening
                    progressBar.setVisibility(View.INVISIBLE);
                    speech.stopListening();
                    btnSpeak.setEnabled(true);
                 */
            }


        });

        // set toc do
        skTocDo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                try {
                    client.publish(topicTocDo, String.valueOf(progress).getBytes(), 0, false);
                } catch (MqttException e) {
                    e.printStackTrace();
                }

                _txtvTocDo.setText("Tốc Độ : " + progress + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }
    public void AnhXa (){
        tvResult = (TextView) findViewById(R.id.textViewResult);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
        cbAndroidMqtt = (CheckBox) findViewById(R.id.cbAndroidMqtt) ;
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        txtvHuongdan = (TextView) findViewById(R.id.textViewHuongDan);
        skTocDo = (SeekBar) findViewById(R.id.seekBarTocDo);
        _txtvTocDo = (TextView) findViewById(R.id.txtvTocDo);
        cbRaspberryMqtt = (CheckBox) findViewById(R.id.cbRaspberryMqtt);
    }
    public void MqttPublic (String Result){
        try {
            client.publish(topic, Result.getBytes(), 0, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    public void MqttConnect (){
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), host, clientId);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(username);
        options.setPassword(password.toCharArray());
        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Toast.makeText(MainActivity.this, "Kết nối thành công !", Toast.LENGTH_SHORT).show();
                    cbAndroidMqtt.setChecked(true);
                    subcribe();

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Toast.makeText(MainActivity.this, "Kết nối thất bại !", Toast.LENGTH_SHORT).show();
                    cbAndroidMqtt.setChecked(false);

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (speech != null) {
            speech.destroy();
            Log.d("Log", "destroy");
        }

    }

    @Override
    public void onBeginningOfSpeech() {
        Log.d("Log", "onBeginningOfSpeech");
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.d("Log", "onBufferReceived: " + buffer);
    }

    @Override
    public void onEndOfSpeech() {
        Log.d("Log", "onEndOfSpeech");
        progressBar.setVisibility(View.INVISIBLE);
        btnSpeak.setEnabled(true);
    }

    @Override
    public void onError(int errorCode) {
        //String errorMessage = getErrorText(errorCode);
        //Log.d("Log", "FAILED " + errorMessage);
        //progressBar.setVisibility(View.INVISIBLE);
        //tvResult.setText(errorMessage);
        //btnSpeak.setEnabled(true);
    }

    @Override
    public void onEvent(int arg0, Bundle arg1) {
        Log.d("Log", "onEvent");
    }

    @Override
    public void onPartialResults(Bundle arg0) {


        ArrayList<String> matches = arg0.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";


        text = matches.get(0).toLowerCase(); //  Remove this line while uncommenting above codes

       switch (text){
            case "tiến lên" : {

                MqttPublic("move forwardV");
                progressBar.setVisibility(View.INVISIBLE);
                speech.stopListening();
                btnSpeak.setEnabled(true);
            } break;
            case "lùi xuống" : {

                MqttPublic("move backwardV");
                progressBar.setVisibility(View.INVISIBLE);
                speech.stopListening();
                btnSpeak.setEnabled(true);
            } break;
            case "quay trái" : {
                MqttPublic("rotate leftV");
                progressBar.setVisibility(View.INVISIBLE);
                speech.stopListening();
                btnSpeak.setEnabled(true);
            } break;
            case "quay phải" : {
                MqttPublic("rotate rightV");
                progressBar.setVisibility(View.INVISIBLE);
                speech.stopListening();
                btnSpeak.setEnabled(true);
            } break;
            case "rẽ trái" : {
                MqttPublic("turn leftV");
                progressBar.setVisibility(View.INVISIBLE);
                speech.stopListening();
                btnSpeak.setEnabled(true);
            } break;
            case "rẽ phải" : {
                MqttPublic("turn rightV");
                progressBar.setVisibility(View.INVISIBLE);
                speech.stopListening();
                btnSpeak.setEnabled(true);
            } break;
            case "dừng lại" : {
                MqttPublic("stop");
                progressBar.setVisibility(View.INVISIBLE);
                speech.stopListening();
                btnSpeak.setEnabled(true);
            } break;
            default:{
                progressBar.setVisibility(View.INVISIBLE);
                speech.stopListening();
                btnSpeak.setEnabled(true);
            }break;

        }

        tvResult.setText("Nhận dạng : "+text);
    }

    @Override
    public void onReadyForSpeech(Bundle arg0) {
        Log.d("Log", "onReadyForSpeech");
    }

    @Override
    public void onResults(Bundle results) {
        Log.d("Log", "onResults");

    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.d("Log", "onRmsChanged: " + rmsdB);
        progressBar.setProgress((int) rmsdB);

    }

    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }

    void subcribe (){
        // Subcribe

        int qos = 0;
        try {
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The message was published
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // The subscription could not be performed, maybe the user was not
                    // authorized to subscribe on the specified topic e.g. using wildcards

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    void unsubcribe(){

        try {
            IMqttToken unsubToken = client.unsubscribe(topic);
            unsubToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The subscription could successfully be removed from the client
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // some error occurred, this is very unlikely as even if the client
                    // did not had a subscription to the topic the unsubscribe action
                    // will be successfully
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    public  void nhanTinNhan (){
        // message Arrived
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                switch (message.toString()){
                    case "Mqtt đã kết nối với Raspberry thành công !" :
                        cbRaspberryMqtt.setChecked(true); break;
                    case "Raspberry : đã mất kết nối!" :
                        cbRaspberryMqtt.setChecked(false); break;


                }

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

}