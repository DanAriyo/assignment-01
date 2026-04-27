package pcd.sketch01.view;

import pcd.sketch01.controller.*;
import pcd.sketch01.controller.commands.*;
import pcd.sketch01.model.P2d;


import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;

public class ViewFrame extends JFrame implements KeyListener {

	private static final Font SCORE_FONT = new Font("Arial", Font.BOLD, 72);
	private static final Font OVERLAY_FONT = new Font("Arial", Font.BOLD, 64);
	private static final int INPUT_TIMEOUT = 1000;

	private VisualiserPanel panel;
	private ViewModel model;
	private RenderSynch sync;
	private Integer firstKey = null;
	private javax.swing.Timer timeoutTimer;
	private final Controller controller;

	public ViewFrame(ViewModel model, int w, int h, Controller controller){
		this.model = model;
		this.controller = controller;
		this.sync = new RenderSynch();

		setTitle("Billiard Sketch 03");
		setSize(w, h + 25);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		panel = new VisualiserPanel(w, h);
		getContentPane().add(panel);

		this.addKeyListener(this);
		this.setFocusable(true);
		this.setFocusTraversalKeysEnabled(false);
	}

	public void render(){
		long nf = sync.nextFrameToRender();
		panel.repaint();
		try {
			sync.waitForFrameRendered(nf);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}

	@Override public void keyTyped(KeyEvent e) {}
	@Override public void keyReleased(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		if (controller.isGameOver()) return; // Blocca input se finito

		int currentKey = e.getKeyCode();
		if (isArrowKey(currentKey)) {
			handleComboInput(currentKey);
		}
	}

	private void handleComboInput(int currentKey) {
		if (firstKey == null) {
			firstKey = currentKey;
			if (timeoutTimer == null) {
				timeoutTimer = new javax.swing.Timer(INPUT_TIMEOUT, arg -> {
					if (firstKey != null) {
						controller.notifyNewCmd(createComboCommand(firstKey, firstKey));
						firstKey = null;
					}
				});
				timeoutTimer.setRepeats(false);
			}
			timeoutTimer.restart();
		} else {
			timeoutTimer.stop();
			controller.notifyNewCmd(createComboCommand(firstKey, currentKey));
			firstKey = null;
		}
	}

	private boolean isArrowKey(int k) {
		return k == KeyEvent.VK_UP || k == KeyEvent.VK_DOWN || k == KeyEvent.VK_LEFT || k == KeyEvent.VK_RIGHT;
	}

	private Cmd createComboCommand(int k1, int k2) {
		if (k1 == KeyEvent.VK_UP && k2 == KeyEvent.VK_RIGHT ||
				k1 == KeyEvent.VK_RIGHT && k2 == KeyEvent.VK_UP) {
			return new MoveUpRightCmd();
		}
		if (k1 == KeyEvent.VK_UP && k2 == KeyEvent.VK_LEFT ||
				k1 == KeyEvent.VK_LEFT && k2 == KeyEvent.VK_UP) {
			return new MoveUpLeftCmd();
		}
		if (k1 == KeyEvent.VK_DOWN && k2 == KeyEvent.VK_LEFT ||
				k1 == KeyEvent.VK_LEFT && k2 == KeyEvent.VK_DOWN) {
			return new MoveDownLeftCmd();
		}
		if (k1 == KeyEvent.VK_DOWN && k2 == KeyEvent.VK_RIGHT ||
				k1 == KeyEvent.VK_RIGHT && k2 == KeyEvent.VK_DOWN) {
			return new MoveDownRightCmd();
		}
		if (k1 == KeyEvent.VK_DOWN && k2 == KeyEvent.VK_DOWN) {
			return new MoveDownCmd();
		}
		if (k1 == KeyEvent.VK_UP && k2 == KeyEvent.VK_UP) {
			return new MoveUpCmd();
		}

		if (k1 == KeyEvent.VK_RIGHT && k2 == KeyEvent.VK_RIGHT) {
			return new MoveRightCmd();
		}

		if (k1 == KeyEvent.VK_LEFT && k2 == KeyEvent.VK_LEFT) {
			return new MoveLeftCmd();
		}
		return new DefaultCmd();
	}

	public class VisualiserPanel extends JPanel {
		private final int ox, oy, delta;

		public VisualiserPanel(int w, int h){
			setPreferredSize(new Dimension(w, h + 25));
			ox = w / 2;
			oy = h / 2;
			delta = Math.min(ox, oy);
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			setupRenderingHints(g2);

			g2.clearRect(0, 0, getWidth(), getHeight());

			drawGrid(g2);

			drawHoles(g2);
			drawSmallBalls(g2);
			drawMainBall(g2, model.getPlayerBall(), "P", Color.BLACK);
			drawMainBall(g2, model.getBotBall(), "B", Color.BLACK);

			drawHUD(g2);

			if (controller.isGameOver()) {
				drawGameOverOverlay(g2);
			}

			sync.notifyFrameRendered();
		}

		private void setupRenderingHints(Graphics2D g2) {
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		}

		private void drawGrid(Graphics2D g2) {
			g2.setColor(Color.LIGHT_GRAY);
			g2.drawLine(ox, 0, ox, oy * 2);
			g2.drawLine(0, oy, ox * 2, oy);
		}

		private void drawHoles(Graphics2D g2) {
			g2.setColor(Color.BLACK);
			g2.setStroke(new BasicStroke(2));
			renderCircle(g2, model.getHoles().getX().pos(), model.getHoles().x().radius(), true);
			renderCircle(g2, model.getHoles().getY().pos(), model.getHoles().y().radius(), true);
		}

		private void drawSmallBalls(Graphics2D g2) {
			g2.setColor(Color.DARK_GRAY);
			g2.setStroke(new BasicStroke(1));
			for (var b : model.getBalls()) {
				renderCircle(g2, b.pos(), b.radius(), false);
			}
		}

		private void drawMainBall(Graphics2D g2, BallViewInfo ball, String label, Color color) {
			if (ball == null) return;
			g2.setColor(color);
			g2.setStroke(new BasicStroke(3));
			int x = (int)(ox + ball.pos().x() * delta);
			int y = (int)(oy - ball.pos().y() * delta);
			int r = (int)(ball.radius() * delta);
			g2.drawOval(x - r, y - r, r * 2, r * 2);
			g2.drawString(label, x - 5, y + 5);
		}

		private void renderCircle(Graphics2D g2, P2d pos, double radius, boolean fill) {
			int x = (int)(ox + pos.x() * delta);
			int y = (int)(oy - pos.y() * delta);
			int r = (int)(radius * delta);
			if (fill) g2.fillOval(x - r, y - r, r * 2, r * 2);
			else g2.drawOval(x - r, y - r, r * 2, r * 2);
		}

		private void drawHUD(Graphics2D g2) {
			g2.setColor(Color.BLACK);
			g2.setFont(getFont().deriveFont(Font.BOLD, 12f));
			g2.drawString("Num Small Balls: " + model.getBalls().size(), 200, 30);
			g2.drawString("FPS: " + model.getFramePerSec(), 200, 50);

			g2.setFont(SCORE_FONT);
			g2.drawString("P: " + model.getPlayerScore(), 50, getHeight() - 50);
			g2.drawString("B: " + model.getBotScore(), getWidth() - 200, getHeight() - 50);
		}

		private void drawGameOverOverlay(Graphics2D g2) {

			g2.setColor(new Color(0, 0, 0, 180));
			g2.fillRect(0, 0, getWidth(), getHeight());


			g2.setColor(Color.WHITE);
			g2.setFont(OVERLAY_FONT);
			String winner = controller.getWinner().isPresent() ? controller.getWinner().get().toString() : "DRAW";

			String text = "GAME OVER";
			String subText = "WINNER: " + winner;

			FontMetrics fm = g2.getFontMetrics();
			g2.drawString(text, (getWidth() - fm.stringWidth(text)) / 2, getHeight() / 2 - 20);

			g2.setFont(OVERLAY_FONT.deriveFont(32f));
			fm = g2.getFontMetrics();
			g2.drawString(subText, (getWidth() - fm.stringWidth(subText)) / 2, getHeight() / 2 + 50);
		}
	}
}
