package model.boardConf;

import model.Ball;
import model.Boundary;
import model.Hole;
import util.Pair;

import java.util.List;

public interface BoardConf {

	Boundary getBoardBoundary();
	
	Ball getPlayerBall();

	Ball getBotBall();
	
	List<Ball> getSmallBalls();

	Pair<Hole, Hole> getHoles();
}
