package pcd.sketch01.model;

import static pcd.sketch01.model.Ball.RESTITUTION_FACTOR;

public class CollisionHandler {

    public CollisionHandler(){

    }

    public boolean checkCollision(Ball ball, Hole hole){
        /* check if there is a collision */

        /* compute dv = b.pos - a.pos vector */

        double dx   = ball.getPos().x() - hole.getPos().x();
        double dy   = ball.getPos().y() - hole.getPos().y();
        double dist = Math.hypot(dx, dy);
        double minD = hole.getRadius() + ball.getRadius();

        /*if there is collision we need to do 2 things:
        1) update the score if it was thrown either from cpu or player
        2) remove the ball from the list
        There is a collision if the distance between the two balls is less than the sum of the radius
         */
        return dist < minD && dist > 1e-6;

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


}
