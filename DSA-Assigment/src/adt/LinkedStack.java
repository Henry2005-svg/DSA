// Author: Henry
package adt;

import java.util.NoSuchElementException;

public class LinkedStack<T> implements StackInterface<T> {
    private Node<T> topNode;
    private int numberOfEntries;

    @Override
    public void push(T newEntry) {
        if (newEntry == null) throw new IllegalArgumentException("Stack entry cannot be null.");
        topNode = new Node<T>(newEntry, topNode);
        numberOfEntries++;
    }

    @Override
    public T pop() {
        T result = peek();
        topNode = topNode.getNext();
        numberOfEntries--;
        return result;
    }

    @Override
    public T peek() {
        if (isEmpty()) throw new NoSuchElementException("The stack is empty.");
        return topNode.getData();
    }

    @Override
    public boolean isEmpty() { return topNode == null; }
    @Override
    public int size() { return numberOfEntries; }
    @Override
    public void clear() { topNode = null; numberOfEntries = 0; }
}
