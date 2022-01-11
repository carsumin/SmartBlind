package com.example.smart_blind3;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class alarm extends AppCompatActivity {

    Button finish, stop;
    String formatstr;
    String nowstr;


    private TimePicker morning;

    Handler handler = new Handler(); //토스트 띄우기 위한 메인스레드 핸들러 객체 생성

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        finish = findViewById(R.id.finish);
        stop = findViewById(R.id.stop);
        morning = findViewById(R.id.morning);

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        } //안드로이드 3.0부터는 추가해야 에러 안남

        finish.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {

                String addr = "192.168.200.180";
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, morning.getHour());
                calendar.set(Calendar.MINUTE, morning.getMinute());
                calendar.set(Calendar.SECOND, 0);

                SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
                formatstr = format.format(calendar.getTime());
                Toast.makeText(alarm.this, "예약 시간 : " + formatstr, Toast.LENGTH_SHORT).show();
                Toast.makeText(alarm.this, "예약 완료", Toast.LENGTH_SHORT).show();
                formatstr = formatstr.replace(":", "");

                ConnectThread thread = new ConnectThread(addr);
                thread.start();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"예약 취소",Toast.LENGTH_SHORT).show();
            }
        });
    }

    class ConnectThread extends Thread {
        private static final String TAG = "socketTest";
        String hostname;

        public ConnectThread(String addr) {
            hostname = addr;
        }

        public void run() {
            try { //클라이언트 소켓 생성
                int port = 12340;
                Socket socket = new Socket(hostname, port);//소켓 열어줌
                Log.d(TAG, "Socket 생성, 연결.");

                DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

                Calendar today = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.KOREA);
                nowstr = sdf.format(today.getTime()).replace(":", "");
                ;
//                Toast.makeText(getApplicationContext(),"현재시간 : " +nowstr,Toast.LENGTH_SHORT).show();

                if (nowstr.equals(formatstr)) {
                    dos.writeByte('A');
                } else {
                    dos.writeByte('G');
                }
                dos.flush();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        Toast.makeText(getApplicationContext(), "예약 완료되었습니다", Toast.LENGTH_SHORT).show();
                    }
                });
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
    }
}