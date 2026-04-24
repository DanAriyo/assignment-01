package pcd.sketch01.model;

import java.util.Optional;

public class Ball {
    
    private P2d pos;
    private V2d vel;
    private final double radius;
    private double mass;
    private final Role role;
    private Role lastHitter = null;
    
    public static double FRICTION_FACTOR = 0.25; 	/* 0 minimum */
    public static double RESTITUTION_FACTOR = 1;

    public Ball(P2d pos, double radius, double mass, V2d vel,Role role){
       this.pos = pos;
       this.radius = radius;
       this.mass = mass;
       this.vel = vel;
       this.role = role;
    }

    public void setLastHitter(Role hitter) {
        lastHitter = hitter;
    }

    public void updateState(long dt, Board ctx){
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
    }

    public void kick(V2d vel) {
    	this.vel = vel;
    }

    /**
     * 
     * Keep the ball inside the boundaries, updating the velocity in the case of bounces
     * 
     * @param ctx
     */
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
    	return pos;
    }

    public void setPos(P2d pos){
        this.pos = pos;
    }
    
    public double getMass() {
    	return mass;
    }

    public void setMass(double mass){
        this.mass = mass;
    }
    
    public V2d getVel() {
    	return vel;
    }

    public void setVel(V2d vel){ this.vel = vel; }
    
    public double getRadius() {
    	return radius;
    }

    public Role getRole(){
        return this.role;
    }

    public Optional<Role> getLastHitter() {
        return Optional.ofNullable(lastHitter);
    }

    public void resetLastHitter() {
        this.lastHitter = null;
    }

}
