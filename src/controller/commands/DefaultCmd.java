package controller.commands;

import model.Board;
import model.V2d;

public class DefaultCmd implements Cmd {
    @Override
    public void execute(Board board) {
        board.applyImpulseToPlayerBall(new V2d(0,0));
    }
}
