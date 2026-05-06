package part02.model;

import common.Ball;
import common.CollisionHandler;
import util.Boundary;

import java.util.List;
import java.util.concurrent.Callable;

public class ZoneTask implements Callable<Void> {
    private final Boundary zoneBoundary;
    private final List<Ball> ballsInZone;
    private final Ball playerBall;
    private final Ball botBall;
    private final CollisionHandler handler;
    private final double margin;

    public ZoneTask(Boundary boundary, List<Ball> balls, Ball player, Ball bot, CollisionHandler handler, double margin) {
        this.zoneBoundary = boundary;
        this.ballsInZone = balls;
        this.playerBall = player;
        this.botBall = bot;
        this.handler = handler;
        this.margin = margin;
    }

    @Override
    public Void call() {
        // 1. Collisioni tra palline piccole (Data Decomposition)
        for (int i = 0; i < ballsInZone.size(); i++) {
            Ball b1 = ballsInZone.get(i);
            for (int j = i + 1; j < ballsInZone.size(); j++) {
                handler.resolveCollision(b1, ballsInZone.get(j));
            }

            // 2. Collisione Player vs Palline Piccole
            // Facciamo il check solo se il Player è in questa zona (con margine)
            if (zoneBoundary.contains(playerBall.getPos(), margin)) {
                handler.resolveCollision(playerBall, b1);
            }

            // 3. Collisione Bot vs Palline Piccole
            if (zoneBoundary.contains(botBall.getPos(), margin)) {
                handler.resolveCollision(botBall, b1);
            }
        }

        // 4. Collisione Player vs Bot (solo se entrambi in questa zona)
        if (zoneBoundary.contains(playerBall.getPos(), margin) &&
                zoneBoundary.contains(botBall.getPos(), margin)) {
            handler.resolveCollision(playerBall, botBall);
        }

        return null;
    }
}