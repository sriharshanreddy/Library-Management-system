package dsa;

import java.util.ArrayDeque;
import java.util.Deque;

public class StackManager<T> {
    private final Deque<T> stack = new ArrayDeque<>();

    public void push(T item) {
        stack.push(item);
    }

    public T pop() {
        return stack.poll();
    }

    public T peek() {
        return stack.peek();
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    public int size() {
        return stack.size();
    }
}
