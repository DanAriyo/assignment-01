package part01.model;

import common.*;
import util.Boundary;
import util.Role;
import util.V2d;
import common.boardConf.BoardConf;
import util.Pair;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Board1Demo implements Board {

    private List<Ball> balls;
    private Ball playerBall;
    private Ball botBall;
    private final Lock mutex;
    private final Condition startCollisions;
    private final Condition collisionsDone;
    private int finishedCount = 0;
    private boolean canStart = false;

    public Board1Demo(BoardConf conf){
        this.mutex = new ReentrantLock();
        startCollisions = mutex.newCondition();
        collisionsDone = mutex.newCondition();
        try{
            this.mutex.lock();
            balls = conf.getSmallBalls();
            playerBall = conf.getPlayerBall();
            botBall = conf.getBotBall();
        }finally {
            this.mutex.unlock();
        }
    }


    public void updateState() {
        try{
            this.mutex.lock();
            //update delle posizioni dele palline
            canStart = true;
            finishedCount = 0;
            startCollisions.signalAll();

            while (finishedCount < 3) {
                collisionsDone.await();
            }
            canStart = false;

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally{
            this.mutex.unlock();
        }

    }



    public List<Ball> getBalls(){
        try{
            this.mutex.lock();
            return List.copyOf(this.balls);
        }finally{
            this.mutex.unlock();
        }
    }

    public Ball getPlayerBall() {
        try{
            this.mutex.lock();
            return playerBall;
        }finally{
            this.mutex.unlock();
        }
    }

    public Ball getBotBall() {
        try{
            this.mutex.lock();
            return botBall;
        }finally{
            this.mutex.unlock();
        }
    }

    public void applyImpulseToPlayerBall(V2d vel){
        try{
            this.mutex.lock();
            this.playerBall.kick(vel);
        }finally {
            this.mutex.unlock();
        }
    }

    @Override
    public Boundary getBounds() {
        return null;
    }


    public void handlePlayerCollision(){
        try {
            mutex.lock();
            while (!canStart) {
                startCollisions.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mutex.unlock();
        }

        //collisioni tra playerball e (bot + palline + buche)

        try {
            mutex.lock();
            finishedCount++;
            if (finishedCount == 3) {
                collisionsDone.signal();
            }
        } finally {
            mutex.unlock();
        }
    }

    public void handleBotCollision(){
        try {
            mutex.lock();
            while (!canStart) {
                startCollisions.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mutex.unlock();
        }

        //collisioni tra bot e ( player + palline + buche)

        try {
            mutex.lock();
            finishedCount++;
            if (finishedCount == 3) {
                collisionsDone.signal();
            }
        } finally {
            mutex.unlock();
        }

    }

    public void handleBallsCollision(){

        try {
            mutex.lock();
            while (!canStart) {
                startCollisions.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mutex.unlock();
        }

        //collisioni tra palline e buche

        try {
            mutex.lock();
            finishedCount++;
            if (finishedCount == 3) {
                collisionsDone.signal();
            }
        } finally {
            mutex.unlock();
        }
    }




}
