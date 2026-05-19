package part01;

import common.boardConf.BoardConf;
import common.boardConf.BoardConfDemo;
import common.boardConf.MinimalBoardConf;
import part01.controller.*;
import part01.model.Board1;
import part01.model.Board1Demo;

public class TestJPF {

    public static void main(String[] args) {
        // Usiamo una configurazione minimale: 2 palline sono sufficienti
        // per testare se i thread collidono o si aspettano correttamente.
        BoardConf boardConfDemo = new BoardConfDemo();

        Board1Demo board1Demo = new Board1Demo(boardConfDemo);

        // Inizializziamo i controller (i tuoi thread Worker)
        PlayerControllerDemo playerThread = new PlayerControllerDemo(board1Demo);
        BotControllerDemo botThread = new BotControllerDemo(board1Demo);
        BallsControllerDemo ballsThread = new BallsControllerDemo(board1Demo);

        // Facciamo partire i thread
        playerThread.start();
        botThread.start();
        ballsThread.start();

        board1Demo.updateState();
        System.exit(0);
    }
}