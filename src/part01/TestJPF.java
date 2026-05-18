package part01;

import common.boardConf.BoardConf;
import common.boardConf.MinimalBoardConf;
import part01.controller.BallsController;
import part01.controller.BotController;
import part01.controller.PlayerController;
import part01.model.Board1;

public class TestJPF {

    public static void main(String[] args) {
        // Usiamo una configurazione minimale: 2 palline sono sufficienti
        // per testare se i thread collidono o si aspettano correttamente.
        BoardConf boardConf = new MinimalBoardConf();

        Board1 board1 = new Board1();
        board1.init(boardConf);

        // Inizializziamo i controller (i tuoi thread Worker)
        PlayerController playerThread = new PlayerController(board1);
        BotController botThread = new BotController(board1);
        BallsController ballsThread = new BallsController(board1);

        // Facciamo partire i thread
        playerThread.start();
        botThread.start();
        ballsThread.start();

        board1.updateState(20);

        // Importante: dovresti avere un metodo per fermare i thread pulitamente
        // altrimenti JPF continuerà l'analisi all'infinito.
        // Se non lo hai, possiamo forzare l'uscita (non elegantissimo ma efficace per JPF):
        System.exit(0);
    }
}