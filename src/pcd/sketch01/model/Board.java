package pcd.sketch01.model;

import pcd.sketch01.model.boardConf.BoardConf;
import pcd.sketch01.util.Pair;
import pcd.sketch02.model.CounterObserver;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static pcd.sketch01.model.Ball.RESTITUTION_FACTOR;

public class Board {

    private List<Ball> balls;
    private Ball playerBall;
    private Ball botBall;
    private Boundary bounds;
    private Pair<Hole,Hole> holes;
    private final Map<Role, Integer> scores = new EnumMap<>(Role.class);
    private final Lock mutex;
    private final CollisionHandler handler;
    private Referee referee;

    public Board(){
        this.mutex = new ReentrantLock();
        this.handler = new CollisionHandler();
    }
    
    public void init(BoardConf conf) {
        try{
            this.mutex.lock();
            balls = conf.getSmallBalls();
            playerBall = conf.getPlayerBall();
            bounds = conf.getBoardBoundary();
            holes = conf.getHoles();
            botBall = conf.getBotBall();
            scores.put(Role.PLAYER, 0);
            scores.put(Role.BOT, 0);
            this.referee = new Referee();
        }finally {
            this.mutex.unlock();
        }
    }

    public void updateState(long dt) {

        try{
            this.mutex.lock();
            playerBall.updateState(dt, this);
            botBall.updateState(dt,this);

            for (var b: balls) {
                b.updateState(dt, this);
            }

            for (int i = 0; i < balls.size() - 1; i++) {
                for (int j = i + 1; j < balls.size(); j++) {
                    handler.resolveCollision(balls.get(i), balls.get(j));
                }
            }

            for (var b: balls) {
                handler.resolveCollision(playerBall, b);
                handler.resolveCollision(botBall,b);
            }

            balls.removeIf(b -> {
                if (handler.checkCollision(b, holes.x()) || handler.checkCollision(b, holes.y())) {
                    b.getLastHitter().ifPresent(this::incrementScore);
                    System.out.println("collided ball" + b + " and hole");
                    this.incrementScore(b.getLastHitter().get());
                    System.out.println("nuovo score:" + this.getScore(b.getLastHitter().get()));
                    return true;
                }
                return false;
            });


            handler.resolveCollision(playerBall,botBall);

            if(handler.checkCollision(playerBall,holes.getX()) || handler.checkCollision(playerBall,holes.getY())){
                this.referee.setGameOver(this.botBall.getRole());
            }
            if(handler.checkCollision(botBall,holes.getX()) || handler.checkCollision(botBall,holes.getY())){
                this.referee.setGameOver(this.playerBall.getRole());
            }

            if(balls.isEmpty()){
                this.checkEndGameConditions();
            }
        } finally{
            this.mutex.unlock();
        }

    }

    public void checkEndGameConditions() {
        try{
            this.mutex.lock();
            int playerScore = this.scores.get(Role.PLAYER);
            int botScore = this.scores.get(Role.BOT);

            if (playerScore > botScore) {
                this.referee.setGameOver(Role.PLAYER);
            } else if (botScore > playerScore) {
                this.referee.setGameOver(Role.BOT);
            } else {
                this.referee.setGameOver(Role.GENERIC);
            }
        }finally{
            this.mutex.unlock();
        }

    }
    
    public List<Ball> getBalls(){
    	return balls;
    }
    
    public Ball getPlayerBall() {
    	return playerBall;
    }

    public Ball getBotBall() {return botBall;}
    
    public  Boundary getBounds(){
        return bounds;
    }

    public Pair<Hole, Hole> getHoles() {
        return holes;
    }

    public void incrementScore(Role hitter) {
        scores.computeIfPresent(hitter, (k, v) -> v + 1);
    }

    public int getScore(Role type) {
        return scores.getOrDefault(type, 0);
    }

    public void applyImpulseToPlayerBall(V2d vel){
        try{
            this.mutex.lock();
            if(this.playerBall.getVel().abs() < 0.05){
                this.playerBall.kick(vel);
            }
        }finally {
            this.mutex.unlock();
        }
    }

    public boolean isGameOver(){
        return this.referee.isGameOver();
    }

    public Optional<Role> getWinner(){
        return this.referee.getWinner();
    }




}
