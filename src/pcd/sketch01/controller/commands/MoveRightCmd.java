package pcd.sketch01.controller.commands;

import pcd.sketch01.Board;
import pcd.sketch01.V2d;

public class MoveRightCmd implements Cmd {
    @Override
    public void execute(Board board) {
        board.applyImpulseToPlayerBall(new V2d(1,0));
    }
}
