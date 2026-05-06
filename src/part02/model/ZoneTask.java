package part02.model;

import common.Ball;
import common.CollisionHandler;
import common.Hole;
import common.Referee;
import util.Boundary;
import util.Pair;

import java.util.List;
import java.util.concurrent.Callable;

public class ZoneTask implements Callable<Void> {
    private final List<Ball> ballsInZone;
    private final Ball playerInZone;
    private final Ball botInZone;
    private final CollisionHandler handler;

    public ZoneTask(List<Ball> balls, Ball player, Ball bot, CollisionHandler handler) {
        this.ballsInZone = balls;
        this.playerInZone = player;
        this.botInZone = bot;
        this.handler = handler;
    }

    @Override
    public Void call() {
        int size = ballsInZone.size();
        for (int i = 0; i < size; i++) {
            Ball b1 = ballsInZone.get(i);

            for (int j = i + 1; j < size; j++) {
                handler.resolveCollision(b1, ballsInZone.get(j));
            }

            if (playerInZone != null) {
                handler.resolveCollision(playerInZone, b1);
            }

            if (botInZone != null) {
                handler.resolveCollision(botInZone, b1);
            }
        }

        if (playerInZone != null && botInZone != null) {
            handler.resolveCollision(playerInZone, botInZone);
        }

        return null;
    }
}