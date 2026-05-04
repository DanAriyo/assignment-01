package part02;

import part01.controller.BallsController;
import part01.controller.BotController;
import part01.controller.PlayerController;
import part01.model.Board1;
import part01.view.View;
import part01.view.ViewModel;
import util.boardConf.MassiveBoardConf;


public class App2 {

    public static void main(String[] args) {

        /*
         * Different board configs to try:
         * - minimal: 2 small balls
         * - large: 400 small balls
         * - massive: 4500 small balls
         */

        //var boardConf = new MinimalBoardConf();
        //var boardConf = new LargeBoardConf();
        var boardConf = new MassiveBoardConf();

        Board1 board1 = new Board1();
        board1.init(boardConf);
        var controller = new PlayerController(board1);
        var botController = new BotController(board1);
        var ballsController = new BallsController(board1);

        ViewModel viewModel = new ViewModel();
        View view = new View(viewModel, 1200, 800, controller);
        controller.start();
        botController.start();
        ballsController.start();

        viewModel.update(board1, 0);
        view.render();
        waitAbit();

        int nFrames = 0;
        long t0 = System.currentTimeMillis();
        long lastUpdateTime = System.currentTimeMillis();

        /* main simulation loop */

        while (true) {

            /* update board state */

            long elapsed = System.currentTimeMillis() - lastUpdateTime;
            lastUpdateTime = System.currentTimeMillis();
            board1.updateState(elapsed);

            /* render */

            nFrames++;
            int framePerSec = 0;
            long dt = (System.currentTimeMillis() - t0);
            if (dt > 0) {
                framePerSec = (int) (nFrames * 1000 / dt);
            }

            viewModel.update(board1, framePerSec);
            view.render();

        }
    }

    private static void waitAbit() {
        try {
            Thread.sleep(2000);
        } catch (Exception ex) {
        }
    }

}