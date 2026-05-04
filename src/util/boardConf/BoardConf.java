package util.boardConf;

import part01.model.Ball;
import util.Boundary;
import part01.model.Hole;
import util.Pair;

import java.util.List;

public interface BoardConf {

	Boundary getBoardBoundary();
	
	Ball getPlayerBall();

	Ball getBotBall();
	
	List<Ball> getSmallBalls();

	Pair<Hole, Hole> getHoles();
}
