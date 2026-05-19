package common.boardConf;

import common.Ball;
import common.Hole;
import util.*;

import java.util.ArrayList;
import java.util.List;

public class BoardConfDemo implements BoardConf {


    @Override
    public Ball getPlayerBall() {
        return new Ball(1, new P2d(0, 0), 0.05, 1.0, new V2d(0, 0), Role.PLAYER);
    }

    @Override
    public Ball getBotBall() {

        return new Ball(2, new P2d(0, 0), 0.05, 1.0, new V2d(0, 0), Role.BOT);
    }

    @Override
    public List<Ball> getSmallBalls() {
        return new ArrayList<>();
    }

    @Override
    public Boundary getBoardBoundary() {
        return new Boundary(-1.5,-1.0,1.5,1.0);
    }

    @Override
    public Pair<Hole, Hole> getHoles() {
        return new Pair<>(new Hole(new P2d(-1.5,1.0),0.2), new Hole(new P2d(1.5,1.0),0.2));

    }
}