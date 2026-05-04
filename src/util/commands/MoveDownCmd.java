package util.commands;

import common.Board;
import part01.model.Board1;
import util.V2d;

public class MoveDownCmd implements Cmd {
    @Override
    public void execute(Board board) {
        board.applyImpulseToPlayerBall(new V2d(0,-1));
    }
}
