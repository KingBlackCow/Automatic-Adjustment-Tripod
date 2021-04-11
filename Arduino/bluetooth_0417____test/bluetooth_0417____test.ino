#include <SoftwareSerial.h>
#include <Servo.h>    // 서보모터 라이브러리
#include <IRremote.h>  // 적외선 리모컨 라이브러리

int remote = 11;    // 적외선 리모컨 수광 다이오드 신호선
     // 서보모터
int motor=7;
const int rx = 2; //Bluetooth TX 핀
const int tx = 3; //Bluetooth RX 핀
SoftwareSerial mySerial (rx, tx);
Servo myservo_LR ;       
Servo myservo_UD;
IRrecv irrecv(remote);    // IRremote를 사용해주기 위해 irrecv객체 생성
decode_results results;   // 디코드한 결과값
  
int angle = 0;    // 서보모터 각도 값
int  gak_LR=97;
int  gak_UD=140;
int centerX;
int centerY;
void setup() {
  mySerial.begin(9600);    //시리얼 통신 9600 통신속도로 시작
  Serial.begin(9600);
  
  myservo_LR.attach(7);    // 모터 시작
  myservo_LR.write(97);     //좌우 서보
  myservo_UD.attach(4);   //상하 서보
  myservo_UD.write(140);     //서보 초기각도 0도 설정
  irrecv.enableIRIn();    // 수광 다이오드 시작
  gak_LR=97;//좌우
  gak_UD=140;//상하
 
  pinMode(6,OUTPUT);  // 모터 출력
  pinMode(remote, INPUT); // 수광 다이오드 입력
 
}

