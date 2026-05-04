package util.commands;

import part01.model.Board;
import part01.model.V2d;

public class MoveRightCmd implements Cmd {
    @Override
    public void execute(Board board) {
        board.applyImpulseToPlayerBall(new V2d(1,0));
    }
}
