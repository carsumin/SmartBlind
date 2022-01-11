package com.example.smart_blind3;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class auto extends AppCompatActivity {

    public static int TO_GRID = 0;
    public static int TO_GPS = 1;

    String key = "Ub6FENpoFzVHrrTQiBVzt%2FdL5BAA43a6Kf2i1grv2J5PjCJba1QS7tvo3TEnIsnMfJCLjMTUxNmLju1xd23ZTg%3D%3D";
    String data;
    private LocationManager locationManager;
    private static final int REQUEST_CODE_LOCATION = 2;
    private Socket socket;
    int cloudcon;


    ImageButton autobtn;
    TextView weathertext, weather, automodeok;

    final int a = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto);
        autobtn = findViewById(R.id.autobtn);
        weathertext = findViewById(R.id.weathertext);
        weather = findViewById(R.id.weather);
        automodeok = findViewById(R.id.automodeok);

        //안드로이드 3.0부터는 추가해야 에러 안남
        //socket통신
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        weathertext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23 &&
                        ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(auto.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            0);
                } else {
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            1000,
                            1,
                            gpsLocationListener);
                    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            1000,
                            1,
                            gpsLocationListener);
                }

            }
        });

        weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String addr = "192.168.200.119";
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

                    if (cloudcon == 1) {
                        dos.writeByte('B');
                    } else if (cloudcon == 2) {
                        dos.writeByte('C');
                    } else if (cloudcon == 3) {
                        dos.writeByte('D');
                    }
                    dos.flush();

