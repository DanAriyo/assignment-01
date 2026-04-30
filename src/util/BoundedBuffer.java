package util;

import java.util.Optional;

public interface BoundedBuffer<Item> {

    void put(Item item) throws InterruptedException;
    
    Item get() throws InterruptedException;

    Optional<Item> poll();
    
}
