package util.commands;

import part01.model.Board;
import util.V2d;

public class MoveDownRightCmd implements Cmd {
    @Override
    public void execute(Board board) {
        board.applyImpulseToPlayerBall(new V2d(1,-1));
    }
}
