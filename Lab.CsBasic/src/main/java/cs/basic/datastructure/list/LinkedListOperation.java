package cs.basic.datastructure.list;

import java.util.List;

public interface LinkedListOperation<T> {

	/**
	 * 
	 * @param t
	 * @return Size of the Linked List
	 */
	int insertion(T t);
	
	/**
	 * 
	 * @param t
	 * @return index position of the Node
	 */
	int deletion(T t);
	
	/**
	 * 
	 * @return A List of all Node
	 */
	List<T> traversal();
	
	/**
	 * 
	 * @param t {@link Node} to be searched
	 * @return index position of the Node
	 */
	int search(T t);
	
	/**
	 * 
	 * @param ascending
	 * @return A List of ordered Node
	 */
	List<T> sorting(boolean ascending);
}
