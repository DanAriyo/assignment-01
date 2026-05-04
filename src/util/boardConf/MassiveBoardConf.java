package util.boardConf;

import common.Ball;
import common.Hole;
import util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MassiveBoardConf implements BoardConf {

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
		var ballRadius = 0.01;
        var balls = new ArrayList<Ball>();

    	for (int row = 0; row < 30; row++) {
    		for (int col = 0; col < 150; col++) {
        		var px = -1.0 + col*0.015;
        		var py =  row*0.015;
        		var b = new Ball(counter.incrementAndGet(),new P2d(px, py), ballRadius, 0.25, new V2d(0,0), Role.GENERIC);
            	balls.add(b);    			
    		}
    	}		
    	return balls;
	}

	@Override
	public Pair<Hole, Hole> getHoles() {
		return new Pair<>(new Hole(new P2d(-1.5,1.0),0.2), new Hole(new P2d(1.5,1.0),0.2));


	}

	public Boundary getBoardBoundary() {
        return new Boundary(-1.5,-1.0,1.5,1.0);
	}
}
