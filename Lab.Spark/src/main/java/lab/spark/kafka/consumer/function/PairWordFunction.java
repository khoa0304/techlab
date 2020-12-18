package lab.spark.kafka.consumer.function;

import java.io.Serializable;

import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

public class PairWordFunction implements Serializable,PairFunction<String, String, Integer>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Tuple2<String, Integer> call(String word) {
		return new Tuple2<String,Integer>(word, 1);
	}
}
