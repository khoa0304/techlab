package cs.basic.leetcode;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @since 7/7/19
 *
 */
public class LRUCache {

	private Map<Integer, Node> map;
	private Node dummyHead; 
	private Node dummyTail; 
	private int capacity;

	public LRUCache(int capacity) {
		this.capacity = capacity;
		map = new HashMap<Integer,Node>();
		dummyHead = new Node(-1, -1);
		dummyTail = new Node(-1, -1);
		dummyHead.next = dummyTail; 
		dummyTail.prev = dummyHead; 
	}
	
	public int get(int key) {
		if( !map.containsKey(key) ) {
			return -1;
		}
		Node node = map.get(key);
		promoteToHead(node);
		return node.val;
	}
	
	public void set(int key, int value) {
		Node n;
		// update existing Node; does not alter cache size
		if( map.containsKey(key) ) {
			n = map.get(key);
			n.val = value;   // map.get(n.key) will now return node with new val
			promoteToHead(n);
			
			return;
		}
		if( map.size() == capacity ) {
			Node last = dummyTail.prev;
			map.remove(last.key);
			removeCurrentNode(last);
		}
		n = new Node(key, value);
		addToHead(n);
		map.put(key, n);
	}

	/**
	 * Move given Node to head of queue.
	 */
	private void promoteToHead(Node newNode) {
		if( dummyHead != newNode ) {
			removeCurrentNode(newNode);
			addToHead(newNode);
		}
	}

	/**
	 * Remove given Node from queue.
	 */
	private void removeCurrentNode(Node n) {
		n.prev.next = n.next;
		n.next.prev = n.prev;
	}

	/**
	 * Insert given Node to head of queue.
	 */
	private void addToHead(Node n) {
		// first insert looks like:
		//  -1 <-> -1
		//  -1 <-> n <-> -1
		Node temp = dummyHead.next;
		dummyHead.next = n;
		n.prev = dummyHead;
		n.next = temp;
		n.next.prev = n;
	}
	
	public void printCache() throws Exception {
		if( dummyHead.next == dummyTail ) {
			throw new Exception("empty cache!");
		}
		Node n = dummyHead.next;
		System.out.print("[ ");
		while( n != dummyTail ) {
			System.out.print(n.val + " ");
			n = n.next;
		}
		System.out.println("]");
	}
	
	public class Node {
    	int key;
    	int val;
    	Node prev;
    	Node next;
    		
    	public Node(int key, int val) {
    		this.key = key;
    		this.val = val;
    	}
    }
}
