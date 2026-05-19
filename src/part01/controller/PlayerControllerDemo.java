package part01.controller;

import part01.model.Board1;
import part01.model.Board1Demo;
import util.commands.Cmd;
import util.commands.MoveUpCmd;
import java.util.Optional;

// Deve estendere Thread per permettere l'interleaving a JPF
public class PlayerControllerDemo extends Thread {
    private final Board1Demo board1;

    public PlayerControllerDemo(Board1Demo board1){
        this.board1 = board1;
    }

    @Override
    public void run() {
        try {
            board1.handlePlayerCollision();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}