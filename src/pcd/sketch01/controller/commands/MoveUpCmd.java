package pcd.sketch01.controller.commands;

import pcd.sketch01.model.Board;
import pcd.sketch01.model.V2d;

public class MoveUpCmd implements Cmd {
    @Override
    public void execute(Board board) {
        board.applyImpulseToPlayerBall(new V2d(0,1));
    }
}
