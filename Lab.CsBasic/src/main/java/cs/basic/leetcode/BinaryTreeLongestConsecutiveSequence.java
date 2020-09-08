package cs.basic.leetcode;

/**
 * 
 * @since 7/14/2019
 *
 */
public class BinaryTreeLongestConsecutiveSequence {

	public static void main(String[] args) {
	
		BinaryTree tree = new BinaryTree();
		tree.root = new Node(12);
		tree.root.right = new Node(13);
		tree.root.right.left = new Node(7);
		tree.root.right.right = new Node(14);
		tree.root.right.right.right = new Node(15);
		
		int longestLength = tree.getLongestConsecutiveSequence(tree.root);
		System.out.println("Longest Length " + longestLength);
	}

	private static class BinaryTree {

		private Node root;

		public int getLongestConsecutiveSequence(Node root) {
			if (root == null)
				return 0;
			Output output = new Output();
			getLongestConsecutiveLength(root, 0, root.data, output);
			return output.result;
		}

		private void getLongestConsecutiveLength(Node root, int currentLength, int expected, Output res) {
			
			if (root == null)return;

			if (root.data == expected) {

				if (res.result <= currentLength) {

					System.out.println(root.data);
					
				}

				currentLength++;
				res.result = currentLength;

			} else
				currentLength = 1;

			getLongestConsecutiveLength(root.left, currentLength, root.data + 1, res);
			getLongestConsecutiveLength(root.right, currentLength, root.data + 1, res);
		}

	}

	private static class Node {
		int data;
		Node left, right;

		Node(int item) {
			data = item;
			left = right = null;
		}
	}

	private static class Output {
		int result = 0;
	}
}
