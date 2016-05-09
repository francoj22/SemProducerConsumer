package com.cpsemahpore;
/**
 * Create by Franco Gutierrez
 * Solving : http://stackoverflow.com/questions/289434/how-to-make-a-java-thread-wait-for-another-threads-output/37123201#37123201
 * @Date 09-05-2017
 * @Description: Consumer Producer Problem using Semaphores
 */
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SemProducerConsumer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Buffer <Integer> buffer = new Buffer<Integer>(10);
		new Thread(new Producer(buffer)).start();
		new Thread(new Consumer(buffer)).start();
	}

}
//class intermediate between Consumer and Producer
class Buffer<Object> {
	private int max;
	private int size = 0;
	private ArrayList<Object> buffer;
	private Semaphore empty; // control consumer
	private Semaphore full; // control producer
	private Lock lock = new ReentrantLock();

	public Buffer(int s) {
		buffer = new ArrayList<Object>();
		max = s;
		empty = new Semaphore(0);
		full = new Semaphore(max);
	}
	//used on producer thread
	public void put(Object x) {
		try {	//decrement counter from full list
			full.acquire();
		} catch (InterruptedException e) {
		}
		// synchronize update of buffer
		lock.lock();
		try {	
			buffer.add(x);
			size++;
			//value added on list increment it
			empty.release();
		} finally {
			lock.unlock();
		}
	}
	//used on consumer thread
	public Object get() {
		try {	//removing item from buffer
			empty.acquire();
		} catch (InterruptedException e) {
		}
		// synchronize update of buffer
		lock.lock();
		try {
			Object temp = buffer.get(0);
			buffer.remove(0);
			size--;
			//increment for adding more on buffer
			full.release();
			return temp;
		} finally {
			lock.unlock();
		}
	}
}

class Producer implements Runnable {
	Buffer<Integer> buffer;

	public Producer(Buffer<Integer> k) {
		buffer = k;
	}

	public void run() {
		for (int j = 0; j < 50; j++) {
			Integer x = new Integer(j);
			//wait if it is not full.
			buffer.put(x);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
		buffer.put(null); // sentinel value
	}
}

class Consumer implements Runnable {
	Buffer<Integer> buffer;

	public Consumer(Buffer<Integer> k) {
		buffer = k;
	}

	public void run() {
		System.out.print("Buffer data: ");
		Integer x = buffer.get();
		while (x != null) {
			System.out.print(x + " ");
			// set consumption cycle
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
			x = buffer.get();
		}
	}
}
