//basic side rotations and whole cube rotations


void HandleOffset() {
  int offset = Xoff % 4;
  Xoff = 0;
  R1_TurnCube(offset);
}

void R1_TurnCube(int i) {
  //Clockwise
  if (i == 1) {
    R2_Open();
    R1_Right(-3);
    R2_Close();
    R1_Open();
    R1_Mid();
    R1_Close();

    //Double
  } else if (i == 2) {
    R1_Open();
    R1_Left(+2);
    R1_Close();
    R2_Open();
    R1_Right(-3);
    R2_Close();
    R1_Open();
    R1_Mid();
    R1_Close();

    //Counter clockwise
  } else if (i == 3) {
    R2_Open();
    R1_Left(+4);
    R2_Close();
    R1_Open();
    R1_Mid(-5);
    R1_Close();
  }
}
void R1_TurnSide(int i) {

  //Clockwise
  if (i == 1) {
    R1_Right(-2);
    R1_Open();
    R1_Mid();
    R1_Close();

    //Double
  } else if (i == 2) {
    R1_Open();
    R1_Left();
    R1_Close();
    R1_Right(-2);
    R1_Open();
    R1_Mid();
    R1_Close();

    //Counter clockwise
  } else if (i == 3) {
    R1_Left(+1);
    R1_Open();
    R1_Mid(-6);
    R1_Close();
  }
}

//R2

void R2_TurnCube(int i) {

  //Clockwise
  if (i == 1) {
    R1_Open();
    R2_Right(+3);   //*
    R1_Close();
    R2_Open();
    R2_Mid();
    R2_Close();

    //Double
  } else if (i == 2) {
    //Double needed only
    R2_Open();
    R2_Left();
    R2_Close();
    R1_Open();
    R2_Right(+5);
    R1_Close();
    R2_Open();
    R2_Mid(-3);
    R2_Close();

    
  } //Counter clockwise
  //else if (i == 3) {
  //R1_Open();
  // R2_Left(+4);
  //R1_Close();
  //R2_Open();
  // R2_Mid();
  // R2_Close();
  //}
}

void R2_TurnSide(int i) {

  //Clockwise
  if (i == 1) {
    R2_Right(+4);
    R2_Open();
    R2_Mid(-1);
    R2_Close();

    //Double
  } else if (i == 2) {
    R2_Open();
    R2_Left();
    R2_Close();
    R2_Right(+3);
    R2_Open();
    R2_Mid(-2);
    R2_Close();

    //Counter clockwise
  } else if (i == 3) {
    R2_Left(-1);
    R2_Open();
    R2_Mid();
    R2_Close();
  }
}

//autoscanning
void autoScan() {
  waitForInput();
  R1_TurnCube(1);
  waitForInput();
  R1_TurnCube(1);
  waitForInput();
  R1_TurnCube(1);
  waitForInput();
  R2_TurnCube(1);
  R1_TurnCube(1);
  waitForInput();
  R1_TurnCube(2);
  R2_TurnCube(2);
  waitForInput();
  R1_TurnCube(3);
  R2_TurnCube(1);
  R1_TurnCube(1);
}
