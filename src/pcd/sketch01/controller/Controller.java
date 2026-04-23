package pcd.sketch01.controller;

import pcd.sketch01.Board;
import pcd.sketch01.controller.Cmd;
import pcd.sketch02.util.BoundedBuffer;
import pcd.sketch02.util.BoundedBufferImpl;

import static java.rmi.server.LogStream.log;

public class Controller extends Thread{

    private BoundedBuffer<Cmd> cmdBuffer;
    private final Board board;
    private static int MAX_SIZE = 1;

    public Controller(Board board){

        this.board = board;
        cmdBuffer = new BoundedBufferImpl<>(MAX_SIZE);
    }

    public void run() {
        log("started.");
        while (true) {
            try {
                log("Waiting for cmds ");
                var cmd = cmdBuffer.get();
                log("new cmd fetched: " + cmd);
                //cmd.execute(counter);
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
