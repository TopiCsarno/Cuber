#include <VarSpeedServo.h>

VarSpeedServo servoRot1;
VarSpeedServo servoClaw1;
VarSpeedServo servoRot2;
VarSpeedServo servoClaw2;

const byte numChars = 128;
char input[numChars];

boolean newData = false;
boolean paused = false;
int Xoff = 0;

void setup() {
  Serial.begin(9600);  
}

void loop() {

  receiveMessage();
  parseMessage();
  delay(50);  

  // Testing... 
  /*
  delay(1000);
  start();
  
  delay(1000);
  R2_TurnSide(1);
  delay(1000);  
  R2_TurnSide(2);
  delay(1000);
  R2_TurnSide(3);
  delay(1000);
  
  finish();
  delay(20000);
  */  
}

void servosOn() {
  servoRot1.attach(5);   //UNIT1 - Rotator
  servoClaw1.attach(6);  //UNIT1 - Claw
  servoClaw2.attach(9);  //UNIT2 - Claw
  servoRot2.attach(10);  //UNIT2 - Rotator
}

void parseMessage() {
  if (newData == true) {
    Serial.print("Message recieved: ");
    Serial.println(input);

    if (strcmp(input, "next") == 0) {
      Serial.println("Next move executed");
      newData = false;
      paused = false;
    
    } else if (strcmp(input, "start") == 0)  {
      Serial.println("Start robot");
      newData = false;
      start();  
      delay(2000);
      autoScan();  

    } else if (strcmp(input, "finish") == 0)  {
      Serial.println("Release cube");
      newData = false;
      finish();
    
    } else if (strcmp(input, "next") == 0)  {
      Serial.println("Next move");
      newData = false;
      paused = false;
    
    } else {      
    for (int i = 0; i < strlen(input); i++ ) {
        switch (input[i]) {

          case 'R':
            if (input[i + 1] == '2') {
              R(2);
              i++;
            } else if (input[i + 1] == 39) { // the ' character
              R(3);
              i++;
            } else {
              R(1);
            }
            break;

          case 'L':
            if (input[i + 1] == '2') {
              L(2);
              i++;
            } else if (input[i + 1] == 39) { // the ' character
              L(3);
              i++;
            } else {
              L(1);
            }
            break;

          case 'U':
            if (input[i + 1] == '2') {
              U(2);
              i++;
            } else if (input[i + 1] == 39) { // the ' character
              U(3);
              i++;
            } else {
              U(1);
            }
            break;

          case 'D':
            if (input[i + 1] == '2') {
              D(2);
              i++;
            } else if (input[i + 1] == 39) { // the ' character
              D(3);
              i++;
            } else {
              D(1);
            }
            break;

          case 'F':
            if (input[i + 1] == '2') {
              Fr(2);
              i++;
            } else if (input[i + 1] == 39) { // the ' character
              Fr(3);
              i++;
            } else {
              Fr(1);
            }
            break;

          case 'B':
            if (input[i + 1] == '2') {
              B(2);
              i++;
            } else if (input[i + 1] == 39) { // the ' character
              B(3);
              i++;
            } else {
              B(1);
            }
            break;
          case 'Y':
            HandleOffset();
          default:
            Serial.print("char unknown: ");
            Serial.println(input[i]);
        }
      }
    }

    //end
    newData = false;
  }
}

void receiveMessage() {
  static boolean recvInProgress = false;
  static byte ndx = 0;
  char startMarker = '<';
  char endMarker = '>';
  char rc;

  while (Serial.available() > 0 && newData == false) {
    rc = Serial.read();

    if (recvInProgress == true) {
      if (rc != endMarker) {
        input[ndx] = rc;
        ndx++;
        if (ndx >= numChars) {
          ndx = numChars - 1;
        }
      }
      else {
        input[ndx] = '\0'; // terminate the string
        recvInProgress = false;
        ndx = 0;
        newData = true;
      }
    }

    else if (rc == startMarker) {
      recvInProgress = true;
    }
  }
}

void waitForInput() {
  paused = true;
  while (paused) {
    receiveMessage();
    parseMessage();
  }
}
