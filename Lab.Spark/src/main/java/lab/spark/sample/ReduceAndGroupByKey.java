package lab.spark.sample;

import java.util.Arrays;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;

import scala.Tuple2;

public class ReduceAndGroupByKey {

	public void perform(JavaSparkContext javaSparkContext) {
		
		JavaPairRDD<String, Integer> petsRDD = JavaPairRDD
				.fromJavaRDD(javaSparkContext.parallelize(Arrays.asList(new Tuple2<String, Integer>("cat", 1),
						new Tuple2<String, Integer>("dog", 5), new Tuple2<String, Integer>("cat", 3))));

		System.out.println(petsRDD.collect().toString());

		JavaPairRDD<String, Integer> agedPetsRDD = petsRDD.reduceByKey((v1, v2) -> Math.max(v1, v2));
		System.out.println(agedPetsRDD.collect().toString());

		JavaPairRDD<String, Iterable<Integer>> groupedPetsRDD = petsRDD.groupByKey();
		System.out.println(groupedPetsRDD.collect().toString());
	}
}
