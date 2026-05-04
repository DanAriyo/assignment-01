package util.commands;

import part01.model.Board1;
import util.V2d;

public class MoveUpCmd implements Cmd {
    @Override
    public void execute(Board1 board1) {
        board1.applyImpulseToPlayerBall(new V2d(0,1));
    }
}
