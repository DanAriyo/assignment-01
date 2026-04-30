package controller;

import controller.commands.Cmd;
import model.Board;

public class BallsController extends Thread{

    private Board board;

    public BallsController(Board board){
        this.board = board;
    }

    public void run(){
        System.out.println("Balls Controller thread started.");
        while (!Thread.currentThread().isInterrupted()) {
            try {
                board.handleBallsCollision();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
