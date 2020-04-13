package cs.basic.datastructure.collection;

import java.util.Arrays;
import java.util.List;

public class GenericExample {

	public <T> T returnType( T argument) {
		return argument;
	}
	
	public interface A{
		
	}
	
	public static class A1 implements A{
		
	}
	
	public static class A11 extends A1 {
		
	}
	/*
	 * producer extends, consumer super.
	 */
	public interface GenericInterface<T>{
		
		<T1> T1 returnType( T1 argument);
		
		 T returnType2(T argument);
		 
		 void addA(List<A> newAnimal);
		 
		 void addExtendA1(List<? extends A> newAnimal);
		 void addExtendA11(List<? extends A1> newAnimal);
		 
		 void addSuperA1(List<? super A1> newAnimal);
		 
		 void addSuperA11(List<? super A11> newAnimal);
		 
	}
	
	public static class GenericImpl implements GenericInterface<String>{

		public <T1> T1 returnType(T1 argument) {
			// TODO Auto-generated method stub
			return null;
		}

		public String returnType2(String argument) {
			// TODO Auto-generated method stub
			return null;
		}

		public void addExtendA1(List<? extends A> newAnimal) {
			// TODO Auto-generated method stub
			
		}

		public void addSuperA1(List<? super A1> newAnimal) {
			// TODO Auto-generated method stub
			
		}
		
		public void addSuperA11(List<? super A11> newAnimal) {
				
		}

		public void addExtendA11(List<? extends A1> newAnimal) {
			// TODO Auto-generated method stub
			
		}

		public void addA(List<A> newAnimal) {
			// TODO Auto-generated method stub
			
		}
		
	}
	public static void main(String[] args) {
		
		GenericImpl genericImpl = new GenericImpl();
		A1 a1 = new A1();
		A11 a11 = new A11();
		
		genericImpl.addExtendA1(Arrays.asList(a1));
		genericImpl.addExtendA11(Arrays.asList(a1));
		genericImpl.addExtendA11(Arrays.asList(a11));
		
		//genericImpl.addSuperA1(Arrays.asList(a11));
		//genericImpl.addA(Arrays.asList(a1));
		
		
	}
}
