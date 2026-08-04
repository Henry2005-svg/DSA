// Author: Henry
package adt;

public interface StackInterface<T> {
    void push(T newEntry);
    T pop();
    T peek();
    boolean isEmpty();
    int size();
    void clear();
}
