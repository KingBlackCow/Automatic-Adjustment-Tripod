#include <SoftwareSerial.h>
#include "Servo.h" //서보 라이브러리
 
Servo myservo;
Servo myservo1;//서보객체
SoftwareSerial BTSerial(2, 3); // SoftwareSerial(RX, TX), 통신을 하기 위한 RX,TX 연결 핀번호
byte buffer[1024];
byte buffer2[2];// 데이터를 수신 받을 자료를 저장할 버퍼
int bufferPosition;  // 버퍼에 데이터를 저장할 때 기록할 위치
int gak;

void setup(){
  BTSerial.begin(9600); // 블루투스 모듈 초기화, 블르투스 연결
  Serial.begin(9600);  // 시리얼 모니터 초기화, pc와 연결
  bufferPosition = 0;  // 버퍼 위치 초기화
  gak=0;
   myservo.attach(7);   //서보 시그널 핀설정
  myservo.write(0);     //서보 초기각도 0도 설정
  myservo1.attach(4);   //서보 시그널 핀설정
  myservo1.write(0);     //서보 초기각도 0도 설정
  buffer2[0]=1;
  buffer2[1]='\0';
 
}
// loop문 안에서 데이터를 받아올 때는 한번에 한글자씩 받아오게 됨.
// 글자를 하나씩 받아와서 출력하고, 현재 bufferPositon에 맞게 데이터를 버퍼에 저장하고 bufferPositon을 1개 늘려줌. 
// 이렇게 계속 반복하여 문자열의 끝('\n') 이 나오게 되면 버퍼의 마지막에 ('\0')을 넣고 버퍼에 저장된 문자열을
// 다시 스마튼폰으로 전송하고 버퍼를 초기화 해준다.

void loop(){
  if (BTSerial.available()){  // 블루투스로 데이터 수신, 블루투스에서 신호가 있으면
    byte data = BTSerial.read(); // 수신 받은 데이터 저장
    
//    String inString = Serial.readStringUntil('\n');
//    Serial.print(inString); 
    Serial.write(data);  // 수신된 데이터 시리얼 모니터로 출력
    buffer[bufferPosition++] = data;  // 수신 받은 데이터를 버퍼에 저장
    
    
    if(data == '\n'){  // 문자열 종료 표시
      buffer[bufferPosition] = '\0';
   
     if(data%2==0){
        myservo.write(130);
        myservo1.write(130);//각도 60도로 움직임
      } 
      else{
        gak+=10;
         if(gak>150)
        {
           gak=0;
        }
        myservo.write(gak);
        myservo1.write(gak);
      }
      
      // 스마트폰으로 문자열 전송
      BTSerial.write(buffer, bufferPosition);
      bufferPosition = 0;
    }  
  }
}
