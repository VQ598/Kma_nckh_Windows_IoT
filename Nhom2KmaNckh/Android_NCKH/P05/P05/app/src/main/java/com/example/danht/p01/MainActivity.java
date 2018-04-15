package com.example.danht.p01;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
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

public class MainActivity extends AppCompatActivity {
    MqttAndroidClient client;
    static String host = "tcp://m14.cloudmqtt.com:14953";
    static String username = "eqzcaosj";
    static String password = "NFmNNSuRgQS-";
    String topic = "Robot2";
    TextView txtvTrangthai;
    Switch swDen1 , swDen2, swTatca ;
    MqttMessage message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        AnhXa();
        MqttConnect();
        swDen1.setChecked(false);
        swDen2.setChecked(false);
        swTatca.setChecked(false);
        switchDen();
        nhanTinNhan();


    }

    public void AnhXa() {
        txtvTrangthai = (TextView) findViewById(R.id.txtvTrangthai);
        swDen1 = (Switch) findViewById(R.id.switchDen1);
        swDen2 = (Switch) findViewById(R.id.switchDen2);
        swTatca = (Switch) findViewById(R.id.switchTatca);

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
    public void switchDen (){
        swDen1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    try {
                        client.publish(topic, "enable lamp 1".getBytes(), 0, false);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    try {
                        client.publish(topic, "disable lamp 1".getBytes(), 0, false);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        // lamp 2
        swDen2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    try {
                        client.publish(topic, "enable lamp 2".getBytes(), 0, false);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    try {
                        client.publish(topic, "disable lamp 2".getBytes(), 0, false);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // all
        swTatca.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    try {
                        client.publish(topic, "enable all lamp".getBytes(), 0, false);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    try {
                        client.publish(topic, "disable all lamp".getBytes(), 0, false);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    void subcribe (){
        // Subcribe

        int qos = 0;
        try {
            IMqttToken subToken = client.subscribe("Robot2", qos);
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
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                switch (message.toString()){
                    case "enable lamp 1" : swDen1.setChecked(true);break;
                    case "disable lamp 1" : swDen1.setChecked(false);break;
                    case "enable lamp 2" : swDen2.setChecked(true);break;
                    case "disable lamp 2" : swDen2.setChecked(false);break;
                    case "enable all lamp" : swTatca.setChecked(true);break;
                    case "disable all lamp" : swTatca.setChecked(false);break;
                }
                Toast.makeText(MainActivity.this
                        , message.toString()
                        , Toast.LENGTH_SHORT).show();

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

}
