package part02.controller;

import part01.model.Board1;

public class BallsController extends Thread{

    private Board1 board1;

    public BallsController(Board1 board1){
        this.board1 = board1;
    }

    public void run(){
        System.out.println("Balls Controller thread started.");
        while (!Thread.currentThread().isInterrupted()) {
            try {
                board1.handleBallsCollision();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
