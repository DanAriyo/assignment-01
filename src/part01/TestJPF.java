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

        // Simuliamo un numero limitato di frame (es. 2 o 3)
        // JPF esplorerà tutti gli incroci possibili in questi frame.
        for (int i = 0; i < 2; i++) {
            System.out.println("--- Inizio Frame " + i + " ---");

            // Il main thread agisce da "Master" chiamando updateState
            // che contiene la logica di signalAll() e await()
            board1.updateState(20);

            System.out.println("--- Fine Frame " + i + " ---");
        }

        // Importante: dovresti avere un metodo per fermare i thread pulitamente
        // altrimenti JPF continuerà l'analisi all'infinito.
        // Se non lo hai, possiamo forzare l'uscita (non elegantissimo ma efficace per JPF):
        System.exit(0);
    }
}