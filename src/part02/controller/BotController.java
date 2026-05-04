package part02.controller;

import part01.model.Board1;
import util.V2d;

import java.util.Random;

public class BotController extends Thread{

    private Board1 board1;

    public BotController(Board1 board1){
        this.board1 = board1;

    }

    public void run(){
        System.out.println("Bot Controller thread started.");
        long t0 = System.currentTimeMillis();
        var ball = board1.getBotBall();
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
                board1.handleBotCollision();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


}
