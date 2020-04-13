package lab.spark;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class ReadingCSV extends CommonSparkConfig {

	public static void main(String[] args) {
		new ReadingCSV();
	}

	public ReadingCSV() {
		super();
		readCSVFile2();
	}


	public void readCSVFile2() {

		String separator = ",";
		String filePath = "file:////opt/spark-data/csv1.csv";

		// read text file to RDD
		JavaRDD<String> lines = javaSparkContext.textFile(filePath);

		// collect RDD for printing
		for (String line : lines.collect()) {
			System.out.println(line);
		}

		Dataset<Row> data = sparkSession.read().format("csv").option("sep", separator).option("inferSchema", "true")
				.option("header", "true").load(filePath);

		System.out.println(data.count());
		data.show();
	}

}
