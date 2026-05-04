package part01.controller;

import part01.model.Board;
import util.V2d;

import java.util.Random;

public class BotController extends Thread{

    private Board board;

    public BotController(Board board){
        this.board = board;

    }

    public void run(){
        System.out.println("Bot Controller thread started.");
        long t0 = System.currentTimeMillis();
        var ball = board.getBotBall();
        var rand = new Random(2);
        var lastKickTime = t0;
        while (!Thread.currentThread().isInterrupted()) {
            try {
                if (ball.getVel().abs() < 0.05 && System.currentTimeMillis() - lastKickTime > 2000) {
                    var angle = rand.nextDouble() * Math.PI * 0.25;
                    var v = new V2d(Math.cos(angle), Math.sin(angle)).mul(1.5);
                    ball.kick(v);
                    lastKickTime = System.currentTimeMillis();
                }
                board.handleBotCollision();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


}
