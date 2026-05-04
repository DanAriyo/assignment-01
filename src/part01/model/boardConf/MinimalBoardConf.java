package part01.model.boardConf;

import part01.model.*;
import util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MinimalBoardConf implements BoardConf {

	private final AtomicInteger counter = new AtomicInteger(0);


	@Override
	public Ball getPlayerBall() {
		return new Ball(counter.incrementAndGet(),new P2d(-0.5, -0.5), 0.06, 1, new V2d(0,0), Role.PLAYER);
	}

	@Override
	public Ball getBotBall() {
		return new Ball(counter.incrementAndGet(),new P2d(0.5, -0.5), 0.06, 1, new V2d(0,0), Role.BOT);
	}
	@Override
	public List<Ball> getSmallBalls() {
        var balls = new ArrayList<Ball>();
    	var b1 = new Ball(counter.incrementAndGet(),new P2d(0, 0.5), 0.05, 0.75, new V2d(0,0), Role.GENERIC);
    	var b2 = new Ball(counter.incrementAndGet(),new P2d(0.05, 0.55), 0.025, 0.25, new V2d(0,0), Role.GENERIC);
    	balls.add(b1);
    	balls.add(b2);
    	return balls;
	}

	@Override
	public Pair<Hole, Hole> getHoles() {
		return new Pair<>(new Hole(new P2d(-1.5,1.0),0.2), new Hole(new P2d(1.5,1.0),0.2));
	}

	@Override
	public Boundary getBoardBoundary() {
        return new Boundary(-1.5,-1.0,1.5,1.0);
	}

}
