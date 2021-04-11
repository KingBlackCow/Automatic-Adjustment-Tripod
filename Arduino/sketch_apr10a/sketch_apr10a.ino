#include<Servo.h>
Servo myservo;
Servo myservo1;
int pos=0;
int potpin=0;
int val;
void setup() 
{
  myservo.attach(6);
  myservo1.attach(4);
  Serial.begin(9600);
  myservo.write(90); //좌우 7번
  myservo1.write(80);//상하 4번
}

void loop() 
{
//  val=analogRead(potpin);
//  val=map(val,0,1023,0,180);
//  myservo1.write(val);
//  delay(150);
//for(pos = 10; pos < 170; pos += 1)  // 0도에서 180도로 이동합니다.
//  {                                  // 이동할때 각도는 1도씩 이동합니다. 
//    myservo.write(pos);  
//    myservo1.write(pos);// 'pos'변수의 위치로 서보를 이동시킵니다.
//    delay(15);                       // 서보 명령 간에 20ms를 기다립니다.
//  } 
//  for(pos = 170; pos>=10; pos-=1)     // 180도에서 0도로 이동합니다.
//  {                                
//    myservo.write(pos); 
//    myservo1.write(pos);// 서보를 반대방향으로 이동합니다.
//    delay(15);                       // 서보 명령 간에 20ms를 기다립니다.
//  } 

}
