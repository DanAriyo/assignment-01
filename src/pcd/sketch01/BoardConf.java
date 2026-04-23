package pcd.sketch01;

import pcd.sketch01.util.Pair;

import java.util.List;

public interface BoardConf {

	Boundary getBoardBoundary();
	
	Ball getPlayerBall();
	
	List<Ball> getSmallBalls();

	Pair<Hole,Hole> getHoles();
}
