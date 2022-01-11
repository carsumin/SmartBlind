#include <WiFi.h>

#define INTERVAL 500 //500ms 간격
#define ARRAY_LENGTH 12 //0.5초 간격 * 12개 데이터 = 6초
#define CDS 33

const char* ssid = "HelloWireless08F3";
const char* password = "1602004371";

//조도센서 평균값을 구하기 위한 변수
int index_=0;
int sensor_array[ARRAY_LENGTH];
int average=0;


// GPIO 디지털 신호 핀 설정
int motor1Pin1 = 27;
int motor1Pin2 = 26; 
int enable1Pin = 14; 

// PWM properties 모터 설정
const int freq = 30000;   //30000Hz 신호 생성
const int pwmChannel = 0; //채널 설정
const int resolution = 8; //8bit해상도 설정
int dutyCycle = 100;      //속도 설정(0~255까지 설정가능)

//서버에서 데이터 값을 byte로 받기 위해 설정
static boolean clientActive = false;
byte incomingByte;

void setup(){
  //모터와 조도센서 핀 번호 설정
  pinMode(motor1Pin1, OUTPUT);
  pinMode(motor1Pin2, OUTPUT);
  pinMode(enable1Pin, OUTPUT);
  digitalWrite(motor1Pin1, LOW);
  digitalWrite(motor1Pin2, LOW); 

  //모터 설정
  ledcSetup(pwmChannel, freq, resolution);
  ledcAttachPin(enable1Pin, pwmChannel);
  ledcWrite(pwmChannel, dutyCycle);   //속도 제어

  Serial.print("DC Motor is Ready");

  //와이파이 설정
  Serial.begin(115200);
  delay(10);

  WiFi.begin (ssid, password);

  Serial.println();
  Serial.println();
  Serial.print("Waiting for WiFi... ");

  while(WiFi.status()!= WL_CONNECTED) {
      Serial.print(".");
      delay(500);
  }

  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());

  delay(500);
}


//조도센서 평균값을 구하는 함수
int avg(int sensor_array[]){
    int sum=0;
    for (int i=0; i<ARRAY_LENGTH; i++){
      sum+=sensor_array[i];
    }
    return average = sum/ARRAY_LENGTH;
}

void loop(){
    //TCP통신을 위한 port번호와 ip주소 설정
    const uint16_t port = 12369;
    const char * host = "192.168.200.180";
  
    Serial.print("Connecting to ");
    Serial.println(host);
  
    //WiFiClient 클래스를 사용하여 TCP 연결 만들기
    WiFiClient client;

    if (!client.connect(host, port)) {
        Serial.println("Connection failed.");
        Serial.println("Waiting 5 seconds before retrying...");
        delay(5000);
        return;
    }
  
    int maxloops = 0;

    //서버의 응답을 받을 수 있을 때까지 기다림
    while (!client.available() && maxloops < 1000) {
      maxloops++;
      delay(1); //delay 1 msec
    }

    //#조도센서 설정
    //#1. 센서 설정 : 현재 센서값
    int cdsValue = analogRead(CDS);  
    Serial.print("\n"); Serial.print("\n");
    Serial.print("cdsValue=");
    Serial.print(cdsValue);
    client.print(cdsValue);
    Serial.print("\n");  
  
    //#2. 최근 6초동안의 데이터 모음
    for (int i=0; i<ARRAY_LENGTH; i++){
      Serial.print(sensor_array[i]);    
      Serial.print(", ");
    }  
    Serial.print("\n");
  
    //#3. 최근 6초동안의 데이터 평균값
    sensor_array[index_]=cdsValue;
    
    if (index_ == ARRAY_LENGTH - 1) {
      int average = avg(sensor_array);
      Serial.print("avg=");
      Serial.print(average);
      client.print(average);
      Serial.print("\n"); Serial.print("\n");
      average = 0;
    }
    
    //#4.데이터 저장 위치 변경 
    index_=(index_+1)%ARRAY_LENGTH;
    delay(INTERVAL);


   //클라이언트가 연결되어 있으면 true를 반환하고 그렇지 않으면 false를 반환
   if (client.connected()) {
    if (!clientActive)
    Serial.println("New Client connection.");

    clientActive = true;

    //서버에서 넘어온 데이터 값을 바이트 수로 반환
    if (client.available()){ 
      //서버에서 받은 값에 따라 switch문으로 제어
       switch(client.read()){
        case 65 :
          Serial.println("블라인드를 내립니다");
          digitalWrite(motor1Pin1, HIGH);
          digitalWrite(motor1Pin2, LOW);
          delay(5000);
          digitalWrite(motor1Pin1, LOW);
          digitalWrite(motor1Pin2, LOW);
          break;
         case 66 :
          Serial.println("3초 동안 내림");
          digitalWrite(motor1Pin1, HIGH);
          digitalWrite(motor1Pin2, LOW);
          delay(3000);
          digitalWrite(motor1Pin1, LOW);
          digitalWrite(motor1Pin2, LOW);
          break;
         case 67 :
          Serial.println("1초 동안 내림");
          digitalWrite(motor1Pin1, HIGH);
          digitalWrite(motor1Pin2, LOW);
          delay(1000);
          digitalWrite(motor1Pin1, LOW);
          digitalWrite(motor1Pin2, LOW);
          break;
         case 68 :
          Serial.println("3초 동안 올림");
          digitalWrite(motor1Pin1, LOW);
          digitalWrite(motor1Pin2, HIGH);
          delay(3000);
          digitalWrite(motor1Pin1, LOW);
          digitalWrite(motor1Pin2, LOW);
          break;
         case 69 :
          Serial.println("블라인드를 올립니다");
          digitalWrite(motor1Pin1, LOW);
          digitalWrite(motor1Pin2, HIGH);
          delay(5000);
          digitalWrite(motor1Pin1, LOW);
          digitalWrite(motor1Pin2, LOW);
          break;
         case 70 :
          Serial.println("조도량을 보냅니다");
          break;
         case 71 :
          Serial.println("예약 완료");
          break;
         case 72 : 
          break;
       }
    }
  }else {
    Serial.println("client.available() timed out ");
  }
    
    delay(2000);
    client.stop();
    Serial.println("Waiting 2 seconds before restarting...");
    delay(2000);
}
