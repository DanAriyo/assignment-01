package pcd.sketch01.model;

import pcd.sketch01.model.boardConf.BoardConf;
import pcd.sketch01.util.Pair;

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

    public Board(){
        this.mutex = new ReentrantLock();
    }
    
    public void init(BoardConf conf) {
    	balls = conf.getSmallBalls();    	
    	playerBall = conf.getPlayerBall(); 
    	bounds = conf.getBoardBoundary();
        holes = conf.getHoles();
        botBall = conf.getBotBall();
        scores.put(Role.PLAYER, 0);
        scores.put(Role.BOT, 0);
    }

    private boolean isStriker(Ball ball) {
        return ball.getRole() == Role.PLAYER || ball.getRole() == Role.BOT;
    }

    private void updateLastHitter(Ball a, Ball b) {

        if (isStriker(a) && b.getRole() == Role.GENERIC) {
            b.setLastHitter(a.getRole());
        }
        else if (isStriker(b) && a.getRole() == Role.GENERIC) {
            a.setLastHitter(b.getRole());
        }
        else if (a.getRole() == Role.GENERIC && b.getRole() == Role.GENERIC) {
            a.resetLastHitter();
            b.resetLastHitter();
        }
    }

    /**
     *
     * Resolving collision between 2 balls, updating their position and velocity
     *
     * @param a
     * @param b
     */
    public void resolveCollision(Ball a, Ball b) {

        updateLastHitter(a, b);

        /* check if there is a collision */

        /* compute dv = b.pos - a.pos vector */

        double dx   = b.getPos().x() - a.getPos().x();
        double dy   = b.getPos().y() - a.getPos().y();
        double dist = Math.hypot(dx, dy);
        double minD = a.getRadius() + b.getRadius();

        /*
         * There is a collision if the distance between the two balls is less than the sum of the radii
         *
         */
        if (dist < minD && dist > 1e-6)  {

            /*
             * Collision case - what to do:
             *
             * 1) solve overlaps, moving balls
             * 2) update velocities
             *
             */

            /* dvn = V2d(nx,ny) = dv unit vector */

            double nx = dx / dist;
            double ny = dy / dist;

            /*
             *
             * Update positions to solve overlaps, moving balls along dvn
             * - the displacements is proportional to the mass
             *
             */
            double overlap = minD - dist;
            double totalM  = a.getMass() + b.getMass();

            double a_factor = overlap * (b.getMass() / totalM);
            double a_deltax = nx * a_factor;
            double a_deltay = ny * a_factor;

            a.setPos(new P2d(a.getPos().x() - a_deltax, a.getPos().y() - a_deltay));

            double b_factor = overlap * (a.getMass() / totalM);
            double b_deltax = nx * b_factor;
            double b_deltay = ny * b_factor;

            b.setPos(new P2d(b.getPos().x() + b_deltax, b.getPos().y() + b_deltay));

            /* Update velocities  */

            /* relative speed along the normal vector*/

            double dvx = b.getVel().x() - a.getVel().x();
            double dvy = b.getVel().y() - a.getVel().y();
            double dvn = dvx * nx + dvy * ny;

            if (dvn <= 0) { /* if not already separating, update velocities */

                double imp = -(1 + RESTITUTION_FACTOR) * dvn / (1.0/a.getMass() + 1.0/b.getMass());
                a.setVel(new V2d(a.getVel().x() - (imp / a.getMass()) * nx, a.getVel().y() - (imp / a.getMass()) * ny));
                b.setVel(new V2d(b.getVel().x() + (imp / b.getMass()) * nx, b.getVel().y() + (imp / b.getMass()) * ny));
            }
        }
    }
    
    public void updateState(long dt) {

    	playerBall.updateState(dt, this);
        botBall.updateState(dt,this);

    	for (var b: balls) {
    		b.updateState(dt, this);
    	}

    	for (int i = 0; i < balls.size() - 1; i++) {
            for (int j = i + 1; j < balls.size(); j++) {
                resolveCollision(balls.get(i), balls.get(j));
            }
        }
    	for (var b: balls) {
    		resolveCollision(playerBall, b);
            resolveCollision(botBall,b);
    	}
        balls.removeIf(b -> {
            if (Hole.checkCollision(b, holes.x()) || Hole.checkCollision(b, holes.y())) {
                b.getLastHitter().ifPresent(this::incrementScore);
                System.out.println("collided ball" + b + " and hole");
                this.incrementScore(b.getLastHitter().get());
                System.out.println("nuovo score:" + this.getScore(b.getLastHitter().get()));
                return true;
            }
            return false;
        });

        if(Hole.checkCollision(playerBall,holes.getX()) || Hole.checkCollision(playerBall,holes.getY())){
            System.out.println("GAME SHOULD END");
        }
        if(Hole.checkCollision(botBall,holes.getX()) || Hole.checkCollision(botBall,holes.getY())){
            System.out.println("GAME SHOULD END");
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
        if(this.playerBall.getVel().abs() < 0.05){
            this.playerBall.kick(vel);
        }
        System.out.println("velocita: "+ vel.toString());
    }


}
