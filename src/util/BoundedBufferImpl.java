package util;

import java.util.LinkedList;
import java.util.Optional;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * 
 * Simple implementation of a bounded buffer
 * as a monitor, using raw mechanisms
 * 
 * @param <Item>
 */
public class BoundedBufferImpl<Item> implements BoundedBuffer<Item> {

	private final LinkedList<Item> buffer;
	private final int maxSize;
	private final Lock mutex;
	private final Condition notFull,notEmpty;

	public BoundedBufferImpl(int size) {
		this.buffer = new LinkedList<Item>();
		this.maxSize = size;
		this.mutex = new ReentrantLock();
		this.notFull = mutex.newCondition();
		this.notEmpty = mutex.newCondition();
	}

	public void put(Item item) throws InterruptedException {
		try{
			this.mutex.lock();
			while(isFull()){
				this.notFull.await();
			}
			buffer.addLast(item);
			this.notEmpty.signalAll();
		} finally {
			this.mutex.unlock();
		}
	}

	public Item get() throws InterruptedException {
		try{
			this.mutex.lock();
			while (isEmpty()) {
				this.notEmpty.await();
			}
			Item item = buffer.removeFirst();
			this.notFull.signalAll();
			return item;
		} finally {
			this.mutex.unlock();
		}

	}

	public Optional<Item> poll() {
		try {
			this.mutex.lock();
			if (buffer.isEmpty()) {
				return Optional.empty();
			}
			Item item = buffer.removeFirst();
			// Segnaliamo che si è liberato un posto,
			// nel caso ci fossero thread in attesa nella put()
			this.notFull.signalAll();
			return Optional.of(item);
		} finally {
			this.mutex.unlock();
		}
	}

	private boolean isFull() {
		try{
			this.mutex.lock();
			return this.buffer.size() == maxSize;
		} finally {
			this.mutex.unlock();
		}
	}

	private boolean isEmpty() throws InterruptedException{
		try{
			this.mutex.lock();
			return this.buffer.isEmpty();
		} finally {
			this.mutex.unlock();
		}
	}
}
