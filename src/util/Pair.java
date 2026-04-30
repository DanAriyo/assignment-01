package util;

public record  Pair<X,Y>(X x,Y y) {

    public X getX(){
        return this.x;
    }

    public Y getY(){
        return this.y;
    }
}
