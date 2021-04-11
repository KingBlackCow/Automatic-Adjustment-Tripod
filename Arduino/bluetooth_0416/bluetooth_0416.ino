#include <SoftwareSerial.h>
#include "Servo.h" //서보 라이브러리
Servo myservo;
Servo myservo1;//서보객체


const int rx = 2; //Bluetooth TX 핀
const int tx = 3; //Bluetooth RX 핀
SoftwareSerial mySerial (rx, tx);
int gak1;
int gak2; 
void setup() {
  
  mySerial.begin(9600);    //시리얼 통신 9600 통신속도로 시작
  Serial.begin(9600);
  
  gak1=0;
  gak2=0;
  
  myservo.attach(7);   //서보 시그널 핀설정
  myservo.write(0);     //서보 초기각도 0도 설정
  myservo1.attach(4);   //서보 시그널 핀설정
  myservo1.write(0);     //서보 초기각도 0도 설정
}
void loop() {
  
  if (mySerial.available() > 0) { //데이터가 수신되는지 확인
    
      
      String inString = mySerial.readStringUntil('\n');
      int index1 = inString.indexOf(','); 
      //int index2 =  inString.indexOf(':');
      int index2 = inString.length();
      //Serial.println(index1);   
      //Serial.println(index2);
      int inString1 = inString.substring(0, index1).toInt();
      int inString2 = inString.substring(index1+1,index2).toInt();
      //int inString3 = inString.substring(index2+1,index3).toInt();
      Serial.println(inString1);   
      Serial.println(inString2);
      gak1+=inString1/100;
      gak2+=inString2/100;
      myservo.write(gak1);
      myservo1.write(gak2);
      if(gak1>100||gak2>100)
      {
        gak1=0;
        gak2=0;
      }
    //String stringsum=inString1+inString2;
      mySerial.println(inString);
     }
  }