//                    String str = dis.readUTF();
//                    Log.i(str, "받아온 값");
//                    Toast.makeText(getApplicationContext(), "Server :" + str, Toast.LENGTH_LONG).show();


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


    //현재위치 위도경도->GRID값으로 변경
    public double longitude;
    public double latitude;
    public double x = 0;
    public double y = 0;
    public double lnga = 0;
    public double lata = 0;

    public LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {

            final String provider = location.getProvider();
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            lnga = Double.parseDouble(String.format("%.3f", longitude));
            lata = Double.parseDouble(String.format("%.3f", latitude));

        }
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        public void onProviderEnabled(String provider) {}
        public void onProviderDisabled(String provider) {}
    };

    private LatXLngY convertGRID_GPS(int mode, double lat_X, double lng_Y) {
        double RE = 6371.00877; // 지구 반경(km)
        double GRID = 5.0; // 격자 간격(km)
        double SLAT1 = 30.0; // 투영 위도1(degree)
        double SLAT2 = 60.0; // 투영 위도2(degree)
        double OLON = 126.0; // 기준점 경도(degree)
        double OLAT = 38.0; // 기준점 위도(degree)
        double XO = 43; // 기준점 X좌표(GRID)
        double YO = 136; // 기1준점 Y좌표(GRID)

        double DEGRAD = Math.PI / 180.0;
        double RADDEG = 180.0 / Math.PI;

        double re = RE / GRID;
        double slat1 = SLAT1 * DEGRAD;
        double slat2 = SLAT2 * DEGRAD;
        double olon = OLON * DEGRAD;
        double olat = OLAT * DEGRAD;

        double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
        double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
        double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);
        LatXLngY rs = new LatXLngY();

        if (mode == TO_GRID) {
            rs.lat = latitude;
            rs.lng = longitude;
            double ra = Math.tan(Math.PI * 0.25 + (lat_X) * DEGRAD * 0.5);
            ra = re * sf / Math.pow(ra, sn);
            double theta = lng_Y * DEGRAD - olon;
            if (theta > Math.PI) theta -= 2.0 * Math.PI;
            if (theta < -Math.PI) theta += 2.0 * Math.PI;
            theta *= sn;
            rs.x = Math.floor(ra * Math.sin(theta) + XO + 0.5);
            rs.y = Math.floor(ro - ra * Math.cos(theta) + YO + 0.5);
        } else {
            rs.x = lat_X;
            rs.y = lng_Y;
            double xn = lat_X - XO;
            double yn = ro - lng_Y + YO;
            double ra = Math.sqrt(xn * xn + yn * yn);
            if (sn < 0.0) {
                ra = -ra;
            }
            double alat = Math.pow((re * sf / ra), (1.0 / sn));
            alat = 2.0 * Math.atan(alat) - Math.PI * 0.5;

            double theta = 0.0;
            if (Math.abs(xn) <= 0.0) {
                theta = 0.0;
            } else {
                if (Math.abs(yn) <= 0.0) {
                    theta = Math.PI * 0.5;
                    if (xn < 0.0) {
                        theta = -theta;
                    }
                } else theta = Math.atan2(xn, yn);
            }
            double alon = theta / sn + olon;
            rs.lat = (alat * RADDEG);
            rs.lng = alon * RADDEG;
        }
        return rs;
    }

    public class LatXLngY {
        public double lat;
        public double lng;

        public double x;
        public double y;
    }

    public void mOnClick(View v) {
        switch (v.getId()) {
            case R.id.autobtn:

                //Android 4.0 이상 부터는 네트워크를 이용할 때 반드시 Thread 사용해야 함
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        data = getXmlData();//아래 메소드를 호출하여 XML data를 파싱해서 String 객체로 얻어오기
//                        Log.v("출력할 내용 :",data.toString());

                        //UI Thread(Main Thread)를 제외한 어떤 Thread도 화면을 변경할 수 없기때문에
                        //runOnUiThread()를 이용하여 UI Thread가 TextView 글씨 변경하도록 함
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                weathertext.setText(data); //TextView에 문자열  data 출력
                            }
                        });
                    }
                }).start();
                break;
        }
    }//mOnClick method..


    String getXmlData() {
        StringBuffer buffer = new StringBuffer();

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        Calendar today = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        String strToday = sdf.format(today.getTime()).replace("-", "");
        int to = Integer.parseInt(strToday) - 1;
        strToday = Integer.toString(to);

        LatXLngY tmp = convertGRID_GPS(TO_GRID, latitude, longitude);
        System.out.println(tmp);
        Log.e(">>", "x = " + (int) tmp.x + ", y = " + (int) tmp.y);

        if ((int) tmp.x > 100 || (int) tmp.x < 0) {
            weather.setText("잠시만 기다려주세요. 버튼을 다시 눌려주세요.");
        }

        String queryUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService/getVilageFcst"
                + "?serviceKey=" + key + "&numOfRows=10&pageNo=1"
                + "&base_date=" + strToday + "&base_time=2330&nx=" + (int) tmp.x + "&ny=" + (int) tmp.y + "";
        try {
            URL url = new URL(queryUrl);//문자열로 된 요청 url을 URL 객체로 생성.

            URLConnection t_connection = url.openConnection();
            t_connection.setReadTimeout(3000);
            InputStream is = t_connection.getInputStream(); //url위치로 입력스트림 연결

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();//xml파싱을 위한
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new InputStreamReader(is, "UTF-8")); //inputstream 으로부터 xml 입력받기

            String tag;

            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
//                        buffer.append("파싱 시작...\n\n");
                        break;

                    case XmlPullParser.START_TAG:
                        tag = xpp.getName();//태그 이름 얻어오기
//                        buffer.append(tag+"\n");

                        if (tag.equals("item")) ;  // 첫번째 검색결과
                        else if (tag.equals("baseDate")) {
                            xpp.next();
                        } else if (tag.equals("baseTime")) {
                            xpp.next();
                        } else if (tag.equals("category")) {
                            xpp.next();
                            if (xpp.getText().equals("SKY")) {
//                                buffer.append("날씨 : ");

//                                buffer.append(xpp.getText());//mapy 요소의 TEXT 읽어와서 문자열버퍼에 추가
//                                buffer.append("\n"); //줄바꿈 문자 추가
                            } else {
                                break;
                            }
                        } else if (tag.equals("fcstDate")) {
                            xpp.next();
                        } else if (tag.equals("fcstTime")) {
                            xpp.next();
                        } else if (tag.equals("fcstValue")) {
                            xpp.next();

                            if (xpp.getText().equals("1")) {
//                                buffer.append("맑음\n");
                                weather.setText("오늘의 날씨는 맑습니다.");
                                cloudcon = 1;
                            } else if (xpp.getText().equals("3")) {
//                                buffer.append("구름 많음\n");
                                weather.setText("오늘의 날씨는 구름이 많습니다.");
                                cloudcon = 2;
                            } else if (xpp.getText().equals("4")) {
//                                buffer.append("흐림\n");
                                weather.setText("오늘의 날씨는 흐립니다.");
                                cloudcon = 3;
                            } else {
//                                buffer.append(xpp.getText());//mapy 요소의 TEXT 읽어와서 문자열버퍼에 추가
//                                weather.setText(xpp.getText());
//                                buffer.append("\n"); //줄바꿈 문자 추가
                            }

                        } else if (tag.equals("nx")) {
                            xpp.next();
                        } else if (tag.equals("ny")) {
                            xpp.next();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        break;

                    case XmlPullParser.END_TAG:
                        tag = xpp.getName(); //태그 이름 얻어오기
//                        buffer.append(tag+"\n");

                        if (xpp.getName().equals("item"))
//                            buffer.append("\n");// 첫번째 검색결과종료..줄바꿈
                            break;
                }

                eventType = xpp.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        buffer.append("\n\n파싱 끝");
        return buffer.toString();       //StringBuffer 문자열 객체 반환

    }//getXmlData method...
}