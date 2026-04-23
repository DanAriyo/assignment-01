package pcd.sketch01.model.boardConf;

import pcd.sketch01.model.Ball;
import pcd.sketch01.model.Boundary;
import pcd.sketch01.model.Hole;
import pcd.sketch01.util.Pair;

import java.util.List;

public interface BoardConf {

	Boundary getBoardBoundary();
	
	Ball getPlayerBall();
	
	List<Ball> getSmallBalls();

	Pair<Hole,Hole> getHoles();
}
