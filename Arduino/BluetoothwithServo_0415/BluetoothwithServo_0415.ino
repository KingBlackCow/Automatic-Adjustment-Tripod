#include <Servo.h> 

Servo servo;

Servo servo1;

int servoPin = 7;
int servoPin1 = 4;
int angle = 0; // servo position in degrees 

void setup() 
{ 
  servo.attach(servoPin);
  servo1.attach(servoPin1);
} 

void loop() 
{ 
  // rotate from 0 to 180 degrees
  for(angle = 0; angle < 180; angle++) 
  { 
    servo.write(angle); 
    servo1.write(angle); 
    delay(15); 
  } 
}
