// Author: Eason
package adt;

public interface BinarySearchTreeInterface<K extends Comparable<K>, V> {
    boolean insert(K key, V value);
    V search(K key);
    boolean contains(K key);
    int size();
    void clear();
}
