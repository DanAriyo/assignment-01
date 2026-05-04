package part02.model;

import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

public class Referee {
    private boolean gameOver;
    private final ReentrantLock lock;
    private Role winner;

    public Referee(){
        this.gameOver = false;
        this.lock = new ReentrantLock();
        this.winner = null;
    }

    public void setGameOver(Role winner) {
        lock.lock();
        try {
            this.gameOver = true;
            this.setWinner(winner);
        } finally {
            lock.unlock();
        }
    }

    public boolean isGameOver() {
        lock.lock();
        try {
            return gameOver;
        } finally {
            lock.unlock();
        }
    }

    private void setWinner(Role winner){
        this.winner = winner;
    }

    public Optional<Role> getWinner(){
        return Optional.ofNullable(this.winner);
    }
}