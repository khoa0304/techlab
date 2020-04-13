package cs.basic.datastructure.tree;

/**
 * 
 * Without Recursion or Stack
 * 
 * @since 7/20/19
 * 
 *
 */
public class InOrderBSTTraversal {

	private Node root;

	void traversal(Node root) {
		Node current, pre;

		if (root == null)
			return;

		current = root;
		while (current != null) {
			if (current.left == null) {
				System.out.print(current.data + " ");
				current = current.right;
			} else {
				/* Find the inorder predecessor of current */
				pre = current.left;
				while (pre.right != null && pre.right != current)
					pre = pre.right;

				/* Make current as right child of its inorder predecessor */
				if (pre.right == null) {
					pre.right = current;
					current = current.left;
				}

				/*
				 * Revert the changes made in the 'if' part to restore the original tree i.e.,
				 * fix the right child of predecessor
				 */
				else {
					pre.right = null;
					System.out.print(current.data + " ");
					current = current.right;
				} /* End of if condition pre->right == NULL */

			} /* End of if condition current->left == NULL */

		} /* End of while */
	}

	public static void main(String args[]) {
		
		InOrderBSTTraversal tree = new InOrderBSTTraversal();
		tree.root = new Node(100);
		tree.root.left = new Node(99);
		tree.root.right = new Node(101);
		tree.root.left.left = new Node(97);
		tree.root.left.right = new Node(98);
		tree.root.right.left = new Node(99);
		tree.root.right.right = new Node(102);

		tree.traversal(tree.root);
	}
}
