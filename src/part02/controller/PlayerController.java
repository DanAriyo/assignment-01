package part02.controller;

import part01.model.Board;
import part01.model.Role;
import util.BoundedBuffer;
import util.BoundedBufferImpl;
import util.commands.Cmd;

import java.util.Optional;

public class PlayerController extends Thread{

    private BoundedBuffer<Cmd> cmdBuffer;
    private final Board board;
    private static int MAX_SIZE = 1;

    public PlayerController(Board board){

        this.board = board;
        cmdBuffer = new BoundedBufferImpl<>(MAX_SIZE);
    }

    public void run() {
        System.out.println("Player Controller thread started.");
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Optional<Cmd> cmd = cmdBuffer.poll();
                cmd.ifPresent(c -> c.execute(board));
                board.handlePlayerCollision();

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
        return board.isGameOver();
    }

    public Optional<Role> getWinner(){return this.board.getWinner();}


}
