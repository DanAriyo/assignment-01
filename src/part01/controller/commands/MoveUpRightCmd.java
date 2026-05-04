package part01.controller.commands;

import part01.model.Board;
import part01.model.V2d;

public class MoveUpRightCmd implements Cmd {
    @Override
    public void execute(Board board) {
        board.applyImpulseToPlayerBall(new V2d(1,1));
    }
}
