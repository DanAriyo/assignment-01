package util;

public final class Boundary{

    private final double x0;
    private final double y0;
    private final double x1;
    private final double y1;

    public Boundary(double x0, double y0, double x1, double y1) {
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
    }

    public boolean contains(P2d pos, double margin) {
        return pos.getX() >= (x0 - margin) && pos.getX() <= (x1 + margin) &&
                pos.getY() >= (y0 - margin) && pos.getY() <= (y1 + margin);
    }


    public boolean contains(P2d pos) {
        return contains(pos, 0.0);
    }

    public double getX0() { return x0; }
    public double getY0() { return y0; }
    public double getX1() { return x1; }
    public double getY1() { return y1; }
}
