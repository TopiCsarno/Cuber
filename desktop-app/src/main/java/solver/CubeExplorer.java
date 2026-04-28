package solver;

import org.kociemba.twophase.Search;
import org.kociemba.twophase.Tools;

public class CubeExplorer {

    private String cubeString;
    private String[] cubeSides;
    private final int maxDepth = 21;
    private final int timeout = 5;

    public CubeExplorer() {
        cubeString = "";
        this.cubeSides = new String[6];
    }

    public String[] getCubeSides() {
        return cubeSides;
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
        int verifyResult = Tools.verify(cubeString);
        if (verifyResult == 0) {
            return Search.solution(cubeString, maxDepth, timeout, true);
        } else {
            return null;
        }
    }

    public void clear() {
        cubeString = "";
        cubeSides = new String[6];
    }
}