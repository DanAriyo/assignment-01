package jpf.src-demo;

import gov.nasa.jpf.vm.Verify;
import part01.Board1; // Il tuo pacchetto originale
import part01.V2d;

public class TestJPF {
    public static void main(String[] args) {
        // Setup minimale con 2 palline per evitare l'esplosione degli stati
        Board1 board = new Board1();
        board.initMinimal(2);

        // Thread che simula un Controller (es. Player o Balls)
        Thread controllerThread = new Thread(() -> {
            // JPF proverà sia il caso in cui viene dato l'impulso sia quello in cui non viene dato
            if (Verify.randomBool()) {
                board.applyImpulseToPlayerBall(new V2d(1, 1));
            }
            board.checkCollisions(); // La tua logica di collisione
        });

        controllerThread.start();

        // Il Main Thread prova a fare l'update nello stesso momento
        try {
            board.updateState(16);
            controllerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Se arriviamo qui senza deadlock, il test è passato per questo interleaving
    }
}