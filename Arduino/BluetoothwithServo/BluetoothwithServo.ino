#include <SoftwareSerial.h> //시리얼 통신 라이브러리 호출
#include "Servo.h" //서보 라이브러리
 
Servo myservo;
Servo myservo1;//서보객체
int blueTx=2;   //Tx (블투 보내는핀 설정)
int blueRx=3;   //Rx (블투 받는핀 설정)
SoftwareSerial mySerial(blueTx, blueRx);  //시리얼 통신을 위한 객체선언
String myString=""; //받는 문자열
int gak=90;
 
void setup() {
  myservo.attach(7);   //서보 시그널 핀설정
  myservo.write(90);     //서보 초기각도 0도 설정
  myservo1.attach(4);   //서보 시그널 핀설정
  myservo1.write(90);     //서보 초기각도 0도 설정
  
  mySerial.begin(9600); //블루투스 시리얼 개방
  Serial.begin(9600);
  myString="";
}
 
void loop() {
  while(mySerial.available())  //mySerial 값이 있으면
  {
    char myChar = (char)mySerial.read();  //mySerial int형식의 값을 char형식으로 변환
    myString+=myChar;   //수신되는 문자열을 myString에 모두 붙임 (1바이트씩 전송되는 것을 모두 붙임)
    delay(5);           //수신 문자열 끊김 방지
  }
  
  if(!myString.equals(""))  //myString 값이 있다면
  {
    
      mySerial.print(myString);//안드로이드스튜디오에 전송 1바이트씩 전송함 
      int value=Serial.read()+2;//이상하게 숫자값이 -1로들어옴 
      Serial.print(value); //시리얼모니터에 myString값 출력
      if(value==1)  //myString 값이 'on' 이라면
      {
        myservo.write(130);
        myservo1.write(130);//각도 60도로 움직임
        
      } 
      else if(myString=="Text")
      {
         myservo.write(0);
         myservo1.write(120);//각도 60도로 움직임
      }
      
      else {
        gak+=10;
        if(gak>150)
        {
           gak=0;
        }
        myservo.write(gak);
        myservo1.write(gak);
                        //각도 0도로 움직임
      }
      myString="";  //myString 변수값 초기화
  }
}
