package cs.basic.datastructure.tree;

import java.util.Stack;

public class DepthFirstSearchBSTStack {

	public void depthFirstSearch(Node root) {
		Stack<Node> s = new Stack<Node>();
		s.add(root);
		while (s.isEmpty() == false) {
			Node x = s.pop();
			if(x.right!=null) s.add(x.right);
			if(x.left!=null) s.add(x.left);			
			System.out.print(" " + x.data);
		}
	}
	
	public static void main(String args[]){
		Node root = new Node(1);
		root.left = new Node(2);
		root.left.left = new Node(4);
		root.left.right = new Node(5);
		root.right = new Node(3);
		root.right.left = new Node(6);
		root.right.right = new Node(7);
		
		DepthFirstSearchBSTStack b = new DepthFirstSearchBSTStack();
		System.out.println("Depth-First-Search : ");
		b.depthFirstSearch(root);
	}
}
