package controller;

import controller.commands.Cmd;
import model.Board;
import model.Role;
import util.BoundedBuffer;
import util.BoundedBufferImpl;

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
                Cmd cmd = cmdBuffer.get();
                System.out.println("Executing command: " + cmd.getClass().getSimpleName());
                cmd.execute(board);
                board.handlePlayerCollision();

            } catch (InterruptedException ex) {
                System.out.println("Player Controller thread interrupted, shutting down...");
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

    public boolean isGameOver(){
        return board.isGameOver();
    }

    public Optional<Role> getWinner(){return this.board.getWinner();}
}
