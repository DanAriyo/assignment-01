package part01.controller;

import part01.model.Board1;
import part01.model.Board1Demo;
import util.V2d;

// Estendiamo Thread per permettere a JPF di simulare l'interleaving dei thread
public class BotControllerDemo extends Thread {

    private final Board1Demo board1;

    public BotControllerDemo(Board1Demo board1){
        this.board1 = board1;
    }

    @Override
    public void run(){
        try {
            board1.handleBotCollision();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}