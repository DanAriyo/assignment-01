package common;

import util.P2d;
import util.Role;
import util.V2d;

import static common.Ball.RESTITUTION_FACTOR;

public class CollisionHandler {

    public CollisionHandler(){

    }

    public boolean checkCollision(Ball ball, Hole hole){
        /* check if there is a collision */

        /* compute dv = b.pos - a.pos vector */

        double dx   = ball.getPos().getX() - hole.getPos().getX();
        double dy   = ball.getPos().getY() - hole.getPos().getY();
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
        // 1. Definiamo l'ordine di acquisizione basato sull'ID per evitare deadlock
        Ball first = a.getId() < b.getId() ? a : b;
        Ball second = a.getId() < b.getId() ? b : a;

        // 2. Acquisiamo i lock in sequenza
        first.lock();
        try {
            second.lock();
            try {
                // 3. Controllo velocità: se entrambe sono ferme, saltiamo il calcolo
                if (a.getVel().abs() < 1e-3 && b.getVel().abs() < 1e-3) {
                    return;
                }

                // 4. Eseguiamo la logica mentre abbiamo il controllo esclusivo di entrambe
                updateLastHitter(a, b);

                double dx = b.getPos().getX() - a.getPos().getX();
                double dy = b.getPos().getY() - a.getPos().getY();
                double dist = Math.hypot(dx, dy);
                double minD = a.getRadius() + b.getRadius();

                if (dist < minD && dist > 1e-6) {
                    double nx = dx / dist;
                    double ny = dy / dist;

                    double overlap = minD - dist;
                    double totalM = a.getMass() + b.getMass();

                    // Aggiornamento posizioni (il Monitor gestisce la thread-safety interna)
                    double a_factor = overlap * (b.getMass() / totalM);
                    a.setPos(new P2d(a.getPos().getX() - nx * a_factor, a.getPos().getY() - ny * a_factor));

                    double b_factor = overlap * (a.getMass() / totalM);
                    b.setPos(new P2d(b.getPos().getX() + nx * b_factor, b.getPos().getY() + ny * b_factor));

                    // Aggiornamento velocità
                    double dvx = b.getVel().getX() - a.getVel().getX();
                    double dvy = b.getVel().getY() - a.getVel().getY();
                    double dvn = dvx * nx + dvy * ny;

                    if (dvn <= 0) {
                        double imp = -(1 + RESTITUTION_FACTOR) * dvn / (1.0 / a.getMass() + 1.0 / b.getMass());
                        a.setVel(new V2d(a.getVel().getX() - (imp / a.getMass()) * nx, a.getVel().getY() - (imp / a.getMass()) * ny));
                        b.setVel(new V2d(b.getVel().getX() + (imp / b.getMass()) * nx, b.getVel().getY() + (imp / b.getMass()) * ny));
                    }
                }
            } finally {
                second.unlock();
            }
        } finally {
            first.unlock();
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

    }


}
