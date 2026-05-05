package util;

public record Boundary(double x0, double y0, double x1, double y1) {

    public boolean contains(P2d pos, double margin) {
        return pos.x() >= (x0 - margin) && pos.x() <= (x1 + margin) &&
                pos.y() >= (y0 - margin) && pos.y() <= (y1 + margin);
    }


    public boolean contains(P2d pos) {
        return contains(pos, 0.0);
    }

    public double getX0() { return x0; }
    public double getY0() { return y0; }
    public double getX1() { return x1; }
    public double getY1() { return y1; }
}
