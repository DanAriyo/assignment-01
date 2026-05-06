package part02.model;

import common.*;
import util.*;
import common.boardConf.BoardConf;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Board2 implements Board {

    private List<Ball> balls;
    private Ball playerBall;
    private Ball botBall;
    private Boundary bounds;
    private Pair<Hole, Hole> holes;
    private final Map<Role, Integer> scores = new EnumMap<>(Role.class);
    private final Lock mutex;
    private final CollisionHandler handler;
    private Referee referee;
    private final Condition startCollisions;
    private final Condition collisionsDone;
    private int finishedCount = 0;
    private boolean canStart = false;
    private List<Boundary> zones = List.of();
    private final ExecutorService executor;
    private final double margin = 0.01;


    public Board2(){
        this.mutex = new ReentrantLock();
        this.handler = new CollisionHandler();
        int nThreads = Runtime.getRuntime().availableProcessors();
        executor = Executors.newFixedThreadPool(nThreads);
        startCollisions = mutex.newCondition();
        collisionsDone = mutex.newCondition();

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
            this.zones = createQuadrants(this.getBounds(),16,8);
        }finally {
            this.mutex.unlock();
        }
    }

    public void updateState(long dt) {
        try {
            this.mutex.lock();
            playerBall.updateState(dt, this);
            botBall.updateState(dt, this);
            for (Ball b : balls) b.updateState(dt, this);

            canStart = true;
            finishedCount = 0;
            startCollisions.signalAll(); // Sveglia Player e Bot
        } finally {
            this.mutex.unlock();
        }

        int numZones = zones.size();
        List<List<Ball>> zoneLists = new ArrayList<>(numZones);
        for (int i = 0; i < numZones; i++) {
            zoneLists.add(new ArrayList<>());
        }

        for (Ball b : balls) {
            P2d pos = b.getPos();
            for (int i = 0; i < numZones; i++) {
                if (zones.get(i).contains(pos, margin)) {
                    zoneLists.get(i).add(b);
                }
            }
        }


        List<Callable<Void>> tasks = new ArrayList<>();
        for (int i = 0; i < numZones; i++) {
            Boundary currentZone = zones.get(i);
            Ball pInZone = currentZone.contains(playerBall.getPos(), margin) ? playerBall : null;
            Ball bInZone = currentZone.contains(botBall.getPos(), margin) ? botBall : null;

            tasks.add(new ZoneTask(zoneLists.get(i), pInZone, bInZone, this.handler));
        }

        try {
            executor.invokeAll(tasks);

            mutex.lock();
            try {
                while (finishedCount < 2) collisionsDone.await();
                balls.removeIf(b -> {
                    if (handler.checkCollision(b, holes.x()) || handler.checkCollision(b, holes.y())) {
                        b.getLastHitter().ifPresent(this::incrementScore);
                        return true;
                    }
                    return false;
                });

                if(balls.isEmpty()){
                    this.checkEndGameConditions();
                }
                if(handler.checkCollision(playerBall,holes.getX()) || handler.checkCollision(playerBall,holes.getY())){
                    this.referee.setGameOver(this.botBall.getRole());
                }
                if(handler.checkCollision(botBall,holes.getX()) || handler.checkCollision(botBall,holes.getY())){
                    this.referee.setGameOver(this.playerBall.getRole());
                }
                canStart = false;
            } finally {
                mutex.unlock();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
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

        try {
            mutex.lock();
            finishedCount++;
            if (finishedCount == 2) {
                collisionsDone.signalAll();
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

        try {
            mutex.lock();
            finishedCount++;
            if (finishedCount == 2) {
                collisionsDone.signalAll();
            }
        } finally {
            mutex.unlock();
        }

    }

    private List<Boundary> createQuadrants(Boundary bounds, int cols, int rows) {
        double x0 = bounds.getX0();
        double y0 = bounds.getY0();
        double x1 = bounds.getX1();
        double y1 = bounds.getY1();

        double zoneWidth = (x1 - x0) / cols;
        double zoneHeight = (y1 - y0) / rows;

        List<Boundary> zones = new ArrayList<>();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double curX0 = x0 + j * zoneWidth;
                double curY0 = y0 + i * zoneHeight;
                double curX1 = curX0 + zoneWidth;
                double curY1 = curY0 + zoneHeight;
                zones.add(new Boundary(curX0, curY0, curX1, curY1));
            }
        }
        return zones;
    }


}
