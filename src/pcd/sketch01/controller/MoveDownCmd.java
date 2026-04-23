package pcd.sketch01.controller;

import pcd.sketch01.Board;
import pcd.sketch01.V2d;

public class MoveDownCmd implements Cmd{
    @Override
    public void execute(Board board) {
        board.applyImpulseToPlayerBall(new V2d(0,-1));
    }
}
