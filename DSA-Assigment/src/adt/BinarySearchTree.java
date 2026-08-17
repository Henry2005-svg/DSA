package adt;

public class BinarySearchTree<T extends Comparable<T>> 
            implements BinarySearchTreeInterface<T> {

    private TreeNode<T> root;
    private int numberOfNodes;

    private static class TreeNode<T> {
        private T data;
        private TreeNode<T> left;
        private TreeNode<T> right;

        private TreeNode(T data) {
            this.data = data;
        }
    }

    @Override
    public boolean isEmpty() {
        return root == null;
    }

    @Override
    public int getNumberOfNodes() {
        return numberOfNodes;
    }

    @Override
    public void clear() {
        root = null;
        numberOfNodes = 0;
    }

    @Override 
    public boolean add(T newEntry) {
        if (newEntry == null) {
            return false; // Do not allow null entries
        }

        if (root == null) {
            root = new TreeNode<>(newEntry);
            numberOfNodes++;
            return true;
        }

        return addEntry(root, newEntry);
    }

    private boolean addEntry(TreeNode<T> node, T newEntry) {
        int comparison = newEntry.compareTo(node.data);

        if (comparison == 0) {
            return false; // Duplicate entry, do not add
        }

        if (comparison < 0) {
            if (node.left == null) {
                node.left = new TreeNode<>(newEntry);
                numberOfNodes++;
                return true;
            }

            return addEntry(node.left, newEntry);
        } else {
            if (node.right == null) {
                node.right = new TreeNode<>(newEntry);
                numberOfNodes++;
                return true;
            }

            return addEntry(node.right, newEntry);
        }
    }

    @Override
    public T search(T entry) {
        if (entry == null) {
            return null; // Do not allow null entries
        }

        return searchEntry(root, entry);
    }

    private T searchEntry(TreeNode<T> node, T entry) {
        if (node == null) {
            return null; // Entry not found 
        }

        int comparison = entry.compareTo(node.data);

        if (comparison == 0) {
            return node.data; // Entry found
        }

        if (comparison < 0) {
            return searchEntry(node.left, entry);
        }

        return searchEntry(node.right, entry);
    }

    @Override
    public boolean contains(T entry) {
        return search(entry) != null;
    }

    @Override
    public void inorderTraverse() {
        inorder(root);
        System.out.println(); // Print a new line after traversal
    }

    public void inorder(TreeNode<T> node) {
        if (node != null) {
            inorder(node.left);
            System.out.print(node.data);
            inorder(node.right);
        }
    }
}
