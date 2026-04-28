//Face rotations

//Up
void U (int i) {
  R2_TurnCube(2);
  R1_TurnSide(i);
  R2_TurnCube(2);
}

//Front
void Fr (int i) {
  Xoff += 2 ;
  HandleOffset();
  R2_TurnSide(i);
  Xoff += 2;
}

//Left
void L (int i) {
  Xoff += 3 ;
  HandleOffset();
  R2_TurnSide(i);
  Xoff += 1;
}

//Right
void R (int i) {
  Xoff += 1 ;
  HandleOffset();
  R2_TurnSide(i);
  Xoff += 3;
}

//Down
void D (int i) {
  HandleOffset();
  R1_TurnSide(i);
}

//Back
void B (int i) {
  HandleOffset();
  R2_TurnSide(i);
}


