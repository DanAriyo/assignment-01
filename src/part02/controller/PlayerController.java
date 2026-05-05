package part02.controller;

import part01.model.Board1;
import part02.model.Board2;
import util.BoundedBuffer;
import util.BoundedBufferImpl;
import util.Role;
import util.commands.Cmd;

import java.util.Optional;

public class PlayerController  extends Thread{

    private BoundedBuffer<Cmd> cmdBuffer;
    private final Board2 board2;
    private static int MAX_SIZE = 1;

    public PlayerController(Board2 board2){
        this.cmdBuffer = new BoundedBufferImpl<>(MAX_SIZE);
        this.board2 = board2;
    }

    public void run() {
        System.out.println("Player Controller thread started.");
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Optional<Cmd> cmd = cmdBuffer.poll();
                cmd.ifPresent(c -> c.execute(board2));
                board2.handlePlayerCollision();

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

    public boolean isGameOver(){
        return board2.isGameOver();
    }

    public Optional<Role> getWinner(){return this.board2.getWinner();}


}
