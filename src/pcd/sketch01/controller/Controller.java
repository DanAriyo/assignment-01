package pcd.sketch01.controller;

import pcd.sketch01.Board;
import pcd.sketch01.controller.commands.Cmd;
import pcd.sketch01.util.BoundedBuffer;
import pcd.sketch01.util.BoundedBufferImpl;

public class Controller extends Thread{

    private BoundedBuffer<Cmd> cmdBuffer;
    private final Board board;
    private static int MAX_SIZE = 1;

    public Controller(Board board){

        this.board = board;
        cmdBuffer = new BoundedBufferImpl<>(MAX_SIZE);
    }

    public void run() {
        System.out.println("Controller thread started.");
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Cmd cmd = cmdBuffer.get();
                System.out.println("Executing command: " + cmd.getClass().getSimpleName());
                cmd.execute(board);

            } catch (InterruptedException ex) {
                System.out.println("Controller thread interrupted, shutting down...");
                break;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void notifyNewCmd(Cmd cmd) {
        try {
            cmdBuffer.put(cmd);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
