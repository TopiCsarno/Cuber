//Servo rotations wrapped in functions
//values calibrated here

const int ROT_L = 4;   //Left position
const int ROT_R = 175;  //Right position
const int ROT_M = 90;   //Middle position

const int CLAW_OPEN = 135;  // 140 is too much
const int CLAW_CLOSE = 94;  // 97 enough? - probably
const int SPEED = 40;  // low number = slow (1-255)
const int TIME = 500;  // global delay between moves

const int CLAWDIF = 5;  //offset for the 2nd gripper (green)(higher = stronger grip)

void R1_Mid() {
  servoRot1.write(ROT_M, SPEED, true);
  delay(TIME);
}

void R1_Mid(int off) {
  servoRot1.write(ROT_M+off, SPEED, true);
  delay(TIME);
}

void R1_Right() {
  servoRot1.write(ROT_R, SPEED, true);
  delay(TIME);
}

void R1_Right(int off) {
  servoRot1.write(ROT_R+off, SPEED, true);
  delay(TIME);
}

void R1_Left() {
  servoRot1.write(ROT_L, SPEED, true);
  delay(TIME);
}

void R1_Left(int off) {
  servoRot1.write(ROT_L+off, SPEED, true);
  delay(TIME);
}

void R1_Open() {
  servoClaw1.write(CLAW_OPEN, SPEED, true);
  delay(TIME);
}

void R1_Close() {
  servoClaw1.write(CLAW_CLOSE, SPEED, true);
  delay(TIME);
}

//R2

void R2_Mid() {   //important for gripping
  servoRot2.write(ROT_M+2, SPEED, true);
  delay(TIME);
}

void R2_Mid(int off) {   //important for gripping
  servoRot2.write(ROT_M+1+off, SPEED, true);
  delay(TIME);
}

void R2_Right() {
  servoRot2.write(ROT_R-1, SPEED, true);
  delay(TIME);
}

void R2_Right(int off) {
  servoRot2.write(ROT_R-off, SPEED, true);
  delay(TIME);
}

void R2_Left() {
  servoRot2.write(ROT_L+10, SPEED, true);
  delay(TIME);
}

void R2_Left(int off) {
  servoRot2.write(ROT_L+10+off, SPEED, true);
  delay(TIME);
}

void R2_Open() {                                  
  servoClaw2.write(CLAW_OPEN - CLAWDIF, SPEED, true);
  delay(TIME);
}

void R2_Close() {
  servoClaw2.write(CLAW_CLOSE - CLAWDIF, SPEED, true);
  delay(TIME);
}

void finish() {
  Serial.println("Finished.");
  int dist = 8;
  servoClaw1.write(CLAW_CLOSE + dist, SPEED);
  servoClaw2.write(CLAW_CLOSE + dist, SPEED, true);
  delay(500);
  servoRot1.detach();
  servoClaw1.detach();
  servoRot2.detach();
  servoClaw2.detach();
}

void start() {

  servosOn();  
  delay(2000);
  servoRot1.write(ROT_M+15, SPEED, true);
  delay(250);
  servoRot1.write(ROT_M, SPEED, true);
  delay(250);
  servoRot2.write(ROT_M+15, SPEED, true);
  delay(250);
  servoRot2.write(ROT_M-3, SPEED, true);
  delay(1000);
  servoClaw1.write(CLAW_OPEN, SPEED);
  servoClaw2.write(CLAW_OPEN - CLAWDIF, SPEED, true);
  delay(500);
  int dist = 4;
  servoClaw1.write(CLAW_CLOSE + dist, SPEED);
  servoClaw2.write(CLAW_CLOSE - CLAWDIF + dist, SPEED, true);

  waitForInput();
  //delay(5000);
    
  servoClaw1.write(CLAW_CLOSE, SPEED);
  servoClaw2.write(CLAW_CLOSE - CLAWDIF, SPEED, true);
  delay(TIME);
}
