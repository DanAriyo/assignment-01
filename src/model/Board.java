package model;

import model.boardConf.BoardConf;
import util.Pair;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Board {

    private List<Ball> balls;
    private Ball playerBall;
    private Ball botBall;
    private Boundary bounds;
    private Pair<Hole, Hole> holes;
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
                if (playerBall.getVel().abs() > 1e-3){
                    handler.resolveCollision(playerBall, b);
                }
                if(botBall.getVel().abs() > 1e-3){
                    handler.resolveCollision(botBall,b);
                }
            }

            balls.removeIf(b -> {
                if (handler.checkCollision(b, holes.x()) || handler.checkCollision(b, holes.y())) {
                    b.getLastHitter().ifPresent(this::incrementScore);
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
        try{
            this.mutex.lock();
            return balls;
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
    
    public Boundary getBounds(){
        try{
            this.mutex.lock();
            return bounds;
        }finally{
            this.mutex.unlock();
        }
    }

    public Pair<Hole, Hole> getHoles() {
        try{
            this.mutex.lock();
            return holes;
        }finally{
            this.mutex.unlock();
        }
    }

    public void incrementScore(Role hitter) {
        try{
            this.mutex.lock();
            scores.computeIfPresent(hitter, (k, v) -> v + 1);
            System.out.println("hitter:" + hitter);
        }finally {
            this.mutex.unlock();
        }
    }

    public int getScore(Role type) {
        try{
            this.mutex.lock();
            return scores.getOrDefault(type, 0);
        }finally {
            this.mutex.unlock();
        }
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
        try{
            this.mutex.lock();
            return this.referee.isGameOver();
        }finally{
            this.mutex.unlock();
        }
    }

    public Optional<Role> getWinner(){
        try{
            this.mutex.lock();
            return this.referee.getWinner();
        }finally{
            this.mutex.unlock();
        }
    }




}
