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
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.danht.p03.R;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;



public class MainActivity extends AppCompatActivity implements RecognitionListener {


    private ProgressBar progressBar;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;

    MqttAndroidClient client;
    static String host = "tcp://m14.cloudmqtt.com:14953";
    static String username = "eqzcaosj";
    static String password = "NFmNNSuRgQS-";
    String topic = "Robot2";
    TextView txtvTrangthai ,tvResult ,txtvDen , txtvDen1 , txtvDen2, txtvTatca;
    ImageButton btnSpeak;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        AnhXa();
        MqttConnect();
        nhanTinNhan();

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

                /*To stop listening
                    progressBar.setVisibility(View.INVISIBLE);
                    speech.stopListening();
                    btnSpeak.setEnabled(true);
                 */
            }


        });



    }
    public void AnhXa (){
        tvResult = (TextView) findViewById(R.id.textViewResult);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
        txtvTrangthai = (TextView) findViewById(R.id.txtvTrangthai);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        txtvDen1 = (TextView) findViewById(R.id.textViewDen1);
        txtvDen2 = (TextView) findViewById(R.id.textViewDen2);
        txtvTatca = (TextView) findViewById(R.id.textViewTatCa);

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
                    txtvTrangthai.setText("Mqtt : Đã kết nối !");
                    subcribe();

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Toast.makeText(MainActivity.this, "Kết nối thất bại !", Toast.LENGTH_SHORT).show();
                    txtvTrangthai.setText("Mqtt : Mất kết nối !");

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
        String errorMessage = getErrorText(errorCode);
        Log.d("Log", "FAILED " + errorMessage);
        progressBar.setVisibility(View.INVISIBLE);
        tvResult.setText(errorMessage);
        btnSpeak.setEnabled(true);
    }

    @Override
    public void onEvent(int arg0, Bundle arg1) {
        Log.d("Log", "onEvent");
    }

    @Override
    public void onPartialResults(Bundle arg0) {


        ArrayList<String> matches = arg0.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";


        text = matches.get(0).toLowerCase(); //// Remove this line while uncommenting above codes

        tvResult.setText("Nhận dạng : "+text);
       switch (text){
            case "bật đèn 1" : {
                MqttPublic("enable lamp 1");
                txtvDen1.setText("Đèn 1 : Bật");
            } break;
            case "tắt đèn 1" : {
                MqttPublic("disable lamp 1");
                txtvDen1.setText("Đèn 1 : Tắt");
            } break;
            case "bật đèn 2" :{
                MqttPublic("enable lamp 2");
                txtvDen2.setText("Đèn 1 : Bật");
            } break;
            case "tắt đèn 2" : {
                MqttPublic("disable lamp 2");
                txtvDen2.setText("Đèn 2 : Tắt");
            } break;
            case "bật tất cả" : {
                MqttPublic("enable all lamp");
                txtvTatca.setText("Tất cả đèn : Bật");
            } break;
            case "tắt tất cả" : {
                MqttPublic("disable all lamp");
                txtvTatca.setText("Tất cả đèn : Tắt");
            } break;
            default: Toast.makeText(MainActivity.this ,
                    "!" ,
                    Toast.LENGTH_SHORT).show(); break;
        }

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
            IMqttToken subToken = client.subscribe("Robot2", qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

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
            IMqttToken unsubToken = client.unsubscribe("Thietbidien");
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
        client.setCallback(new org.eclipse.paho.client.mqttv3.MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, org.eclipse.paho.client.mqttv3.MqttMessage message) throws Exception {
                switch (message.toString()){
                    case "enable lamp 1" : {

                txtvDen1.setText("Đèn 1 : Bật");
            } break;
            case "disable lamp 1" : {

                txtvDen1.setText("Đèn 1 : Tắt");
            } break;
            case "enable lamp 2" :{

                txtvDen2.setText("Đèn 2 : Bật");
            } break;
            case "disable lamp 2" : {

                txtvDen2.setText("Đèn 2 : Tắt");
            } break;
            case "enable all lamp" : {

                txtvTatca.setText("Tất cả đèn : Bật");
            } break;
            case "disable all lamp" : {

                txtvTatca.setText("Tất cả đèn : Tắt");
            } break;
                }
                Toast.makeText(MainActivity.this
                        , message.toString()
                        , Toast.LENGTH_SHORT).show();

            }

            @Override
            public void deliveryComplete(org.eclipse.paho.client.mqttv3.IMqttDeliveryToken token) {

            }
        });
    }
}