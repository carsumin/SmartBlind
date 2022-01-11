package com.example.smart_blind3;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class control extends AppCompatActivity {
    TextView lux,sensornum, sensoronoff, weatheronoff;
    ImageButton sensor, weather;
    int power;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        sensor = findViewById(R.id.sensor);
        weather = findViewById(R.id.weather);
        lux = findViewById(R.id.lux);
        sensornum = findViewById(R.id.sensornum);
        sensoronoff = findViewById(R.id.sensoronoff);
        weatheronoff = findViewById(R.id.weatheronoff);

        power = 1;

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        } //안드로이드 3.0부터는 추가해야 에러 안남


        weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!weatheronoff.getText().equals("자동조절 Off")) {
                    weatheronoff.setText("자동조절 Off");
                } else {
                    weatheronoff.setText("자동조절 On");
                }


                Intent intent = new Intent(getApplicationContext(), auto.class);
                startActivity(intent);
            }
        });


        sensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(), "Connect시도", Toast.LENGTH_SHORT).show();
                String addr = "192.168.200.180";
                final String TAG = "socketTest";
                String hostname;
                hostname = addr;
                try {
                    int port = 12340;
                    Socket socket = new Socket(hostname, port);//소켓 열어줌
                    Log.d(TAG, "Socket 생성, 연결.");
//                    Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();

                    DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                    DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));


                    if(!sensoronoff.getText().equals("자동조절 On")) {
                        sensoronoff.setText("자동조절 On");
                        dos.writeByte('A');
                        dos.flush();
                    } else {
                        sensoronoff.setText("자동조절 Off");
                        dos.writeByte('F');
                        dos.flush();
                    }

                    int in = dis.readInt();
                    sensornum.setText("조도량 : "+in);

                } catch (UnknownHostException uhe) { // 소켓 생성 시 전달되는 호스트(www.unknown-host.com)의 IP를 식별할 수 없음.
                    Log.e(TAG, " 생성 Error : 호스트의 IP 주소를 식별할 수 없음.(잘못된 주소 값 또는 호스트 이름 사용)");
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Error : 호스트의 IP 주소를 식별할 수 없음.(잘못된 주소 값 또는 호스트 이름 사용)", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (IOException ioe) { // 소켓 생성 과정에서 I/O 에러 발생.
                    Log.e(TAG, " 생성 Error : 네트워크 응답 없음");
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Error : 네트워크 응답 없음", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (SecurityException se) { // security manager에서 허용되지 않은 기능 수행.
                    Log.e(TAG, " 생성 Error : 보안(Security) 위반에 대해 보안 관리자(Security Manager)에 의해 발생. (프록시(proxy) 접속 거부, 허용되지 않은 함수 호출)");
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Error : 보안(Security) 위반에 대해 보안 관리자(Security Manager)에 의해 발생. (프록시(proxy) 접속 거부, 허용되지 않은 함수 호출)", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (IllegalArgumentException le) { // 소켓 생성 시 전달되는 포트 번호(65536)이 허용 범위(0~65535)를 벗어남.

                    Log.e(TAG, " 생성 Error : 메서드에 잘못된 파라미터가 전달되는 경우 발생.(0~65535 범위 밖의 포트 번호 사용, null 프록시(proxy) 전달)");
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), " Error : 메서드에 잘못된 파라미터가 전달되는 경우 발생.(0~65535 범위 밖의 포트 번호 사용, null 프록시(proxy) 전달)", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


    }
}