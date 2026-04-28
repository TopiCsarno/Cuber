package topi.cuber;


import android.util.Log;

import cs.min2phase.Search;
import cs.min2phase.Tools;

public class CubeExplorer {

    private String cubeString;
    private String[] cubeSides;
    private final int maxDepth = 25;
    private final int maxProbes = 100000000;
    private final int minProbes = 10000;


    public CubeExplorer() {
        cubeString = "";
        this.cubeSides = new String[6];
    }

    public String[] getCubeSides() {
        return cubeSides;
    }

    public int getScannedCount() {
        int temp = 0;
        for (String s : cubeSides) {
            if (s != null) {
                temp++;
            }
        }
        return temp;
    }

    public void addScannedSide(String side) {

        if (side == null) return;
        if (side.length() < 4) return;
        switch (side.charAt(4)) {
            case 'U':
                cubeSides[0] = side;
                break;
            case 'R':
                cubeSides[1] = side;
                break;
            case 'F':
                cubeSides[2] = side;
                break;
            case 'D':
                cubeSides[3] = side;
                break;
            case 'L':
                cubeSides[4] = side;
                break;
            case 'B':
                cubeSides[5] = side;
                break;
        }
    }

    private void buildCubeString () {
        cubeString = "";
        for (String side : cubeSides) {
            cubeString += side;
        }
    }

    public String solveCube() {
        buildCubeString();
        Log.i("CubeExplorer", "cubestring: " + cubeString);
        int verifyResult = Tools.verify(cubeString);
        if (verifyResult == 0) {
            return new Search().solution(cubeString, maxDepth, maxProbes, minProbes, 0);
        } else {
            return "Error code: " + verifyResult;
        }
    }

    public void clear() {
        cubeString = "";
        cubeSides = new String[6];
    }
}