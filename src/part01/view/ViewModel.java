package part01.view;

import part01.model.Board;
import part01.model.P2d;
import part01.model.Role;
import util.Pair;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

record BallViewInfo(P2d pos, double radius) {}
record HoleViewInfo(P2d pos, double radius){}

public class ViewModel {

	private final ArrayList<BallViewInfo> balls;
	private BallViewInfo player;
	private BallViewInfo bot;
	private Pair<HoleViewInfo,HoleViewInfo> holes;
	private int framePerSec;
	private Pair<Integer,Integer> scoresPair = null;
	private final Lock mutex;
	
	public ViewModel() {
		balls = new ArrayList<>();
		framePerSec = 0;
		holes = new Pair<>(new HoleViewInfo(new P2d(0,0),0),new HoleViewInfo(new P2d(0,0),0));
		mutex = new ReentrantLock();
	}
	
	public void update(Board board, int framePerSec) {
		try{
			this.mutex.lock();
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
		}finally{
			this.mutex.unlock();
		}

	}
	
	public ArrayList<BallViewInfo> getBalls(){
        try{
			this.mutex.lock();
			return new ArrayList<>(balls);
		}finally {
			this.mutex.unlock();
		}
	}

	public int getFramePerSec() {
		try{
			this.mutex.lock();
			return framePerSec;
		}finally {
			this.mutex.unlock();
		}
	}

	public BallViewInfo getPlayerBall() {
		try{
			this.mutex.lock();
			return player;
		}finally {
			this.mutex.unlock();
		}

	}

	public BallViewInfo getBotBall(){
		try{
			this.mutex.lock();
			return bot;
		}finally {
			this.mutex.unlock();
		}
	}

	public Pair<HoleViewInfo,HoleViewInfo> getHoles(){
		try{
			this.mutex.lock();
			return this.holes;
		}finally {
			this.mutex.unlock();
		}
	}

	public Integer getPlayerScore(){
		try{
			this.mutex.lock();
			return this.scoresPair.getX();
		}finally {
			this.mutex.unlock();
		}
	}

	public Integer getBotScore(){
		try{
			this.mutex.lock();
			return this.scoresPair.getY();
		}finally {
			this.mutex.unlock();
		}
	}
	
}
