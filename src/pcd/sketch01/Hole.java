package pcd.sketch01;

public class Hole {

    private final P2d pos;
    private final double radius;

    public Hole(P2d pos, double radius){
        this.pos = pos;
        this.radius = radius;
    }

    public static boolean checkCollision(Ball ball, Hole hole){
        /* check if there is a collision */

        /* compute dv = b.pos - a.pos vector */

        double dx   = ball.getPos().x() - hole.pos.x();
        double dy   = ball.getPos().y() - hole.pos.y();
        double dist = Math.hypot(dx, dy);
        double minD = hole.radius + ball.getRadius();

        /*if there is collision we need to do 2 things:
        1) update the score if it was thrown either from cpu or player
        2) remove the ball from the list
        There is a collision if the distance between the two balls is less than the sum of the radii
         */
        return dist < minD && dist > 1e-6;

    }

    public P2d getPos(){
        return pos;
    }

    public double getRadius() {
        return radius;
    }



}
