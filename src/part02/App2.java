package part02;


import part02.controller.PlayerController;
import part02.model.Board2;
import part02.view.View;
import part02.view.ViewModel;
import part02.model.Board2;
import util.boardConf.LargeBoardConf;
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
        var boardConf = new LargeBoardConf();
        //var boardConf = new MassiveBoardConf();

        Board2 board2 = new Board2();
        board2.init(boardConf);
        var controller = new PlayerController(board2);

        ViewModel viewModel = new ViewModel();
        View view = new View(viewModel, 1200, 800, controller);

        viewModel.update(board2, 0);
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
            board2.updateState(elapsed);

            /* render */

            nFrames++;
            int framePerSec = 0;
            long dt = (System.currentTimeMillis() - t0);
            if (dt > 0) {
                framePerSec = (int) (nFrames * 1000 / dt);
            }

            viewModel.update(board2, framePerSec);
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