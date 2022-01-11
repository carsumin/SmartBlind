package com.example.smart_blind3;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class manual extends AppCompatActivity {
    Button manualup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);
        final NumberPicker manual = findViewById(R.id.manual);
        manual.setMinValue(1);
        manual.setMaxValue(3);
        manual.setWrapSelectorWheel(false);
        manualup = findViewById(R.id.manualup);

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        } //안드로이드 3.0부터는 추가해야 에러 안남


        manualup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(), "Connect시도", Toast.LENGTH_SHORT).show();
                String addr = "192.168.200.180";
                final String TAG = "socketTest";
                String hostname;
                hostname = addr;
                try {
                    int port = 12340;
                    String up = Integer.toString(manual.getValue());
                    Socket socket = new Socket(hostname, port);//소켓 열어줌
                    Log.d(TAG, "Socket 생성, 연결.");
//                    Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();


                    DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

                    if (up.equals("1")) {
                        dos.writeByte('B');
                    } else if (up.equals("2")) {
                        dos.writeByte('C');
                    } else if (up.equals("3")) {
                        dos.writeByte('D');
                    }
                    dos.flush();

                    Toast.makeText(getApplicationContext(), "스마트 블라인드가 내려갑니다", Toast.LENGTH_SHORT).show();

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