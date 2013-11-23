package incorrectCompositions.detection.onlineMonitoring.runMonitor;

import java.util.LinkedList;
import java.util.Queue;

public class TaskQueue<T> {

	private int			capacity;
	private Queue<T>	queue	= new LinkedList<T>();

	public TaskQueue(int capacity) {
		this.capacity = capacity;
	}

	public synchronized void put(T element) throws InterruptedException {
		if (queue.size() == capacity) {
			// wait();
			return;// discard it directly. do nothing.
		}

		queue.add(element);
		// notify();
	}

	public synchronized T take() throws InterruptedException {
		if (queue.isEmpty())
			return null;

		T item = queue.remove();
		// notify();
		return item;
	}
}