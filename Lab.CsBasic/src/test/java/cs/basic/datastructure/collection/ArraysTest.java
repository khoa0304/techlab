package cs.basic.datastructure.collection;

import java.util.Arrays;
import java.util.Comparator;

import org.junit.Test;

public class ArraysTest {

	
	class Car implements Comparable<Car>{
		

		@Override
		public String toString() {
			return "Car [color=" + color + ", year=" + year + ", price=" + price + "]";
		}

		String color = "white";
		int year = 2000;
		double price = 1.0;
		
		public Car(String color, int year, double price) {
	
			this.color = color;
			this.year = year;
			this.price = price;
			
		}

		public int compareTo(Car o) {
			return this.price >= o.price ? 1:-1;
		}
	}
	
	class CompareCarColor implements Comparator<Car>{

		public int compare(Car o1, Car o2) {
			return o1.color.compareTo(o2.color);
		}
	}
	
	class CompareCarYear implements Comparator<Car>{

		public int compare(Car o1, Car o2) {
			return o1.year >= (o2.year) ? 1: -1 ;
		}
	}
	
	@Test
	public void testOrderCarColor() {
		
		Car car1 = new Car("zellow", 2001,1);
		Car car2 = new Car("blue", 2002,2);
		Car car3 = new Car("white", 2003,3);
		Car[] array = new Car[]{car1, car2, car3};
		
		Arrays.sort(array, new CompareCarColor());
		
		for(Car car : array) {
			System.out.println(car);
		}
		System.out.println("\n\n\n");
		
		Arrays.sort(array, new CompareCarYear());
		
		for(Car car : array) {
			System.out.println(car);
		}
		System.out.println("\n\n\n");
		
		Arrays.sort(array);
		
		for(Car car : array) {
			System.out.println(car);
		}
		System.out.println("\n\n\n");
		
		Arrays.sort(array);
		
		for(Car car : array) {
			System.out.println(car);
		}
	}
}
