package common;

import part01.model.Board1;
import util.Boundary;
import util.P2d;
import util.Role;
import util.V2d;

import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;


public class Ball {

    private final int id; // Aggiunto per ordinamento lock anti-deadlock
    private P2d pos;
    private V2d vel;
    private final double radius;
    private double mass;
    private final Role role;
    private Role lastHitter = null;

    private final ReentrantLock mutex = new ReentrantLock();

    public static double FRICTION_FACTOR = 0.25;
    public static double RESTITUTION_FACTOR = 1;

    public Ball(int id, P2d pos, double radius, double mass, V2d vel, Role role){
        this.id = id;
        this.pos = pos;
        this.radius = radius;
        this.mass = mass;
        this.vel = vel;
        this.role = role;
    }

    /* Metodi del Monitor con gestione della mutua esclusione */

    public void setLastHitter(Role hitter) {
        mutex.lock();
        try {
            lastHitter = hitter;
        } finally {
            mutex.unlock();
        }
    }

    public void updateState(long dt, Board ctx){
        mutex.lock();
        try {
            double speed = vel.abs();
            double dt_scaled = dt*0.001;
            if (speed > 0.001) {
                double dec    = FRICTION_FACTOR * dt_scaled;
                double factor = Math.max(0, speed - dec) / speed;
                vel = vel.mul(factor);
            } else {
                vel = new V2d(0,0);
            }
            pos = pos.sum(vel.mul(dt_scaled));
            applyBoundaryConstraints(ctx);
        } finally {
            mutex.unlock();
        }
    }

    public void kick(V2d vel) {
        mutex.lock();
        try {
            this.vel = vel;
        } finally {
            mutex.unlock();
        }
    }

    private void applyBoundaryConstraints(Board ctx){
        Boundary bounds = ctx.getBounds();
        if (pos.x() + radius > bounds.x1()){
            pos = new P2d(bounds.x1() - radius, pos.y());
            vel = vel.getSwappedX();
        } else if (pos.x() - radius < bounds.x0()){
            pos = new P2d(bounds.x0() + radius, pos.y());
            vel = vel.getSwappedX();
        } else if (pos.y() + radius > bounds.y1()){
            pos = new P2d(pos.x(), bounds.y1() - radius);
            vel = vel.getSwappedY();
        } else if (pos.y() - radius < bounds.y0()){
            pos = new P2d(pos.x(), bounds.y0() + radius);
            vel = vel.getSwappedY();
        }
    }

    public P2d getPos(){
        mutex.lock();
        try {
            return pos;
        } finally {
            mutex.unlock();
        }
    }

    public void setPos(P2d pos){
        mutex.lock();
        try {
            this.pos = pos;
        } finally {
            mutex.unlock();
        }
    }

    public V2d getVel() {
        mutex.lock();
        try {
            return vel;
        } finally {
            mutex.unlock();
        }
    }

    public void setVel(V2d vel){
        mutex.lock();
        try {
            this.vel = vel;
        } finally {
            mutex.unlock();
        }
    }

    public Optional<Role> getLastHitter() {
        mutex.lock();
        try {
            return Optional.ofNullable(lastHitter);
        } finally {
            mutex.unlock();
        }
    }

    public void resetLastHitter() {
        mutex.lock();
        try {
            this.lastHitter = null;
        } finally {
            mutex.unlock();
        }
    }



    public void lock() {
        mutex.lock();
    }

    public void unlock() {
        mutex.unlock();
    }

    public int getId() {
        return id;
    }


    public double getMass() {
        return mass;
    }

    public double getRadius() {
        return radius;
    }

    public Role getRole(){
        return this.role;
    }
}