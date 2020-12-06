package lab.spark.mllib;

import org.apache.spark.ml.feature.IDF;
import org.apache.spark.ml.feature.IDFModel;
import org.apache.spark.ml.feature.Tokenizer;
import org.apache.spark.ml.feature.HashingTF;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructType;

public class JavaTfIdf {

	public Dataset<Row> tFIdf(Dataset<Row> data, StructType schema, SparkSession spark) {

		// Dataset<Row> sentenceData = spark.createDataFrame(data, schema);

		Tokenizer tokenizer = new Tokenizer().setInputCol("content").setOutputCol("words");
		Dataset<Row> wordsData = tokenizer.transform(data);
		//wordsData.show(false);
		
		int numFeatures = 20;
		HashingTF hashingTF = new HashingTF().setInputCol("words").setOutputCol("rawFeatures")
				.setNumFeatures(numFeatures);

		Dataset<Row> featurizedData = hashingTF.transform(wordsData);
		// alternatively, CountVectorizer can also be used to get term frequency vectors
		//featurizedData.show(false);

		IDF idf = new IDF().setInputCol("rawFeatures").setOutputCol("features");
		IDFModel idfModel = idf.fit(featurizedData);

		Dataset<Row> rescaledData = idfModel.transform(featurizedData);
		//rescaledData.show(false);
	
		Dataset<Row> finalDataset = rescaledData.select("words", "features");
		//finalDataset.show(false);
		
		return finalDataset;
	}
}
