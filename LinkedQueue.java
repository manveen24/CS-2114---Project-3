import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
 
/**
 * A generic first-in, first-out queue implemented with singly linked nodes.
 * Entries are added at the rear and removed from the front. Null entries
 * are not allowed.
 *
 * @param <T>
 *            the type of entry stored in this queue
 * @author Luke(ltstabler)
 * @version 2026.11.08
 */
public class LinkedQueue<T> {
 
    private Node<T> front;
    private Node<T> rear;
    private int size;
 
    /**
     * Creates an empty queue.
     */
    public LinkedQueue() {
        front = null;
        rear = null;
        size = 0;
    }
 
 
    /**
     * Adds an entry to the rear of this queue.
     *
     * @param entry
     *            the entry to add
     * @throws IllegalArgumentException
     *             if entry is null; the queue is left unchanged
     */
    public void enqueue(T entry) {
        if (entry == null) {
            throw new IllegalArgumentException("Cannot enqueue a null entry.");
        }
        Node<T> newNode = new Node<>(entry);
        if (isEmpty()) {
            front = newNode;
        }
        else {
            rear.next = newNode;
        }
        rear = newNode;
        size++;
    }
 
 
    /**
     * Removes and returns the entry at the front of this queue.
     *
     * @return the oldest entry in the queue
     * @throws NoSuchElementException
     *             if the queue is empty; the queue is left unchanged
     */
    public T dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("The queue is empty.");
        }
        T entry = front.data;
        front = front.next;
        size--;
        if (front == null) {
            // The last entry was removed, so the rear must be cleared too.
            rear = null;
        }
        return entry;
    }
 
 
    /**
     * Returns the entry at the front of this queue without removing it.
     *
     * @return the oldest entry in the queue
     * @throws NoSuchElementException
     *             if the queue is empty
     */
    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("The queue is empty.");
        }
        return front.data;
    }
 
 
    /**
     * Returns the number of entries in this queue.
     *
     * @return the number of entries
     */
    public int size() {
        return size;
    }
 
 
    /**
     * Checks whether this queue has no entries.
     *
     * @return true if the queue is empty, false otherwise
     */
    public boolean isEmpty() {
        return size == 0;
    }
 
 
    /**
     * Returns a new list of this queue's entries in FIFO order, front first.
     * The queue is not modified, and changing the returned list does not
     * change the queue.
     *
     * @return a snapshot of the queue's entries
     */
    public List<T> toList() {
        List<T> snapshot = new ArrayList<>(size);
        Node<T> current = front;
        while (current != null) {
            snapshot.add(current.data);
            current = current.next;
        }
        return snapshot;
    }
 
 
    /**
     * A single linked node holding one queue entry and a link to the next
     * node. This is an implementation detail of LinkedQueue.
     *
     * @param <E>
     *            the type of entry stored in the node
     */
    private static class Node<E> {
        private final E data;
        private Node<E> next;
 
        /**
         * Creates a node with the given data and no next node.
         *
         * @param data
         *            the entry to store
         */
        private Node(E data) {
            this.data = data;
            this.next = null;
        }
    }
}