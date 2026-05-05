package part02.model;

import common.Ball;
import common.CollisionHandler;
import util.Boundary;

import java.util.List;
import java.util.concurrent.Callable;

public class ZoneTask implements Callable<Void> {

    private final Boundary zoneBoundary;
    private final List<Ball> ballsInZone;
    private final CollisionHandler handler;

    public ZoneTask(Boundary boundary, List<Ball> ballsInZone, CollisionHandler handler) {
        this.zoneBoundary = boundary;
        this.ballsInZone = ballsInZone;
        this.handler = handler;
    }

    @Override
    public Void call() throws Exception {
        int size = ballsInZone.size();

        // Algoritmo di collisione spaziale O(N^2) limitato alla zona
        for (int i = 0; i < size; i++) {
            Ball b1 = ballsInZone.get(i);

            for (int j = i + 1; j < size; j++) {
                Ball b2 = ballsInZone.get(j);

                // Risoluzione della collisione
                // Il CollisionHandler deve gestire internamente il locking ordinato
                // per evitare deadlock sulle palline nel "margin"
                handler.resolveCollision(b1, b2);
            }
        }
        return null;
    }
}
