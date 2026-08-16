// Author: Eason
package adt;

public class BinarySearchTree<K extends Comparable<K>, V>
        implements BinarySearchTreeInterface<K, V> {
    private TreeNode<K, V> root;
    private int numberOfEntries;

    @Override
    public boolean insert(K key, V value) {
        if (key == null || value == null)
            throw new IllegalArgumentException("BST key and value cannot be null.");
        if (root == null) {
            root = new TreeNode<K, V>(key, value);
            numberOfEntries++;
            return true;
        }
        boolean inserted = insertNode(root, key, value);
        if (inserted) numberOfEntries++;
        return inserted;
    }

    @Override
    public V search(K key) {
        TreeNode<K, V> current = root;
        while (current != null) {
            int comparison = key.compareTo(current.key);
            if (comparison == 0) return current.value;
            current = comparison < 0 ? current.left : current.right;
        }
        return null;
    }

    @Override
    public boolean contains(K key) { return search(key) != null; }
    @Override
    public int size() { return numberOfEntries; }
    @Override
    public void clear() { root = null; numberOfEntries = 0; }

    private boolean insertNode(TreeNode<K, V> current, K key, V value) {
        int comparison = key.compareTo(current.key);
        if (comparison == 0) {
            current.value = value;
            return false;
        }
        if (comparison < 0) {
            if (current.left == null) {
                current.left = new TreeNode<K, V>(key, value);
                return true;
            }
            return insertNode(current.left, key, value);
        }
        if (current.right == null) {
            current.right = new TreeNode<K, V>(key, value);
            return true;
        }
        return insertNode(current.right, key, value);
    }

    private static class TreeNode<K, V> {
        private K key;
        private V value;
        private TreeNode<K, V> left;
        private TreeNode<K, V> right;

        TreeNode(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}
