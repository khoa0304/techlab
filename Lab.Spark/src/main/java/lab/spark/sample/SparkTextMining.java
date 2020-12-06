package lab.spark.sample;

import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.StructType;

import scala.reflect.ClassTag;

import java.util.Iterator;

import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;

public class SparkTextMining {

	public void perform(Dataset<Row> dataSet) {
		
		dataSet.flatMap(new FlatMapFunction<Row,String>() {

			@Override
			public Iterator<String> call(Row t) throws Exception {
				System.out.println(t.toString());
				return null;
			}
		}, new Encoder<String>() {

			@Override
			public ClassTag<String> clsTag() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public StructType schema() {
				// TODO Auto-generated method stub
				return null;
			}
		});
	}
}
