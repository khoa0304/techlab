package lab.spark.sample;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import lab.spark.SparkConfigService;

public class MapAndFilter {
	
	public void perform(JavaSparkContext javaSparkContext) {
		
		JavaRDD<Integer> numbersRDD = javaSparkContext.parallelize(Arrays.asList(1, 2, 3, 4, 5), 1);

		JavaRDD<Integer> squaresRDD = numbersRDD.map(n -> n * n * n);
		System.out.println(squaresRDD.collect().toString());

		JavaRDD<Integer> evenRDD = squaresRDD.filter(n -> n % 3 == 0);
		System.out.println(evenRDD.collect().toString());

		JavaRDD<Integer> multipliedRDD = numbersRDD.flatMap(n -> Arrays.asList(n, n * 2, n * 3).iterator());
		System.out.println(multipliedRDD.collect().toString());

		System.out.println("Number of partitions : " + multipliedRDD.getNumPartitions());
	}
	
	private void readLocalFileSystem(SparkSession sparkSession,JavaSparkContext javaSparkContext){

		List<StructField> fields = new ArrayList<>();
		fields.add(DataTypes.createStructField("COL1", DataTypes.StringType, false));
		fields.add(DataTypes.createStructField("COL2", DataTypes.StringType, false));

		StructType schema = DataTypes.createStructType(fields);

		String separator = ",";
		String filePath = "file:////opt/spark-data/csv1.csv";

		SQLContext sqlContext = SQLContext.getOrCreate(sparkSession.sparkContext());

		List<String[]> result = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] vals = line.split(separator);
				result.add(vals);
			}
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			throw new RuntimeException(ex);
		}

		JavaRDD<String[]> jRdd = javaSparkContext.parallelize(result);
		JavaRDD<Row> jRowRdd = jRdd.map(RowFactory::create);
		Dataset<Row> data = sqlContext.createDataFrame(jRowRdd, schema);

		System.out.println(data.count());
		data.show();
	}

}
