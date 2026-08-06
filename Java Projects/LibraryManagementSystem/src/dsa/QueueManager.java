package dsa;

import java.util.LinkedList;
import java.util.Queue;

public class QueueManager<T> {
    private final Queue<T> queue = new LinkedList<>();

    public void enqueue(T item) {
        queue.offer(item);
    }

    public T dequeue() {
        return queue.poll();
    }

    public T peek() {
        return queue.peek();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int size() {
        return queue.size();
    }
}
