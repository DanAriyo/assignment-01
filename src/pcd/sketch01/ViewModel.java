package pcd.sketch01;

import pcd.sketch01.util.Pair;

import java.util.ArrayList;

record BallViewInfo(P2d pos, double radius) {}
record HoleViewInfo(P2d pos, double radius){}

public class ViewModel {

	private ArrayList<BallViewInfo> balls;
	private BallViewInfo player;
	private Pair<HoleViewInfo,HoleViewInfo> holes;
	private int framePerSec;
	
	public ViewModel() {
		balls = new ArrayList<BallViewInfo>();
		framePerSec = 0;
		holes = new Pair<>(new HoleViewInfo(new P2d(0,0),0),new HoleViewInfo(new P2d(0,0),0));
	}
	
	public synchronized void update(Board board, int framePerSec) {
		var holesPair = board.getHoles();
		holes = new Pair<>(new HoleViewInfo(holesPair.x().getPos(),holesPair.x().getRadius()),
					new HoleViewInfo(holesPair.y().getPos(),holesPair.y().getRadius()));
		balls.clear();
		for (var b: board.getBalls()) {
			balls.add(new BallViewInfo(b.getPos(), b.getRadius()));
		}
		this.framePerSec = framePerSec;
		var p = board.getPlayerBall();
		player = new BallViewInfo(p.getPos(), p.getRadius());
	}
	
	public synchronized ArrayList<BallViewInfo> getBalls(){
		var copy = new ArrayList<BallViewInfo>();
		copy.addAll(balls);
		return copy;
		
	}

	public synchronized int getFramePerSec() {
		return framePerSec;
	}

	public synchronized BallViewInfo getPlayerBall() {
		return player;
	}

	public synchronized Pair<HoleViewInfo,HoleViewInfo> getHoles(){return this.holes;}
	
}
