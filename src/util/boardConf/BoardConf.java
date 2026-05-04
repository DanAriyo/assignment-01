package util.boardConf;

import common.Ball;
import util.Boundary;
import common.Hole;
import util.Pair;

import java.util.List;

public interface BoardConf {

	Boundary getBoardBoundary();
	
	Ball getPlayerBall();

	Ball getBotBall();
	
	List<Ball> getSmallBalls();

	Pair<Hole, Hole> getHoles();
}
