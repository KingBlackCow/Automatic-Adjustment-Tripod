#include <SoftwareSerial.h>
 
int bluetoothTx = 2;
int bluetoothRx = 3;
 
SoftwareSerial bluetooth(bluetoothTx, bluetoothRx);
 
void setup()
{
  //Setup usb serial connection to computer
  Serial.begin(9600);
  bluetooth.begin(9600);
}
 
 
void loop()
{ 
 
  if(bluetooth.available())
  {
    char toSend = (char)bluetooth.read();
    Serial.print(toSend);
  }
  
  //Read from usb serial to bluetooth
  if(Serial.available())
  {
    char toSend = (char)Serial.read();
    bluetooth.print(toSend);
    //printf("\n");
  }
}
