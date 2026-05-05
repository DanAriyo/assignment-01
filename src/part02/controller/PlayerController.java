package part02.controller;

import part01.model.Board1;
import part02.model.Board2;
import util.BoundedBuffer;
import util.BoundedBufferImpl;
import util.Role;
import util.commands.Cmd;

import java.util.Optional;

public class PlayerController {

    private BoundedBuffer<Cmd> cmdBuffer;
    private final Board2 board2;

    public PlayerController(Board2 board2, BoundedBuffer<Cmd> buffer){

        this.board2 = board2;
        cmdBuffer = buffer;
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
