package adt;

// Define a public interface for Binary Search Tree 
// extends Comparable<T> to ensure that the elements can be compared for ordering
public interface BinarySearchTreeInterface<T extends Comparable<T>> {
    boolean isEmpty(); // Check if the tree is empty

    int getNumberOfNodes(); // Get the number of nodes in the tree

    void clear(); // Clear the tree

    boolean add(T newEntry); // Add a new entry to the tree

    T search(T entry); // Search for an entry in the tree

    boolean contains(T entry); // Check if the tree contains a specific entry
    
    void inorderTraverse(); // Perform an inorder traversal of the tree
}
