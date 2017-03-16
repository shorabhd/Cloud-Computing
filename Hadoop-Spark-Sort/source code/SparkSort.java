import java.util.Arrays;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

public class SparkSort
{
	public static void main(String args[]) throws Exception  
	{
		//Create Spark and Java Context
		SparkConf sparkConf = new SparkConf().setAppName("SparkSort");
		JavaSparkContext ctx = new JavaSparkContext(sparkConf);
		
		//Read File, Split Lines
		JavaRDD<String> textFile = ctx.textFile(args[0]);
		JavaRDD<String> lines = textFile.flatMap(new FlatMapFunction<String, String>() {
			public Iterable<String> call(String s) { return Arrays.asList(s.split(" \n")); }
		});
		
		//Map Lines to Key/Value
		JavaPairRDD<String, String> pairs = lines.mapToPair(new PairFunction<String, String, String>() 
		{
			public Tuple2<String, String> call(String s) 
			{ 
				return new Tuple2<String, String>(s,""); 
			}
		});
		
		//Sort By Key
		JavaPairRDD<String, String> sort = pairs.sortByKey().coalesce(1);
		
		//Save as Text File
		sort.saveAsTextFile(args[1]);
		ctx.close();
	}
}
