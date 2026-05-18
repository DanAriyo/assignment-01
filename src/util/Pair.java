package util;

public final class  Pair<X,Y> {

    private final X x;
    private final Y y;

    public Pair(X x, Y y) {
        this.x = x;
        this.y = y;
    }

    public X getX(){
        return this.x;
    }

    public Y getY(){
        return this.y;
    }
}
