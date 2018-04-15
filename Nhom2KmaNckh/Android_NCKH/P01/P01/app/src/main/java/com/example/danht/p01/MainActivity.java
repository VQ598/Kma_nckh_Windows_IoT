package com.example.danht.p01;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.internal.wire.MqttPublish;

public class MainActivity extends AppCompatActivity {
    MqttAndroidClient client;
    static String host = "tcp://m14.cloudmqtt.com:14953";
    static String username = "eqzcaosj";
    static String password = "NFmNNSuRgQS-";
    String topic = "Robot2";
    String topicTocDo = "TocDo";
    TextView txtvTrangthai , txtvTocDo;
    Button btnTienlen, btnLuixuong , btnRephai , btnRetrai , btnQuayphai, btnQuaytrai , btnDunglai;
    SeekBar skTocDo;
    CheckBox cbRaspberryMqtt , cbAndroidMqtt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        AnhXa();
        MqttConnect();
        ClickButton();
        cbRaspberryMqtt.setEnabled(false);
        cbAndroidMqtt.setEnabled(false);
        skTocDo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                try {
                    client.publish(topicTocDo, String.valueOf(progress).getBytes(), 0, false);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                txtvTocDo.setText("Tốc Độ : " + progress + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    public void AnhXa() {

        btnTienlen = (Button) findViewById(R.id.btnTienLen);
        btnLuixuong = (Button) findViewById(R.id.btnLuixuong);
        btnRephai = (Button) findViewById(R.id.btnRephai);
        btnRetrai = (Button) findViewById(R.id.btnRetrai);
        btnQuayphai = (Button) findViewById(R.id.btnQuayphai);
        btnQuaytrai = (Button) findViewById(R.id.btnQuaytrai);
        btnDunglai = (Button) findViewById(R.id.btnStop);
        txtvTocDo = (TextView) findViewById(R.id.textViewTocDo);
        skTocDo = (SeekBar) findViewById(R.id.seekBarTocDo);
        cbRaspberryMqtt = (CheckBox) findViewById(R.id.cbRaspberryMqtt);
        cbAndroidMqtt = (CheckBox) findViewById(R.id.cbAndroidMqtt);
    }

    public void MqttConnect() {
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
                    subcribe();
                    Toast.makeText(MainActivity.this, "Kết nối thành công !", Toast.LENGTH_SHORT).show();
                    cbAndroidMqtt.setChecked(true);

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

    public void ClickButton (){
        btnDunglai.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int ev = event.getAction();
                switch (ev){
                    case MotionEvent.ACTION_DOWN : MqttPublish("stop"); break;

                }
                return true;
            }
        });
        btnRephai.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int ev = event.getAction();
                switch (ev){
                    case MotionEvent.ACTION_DOWN : MqttPublish("turn right"); break;
                    case MotionEvent.ACTION_UP : MqttPublish("stop");
                }
                return true;
            }
        });
        btnRetrai.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int ev = event.getAction();
                switch (ev){
                    case MotionEvent.ACTION_DOWN : MqttPublish("turn left"); break;
                    case MotionEvent.ACTION_UP : MqttPublish("stop");
                }
                return true;
            }
        });
        btnTienlen.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int ev = event.getAction();
                switch (ev){
                    case MotionEvent.ACTION_DOWN : MqttPublish("move forward"); break;
                    case MotionEvent.ACTION_UP : MqttPublish("stop");
                }
                return true;
            }
        });
        btnLuixuong.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int ev = event.getAction();
                switch (ev){
                    case MotionEvent.ACTION_DOWN : MqttPublish("move backward"); break;
                    case MotionEvent.ACTION_UP : MqttPublish("stop");
                }
                return true;
            }
        });
        btnQuayphai.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int ev = event.getAction();
                switch (ev){
                    case MotionEvent.ACTION_DOWN : MqttPublish("rotate left"); break;
                    case MotionEvent.ACTION_UP : MqttPublish("stop");
                }
                return true;
            }
        });
        btnQuaytrai.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int ev = event.getAction();
                switch (ev){
                    case MotionEvent.ACTION_DOWN : MqttPublish("rotate right"); break;
                    case MotionEvent.ACTION_UP : MqttPublish("stop");
                }
                return true;
            }
        });
    }
    public  void MqttPublish (String a){
        try {
            client.publish(topic, a.getBytes(), 0, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    void subcribe (){
        // Subcribe

        int qos = 0;
        try {
            IMqttToken subToken = client.subscribe(topicTocDo, qos);
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
            IMqttToken unsubToken = client.unsubscribe(topicTocDo);
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
