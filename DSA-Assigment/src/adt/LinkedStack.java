// Author: Henry
package adt;

import java.util.NoSuchElementException;

public class LinkedStack<T> implements StackInterface<T> {
    private Node<T> topNode;
    private int numberOfEntries;

    public void push(T newEntry) {
        if (newEntry == null) throw new IllegalArgumentException("Stack entry cannot be null.");
        topNode = new Node<T>(newEntry, topNode);
        numberOfEntries++;
    }

    public T pop() {
        T result = peek();
        topNode = topNode.getNext();
        numberOfEntries--;
        return result;
    }

    public T peek() {
        if (isEmpty()) throw new NoSuchElementException("The stack is empty.");
        return topNode.getData();
    }

    public boolean isEmpty() { return topNode == null; }
    public int size() { return numberOfEntries; }
    public void clear() { topNode = null; numberOfEntries = 0; }
}
