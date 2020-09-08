package cs.basic.datastructure.list;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author kntran
 */
public class SinglyLinkedList implements LinkedListOperation<Node>{
	
	private Node node; // head of the list

	public int insertion(Node t) {
		
		int index = 0;
		if(this.node == null) {
			this.node =  t;
		}
		else {
			Node last = this.node;
			while(last.next != null) {
				last = last.next;
				index++;
			}
			last.next = t;
		}
		return index;
	}

	public int deletion(Node t) {
		
	
		return 0;
	}

	public List<Node> traversal() {
		
		if(this.node == null) {
			return new ArrayList<Node>(0);
		}
		
		List<Node> list = new ArrayList<Node>();
		list.add(this.node);
		Node nextNode = this.node;
		while(nextNode.next != null) {
			list.add(nextNode.next);
			nextNode = nextNode.next;
		}
		
		return list;
	}

	public int search(Node t) {
		
		int index = 0;
		if(this.node == null) {
			return -1;// not found
		}
		
		//if()
		
		
		return 0;
	}

	public List<Node> sorting(boolean ascending) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
