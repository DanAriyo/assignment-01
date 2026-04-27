package pcd.sketch01.view;

import pcd.sketch01.model.Board;
import pcd.sketch01.model.P2d;
import pcd.sketch01.model.Role;
import pcd.sketch01.util.Pair;

import java.util.ArrayList;

record BallViewInfo(P2d pos, double radius) {}
record HoleViewInfo(P2d pos, double radius){}

public class ViewModel {

	private ArrayList<BallViewInfo> balls;
	private BallViewInfo player;
	private BallViewInfo bot;
	private Pair<HoleViewInfo,HoleViewInfo> holes;
	private int framePerSec;
	private Pair<Integer,Integer> scoresPair = null;
	
	public ViewModel() {
		balls = new ArrayList<>();
		framePerSec = 0;
		holes = new Pair<>(new HoleViewInfo(new P2d(0,0),0),new HoleViewInfo(new P2d(0,0),0));
	}
	
	public synchronized void update(Board board, int framePerSec) {
		this.scoresPair = new Pair<>(board.getScore(Role.PLAYER),board.getScore(Role.BOT));
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
		var b = board.getBotBall();
		bot = new BallViewInfo(b.getPos(),b.getRadius());
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

	public synchronized BallViewInfo getBotBall(){return bot;}

	public synchronized Pair<HoleViewInfo,HoleViewInfo> getHoles(){return this.holes;}

	public synchronized Integer getPlayerScore(){
		return this.scoresPair.getX();
	}

	public synchronized Integer getBotScore(){
		return this.scoresPair.getY();
	}
	
}
