// Author: Henry
package adt;

class Node<T> {
    private final T data;
    private Node<T> next;

    Node(T data, Node<T> next) {
        this.data = data;
        this.next = next;
    }

    T getData() { return data; }
    Node<T> getNext() { return next; }
    void setNext(Node<T> next) { this.next = next; }
}
