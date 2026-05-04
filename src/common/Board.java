package common;

import util.Boundary;
import util.V2d;

public interface Board {
    void applyImpulseToPlayerBall(V2d v2d);

    Boundary getBounds();
}