void loop() {
  if (mySerial.available() > 0) { //데이터가 수신되는지 확인
      String inString = mySerial.readStringUntil('\n');

      int index0=inString.indexOf(':');
      int index1 = inString.indexOf(','); 
      int index2 = inString.length();

      int inString0 = inString.substring(0, index0).toInt();
      int inString1 = inString.substring(index0+1, index1).toInt();
      int inString2 = inString.substring(index1+1,index2).toInt();


      if(inString0==10)
      {
          if(abs(inString1)>10)
          {
            gak_UD+=inString1/5;
          }
          if(abs(inString2)>10)  
          {
            gak_LR+=inString2/5;
          }
        
          centerX=gak_UD;
          centerY=gak_LR;
          myservo_LR.write(centerY);
          myservo_UD.write(centerX);
      }
    
      else if(inString0==1)
      {
       
//        gak_LR+=inString1/7;
//        gak_UD+=inString2/12;
          if(inString1>0){
          for(int i=0;i<inString1/7;i++)
          {
             gak_LR+=1;
             myservo_LR.write(gak_LR);//좌우
             delay(30);
          }
        }
        else{
          for(int i=0;i>inString1/7;i--)
          {
              gak_LR--;
              myservo_LR.write(gak_LR);//좌우
              delay(30);
          }
        }
         if(inString2>0){
          for(int i=0;i<inString2/11;i++)
          {
             gak_UD+=1;
             myservo_UD.write(gak_UD);//좌우
             delay(30);
          }
        }
        else{
          for(int i=0;i>inString2/11;i--)
          {
              gak_UD-=1;
              myservo_UD.write(gak_UD);//좌우
              delay(30);
          }
        }
        
      }
      else if(inString0==2)
      {
//        gak_LR+=inString1/5.3;
//        gak_UD+=inString2/12;
          if(inString1>0){
          for(int i=0;i<inString1/5.3;i++)
          {
             gak_LR++;
             myservo_LR.write(gak_LR);//좌우
             delay(30);
          }
        }
        else{
          for(int i=0;i>inString1/5.3;i--)
          {
              myservo_LR.write(gak_LR);//좌우
              gak_LR--;
              delay(30);
          }
        }
          if(inString2>0){
          for(int i=0;i<inString2/11;i++)
          {
             gak_UD++;
             myservo_UD.write(gak_UD);//좌우
             delay(30);
          }
        }
        else{
          for(int i=0;i>inString2/11;i--)
          {
              gak_UD--;
              myservo_UD.write(gak_UD);//좌우
              delay(30);
          }
        }
      }
      else if(inString0==3)
      {
//        gak_LR+=inString1/7;
//        gak_UD+=inString2/12;
          if(inString1>0){
          for(int i=0;i<inString1/7;i++)
          {
              gak_LR++;
             myservo_LR.write(gak_LR);//좌우
             delay(30);
          }
        }
        else{
          for(int i=0;i>inString1/7;i--)
          {
              gak_LR--;
              myservo_LR.write(gak_LR);//좌우
              delay(30);
          }
        }
          if(inString2>0){
          for(int i=0;i<inString2/11;i++)
          {
              gak_UD++;
             myservo_UD.write(gak_UD);//좌우
             delay(30);
          }
        }
        else{
          for(int i=0;i>inString2/11;i--)
          {
              gak_UD--;
              myservo_UD.write(gak_UD);//좌우
              delay(30);
          }
        }
      }
      else if(inString0==4)
      {
//        gak_LR+=inString1/7;
//        gak_UD+=inString2/8;
   if(inString1>0){
          for(int i=0;i<inString1/7;i++)
          {
              gak_LR++;
             myservo_LR.write(gak_LR);//좌우
             delay(30);
          }
        }
        else{
          for(int i=0;i>inString1/7;i--)
          {
              gak_LR--;
              myservo_LR.write(gak_LR);//좌우
              delay(30);
          }
        }
          if(inString2>0){
          for(int i=0;i<inString2/8;i++)
          {
              gak_UD++;
             myservo_UD.write(gak_UD);//좌우
             delay(30);
          }
        }
        else{
          for(int i=0;i>inString2/8;i--)
          {
              gak_UD--;
              myservo_UD.write(gak_UD);//좌우
              delay(30);
          }
        }
      }
    
      else if(inString0==5)
      {
//        gak_LR+=inString1/5.3;
//        gak_UD+=inString2/8;
          if(inString1>0){
              for(int i=0;i<inString1/5.3;i++)
              {
                 gak_LR++;
                 myservo_LR.write(gak_LR);//좌우
                 delay(30);
              }
           }
          else{
            for(int i=0;i>inString1/5.3;i--)
            {
                gak_LR--;
                myservo_LR.write(gak_LR);//좌우
                delay(30);
            }
          }
          if(inString2>0){
            for(int i=0;i<inString2/8;i++)
            {
                gak_UD++;
               myservo_UD.write(gak_UD);//좌우
               delay(50);
            }
         }
         else{
            for(int i=0;i>inString2/8;i--)
            {
                gak_UD--;
                myservo_UD.write(gak_UD);//좌우
                delay(50);
            }
         }
      }
      else if(inString0==6)
      {
//        gak_LR+=inString1/7;
//        gak_UD+=inString2/8;
   if(inString1>0){
          for(int i=0;i<inString1/7;i++)
          {
              gak_LR++;
             myservo_LR.write(gak_LR);//좌우
             delay(30);
          }
        }
        else{
          for(int i=0;i>inString1/7;i--)
          {
              gak_LR--;
              myservo_LR.write(gak_LR);//좌우
              delay(30);
          }
        }
          if(inString2>0){
          for(int i=0;i<inString2/8;i++)
          {
             gak_UD++;
             myservo_UD.write(gak_UD);//좌우
             delay(30);
          }
        }
        else{
          for(int i=0;i>inString2/8;i--)
          {
              gak_UD--;
              myservo_UD.write(gak_UD);//좌우
              delay(30);
          }
        }
      }
     
      else if(inString0==7)
      {

          if(inString1>0){
          for(int i=0;i<inString1/7;i++)
          {
              gak_LR++;
             myservo_LR.write(gak_LR);//좌우
             delay(30);
          }
        }
        else{
          for(int i=0;i>inString1/7;i--)
          {
              gak_LR--;
              myservo_LR.write(gak_LR);//좌우
              delay(30);
          }
        }
          if(inString2>0){
          for(int i=0;i<inString2/5.5;i++)
          {
              gak_UD++;
             myservo_UD.write(gak_UD);//좌우
             delay(30);
          }
        }
        else{
          for(int i=0;i>inString2/5.5;i--)
          {
              gak_UD--;
              myservo_UD.write(gak_UD);//좌우
              delay(30);
          }
        }
      }
       else if(inString0==8)
      {
//        gak_LR+=inString1/5;
//        gak_UD+=inString2/5.5;
          if(inString1>0){
          for(int i=0;i<inString1/5;i++)
          {
              gak_LR++;
             myservo_LR.write(gak_LR);//좌우
             delay(30);
          }
        }
        else{
          for(int i=0;i>inString1/5;i--)
          {
              gak_LR--;
              myservo_LR.write(gak_LR);//좌우
              delay(30);
          }
        }
          if(inString2>0){
          for(int i=0;i<inString2/5.5;i++)
          {
              gak_UD++;
             myservo_UD.write(gak_UD);//좌우
             delay(30);
          }
        }
        else{
          for(int i=0;i>inString2/5.5;i--)
          {
              gak_UD--;
              myservo_UD.write(gak_UD);//좌우
              delay(30);
          }
        }
      }
       else if(inString0==9)
      {
//        gak_LR+=inString1/7;
//        gak_UD+=inString2/5.5;
           if(inString1>0){
          for(int i=0;i<inString1/7;i++)
          {
              gak_LR++;
             myservo_LR.write(gak_LR);//좌우
             delay(30);
          }
        }
        else{
          for(int i=0;i>inString1/7;i--)
          {
              gak_LR--;
              myservo_LR.write(gak_LR);//좌우
              delay(30);
          }
        }
          if(inString2>0){
          for(int i=0;i<inString2/5.5;i++)
          {
              gak_UD++;
             myservo_UD.write(gak_UD);//좌우
             delay(30);
          }
        }
        else{
          for(int i=0;i>inString2/5.5;i--)
          {
              gak_UD--;
              myservo_UD.write(gak_UD);//좌우
              delay(30);
          }
        }
      }
      else if(inString0==12)
      {
//        gak_LR+=inString1/7;
//        gak_UD+=inString2/5.5;
        if(inString1>0){
          for(int i=0;i<inString1/6.7;i++)
          {
             gak_LR++;
             myservo_LR.write(gak_LR);//좌우
             delay(30);
          }
        }
        else{
          for(int i=0;i>inString1/6.7;i--)
          {
              gak_LR--;
              myservo_LR.write(gak_LR);//좌우
              delay(30);
          }
        }
          if(inString2>0){
          for(int i=0;i<inString2/7;i++)
          {
             gak_UD++;
             myservo_UD.write(gak_UD);//상하
             delay(30);
          }
        }
        else{
          for(int i=0;i>inString2/6;i--)
          {
              gak_UD--;
              myservo_UD.write(gak_UD);//상하
              delay(30);
          }
        }
      }

      else if(inString0==13)
      {
      

        
      }
      
      Serial.println(inString1);   
      Serial.println(inString2);


//     myservo_LR.write(gak_LR);//좌우
//     delay(400);
//     myservo_UD.write(gak_UD);//상하
     

      
        
   }
   if (irrecv.decode(&results)){  // 적외선 리모컨의 신호를 받을 때
        switch (results.value) {
          case 0xFFA857:  // +  버튼을 눌렀을 때
            mySerial.println("+");
            break;
            
          case 0xFF6897:
            mySerial.println("0");// 0 버튼을 눌렀을 때
            break;
          
          
          case 0xFF30CF:  // 1  버튼을 눌렀을 때
            mySerial.println("1");
           
            break;
          case 0xFF18E7:  // 2  버튼을 눌렀을 때
            mySerial.println("2");
           
            break;
          case 0xFF7A85:  // 3  버튼을 눌렀을 때
            mySerial.println("3");
           
            break;
          case 0xFF10EF:  // 4  버튼을 눌렀을 때
            mySerial.println("4");
           
            break;
          case 0xFF38C7:  // 5  버튼을 눌렀을 때
            mySerial.println("5");
           
            break;
          case 0xFF5AA5:  // 6  버튼을 눌렀을 때
            mySerial.println("6");
           
            break;
          case 0xFF42BD:  // 7  버튼을 눌렀을 때
            mySerial.println("7");
           
            break;
          case 0xFF4AB5:  // 8  버튼을 눌렀을 때
            mySerial.println("8");
           
            break;
          case 0xFF52AD:  // 9  버튼을 눌렀을 때
            mySerial.println("9");
           
            break;
          case 0xFF9867:  // 100+버튼
            mySerial.println("11");
            break;

        case 0xFFB04F:  // 200+버튼
            mySerial.println("12");
            break;  

        case 0xFFE01F:  // -버튼
            gak_LR=97;
            gak_UD=140;
            myservo_LR.write(gak_LR);//좌우
            delay(400);
            myservo_UD.write(gak_UD);//상하
            break;

         case 0xFFA25D:  // ch-버튼
            mySerial.println("13");
            break;

                    
    }
    irrecv.resume(); // 수광 다이오드 다음 값 받기
  }
 }


  
 
