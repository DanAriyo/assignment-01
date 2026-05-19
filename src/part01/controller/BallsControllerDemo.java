package part01.controller;

import part01.model.Board1;
import part01.model.Board1Demo;

// Estendiamo Thread per consentire a JPF di simulare la concorrenza
public class BallsControllerDemo extends Thread {

    private final Board1Demo board1;

    public BallsControllerDemo(Board1Demo board1) {
        this.board1 = board1;
    }

    @Override
    public void run() {
        try {
            board1.handleBallsCollision();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}