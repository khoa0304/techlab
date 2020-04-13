package cs.basic.datastructure.queue;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import org.junit.Test;

public class QueueTest {

	@Test
	public void testQueue() {
	
		Queue<Integer> queue = new LinkedList<Integer>();
		for(int i = 1; i <= 10 ; i++) {
			queue.add(i);
		}
		while(!queue.isEmpty()) {
			System.out.println(queue.poll());
		}
	}
	
	@Test
	public void testStack() {
	
		Stack<Integer> queue = new Stack<Integer>();
		for(int i = 1; i <= 10 ; i++) {
			queue.push(i);
		}
		while(!queue.isEmpty()) {
			System.out.println(queue.pop());
		}
	}
}
