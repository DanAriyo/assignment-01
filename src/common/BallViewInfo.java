package common;

import util.P2d;

public class BallViewInfo {
    private final P2d pos;
    private final double radius;

    public BallViewInfo(P2d pos, double radius) {
        this.pos = pos;
        this.radius = radius;
    }

    public P2d pos() { return pos; }
    public double radius() { return radius; }
}
