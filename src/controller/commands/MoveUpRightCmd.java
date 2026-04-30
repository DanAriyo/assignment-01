package controller.commands;

import model.Board;
import model.V2d;

public class MoveUpRightCmd implements Cmd {
    @Override
    public void execute(Board board) {
        board.applyImpulseToPlayerBall(new V2d(1,1));
    }
}
