package pcd.sketch01;

import pcd.sketch01.util.Pair;

import java.util.*;

public class Board {

    private List<Ball> balls;    
    private Ball playerBall;
    private Boundary bounds;
    private Pair<Hole,Hole> holes;
    private final Map<Role, Integer> scores = new EnumMap<>(Role.class);

    public Board(){} 
    
    public void init(BoardConf conf) {
    	balls = conf.getSmallBalls();    	
    	playerBall = conf.getPlayerBall(); 
    	bounds = conf.getBoardBoundary();
        holes = conf.getHoles();
        scores.put(Role.PLAYER, 0);
        scores.put(Role.BOT, 0);
    }
    
    public void updateState(long dt) {

    	playerBall.updateState(dt, this);
    	
    	for (var b: balls) {
    		b.updateState(dt, this);
    	}       	
    	
    	for (int i = 0; i < balls.size() - 1; i++) {
            for (int j = i + 1; j < balls.size(); j++) {
                Ball.resolveCollision(balls.get(i), balls.get(j));
            }
        }
    	for (var b: balls) {
    		Ball.resolveCollision(playerBall, b);
    	}
        balls.removeIf(b -> {
            if (Hole.checkCollision(b, holes.x()) || Hole.checkCollision(b, holes.y())) {
                b.getLastHitter().ifPresent(this::incrementScore);
                return true;
            }
            return false;
        });
    	   	    	
    }
    
    public List<Ball> getBalls(){
    	return balls;
    }
    
    public Ball getPlayerBall() {
    	return playerBall;
    }
    
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
        if(this.playerBall.getVel().abs() < 0.05){
            this.playerBall.kick(vel);
        }
        System.out.println("velocita: "+ vel.toString());
    }


}
