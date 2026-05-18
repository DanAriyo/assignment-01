package common;

import util.P2d;

public class HoleViewInfo {
    private final P2d pos;
    private final double radius;

    public HoleViewInfo(P2d pos, double radius) {
        this.pos = pos;
        this.radius = radius;
    }

    public P2d pos() { return pos; }
    public double radius() { return radius; }
}
