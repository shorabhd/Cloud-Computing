import java.io.IOException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.lib.IdentityReducer;

public class HadoopSort 
{	
	//Mapper Class
	public static class MapSort extends MapReduceBase implements Mapper<LongWritable, Text, Text, NullWritable>
	{
		public MapSort() 
		{}
		
		@Override
		public void map(LongWritable value, Text key, OutputCollector<Text, NullWritable> output, Reporter r)
				throws IOException 
		{
			output.collect(new Text(key.toString()+" "), NullWritable.get());
			
		}
		
	}
	
	public static void main(String[] args) throws IOException 
	{
		JobConf conf = new JobConf(HadoopSort.class);
		conf.setJobName("HadoopSort");
		
		long start = System.currentTimeMillis();
		
		//Create File I/O Objects
		FileInputFormat.setInputPaths(conf, new Path(args[0]));
		FileOutputFormat.setOutputPath(conf, new Path(args[1]));
		
		//Set Mapper & Reducer
		conf.setMapperClass(MapSort.class);
		conf.setReducerClass(IdentityReducer.class);
		
		//Set Output Key/Value Class
		conf.setMapOutputKeyClass(Text.class);
		conf.setMapOutputValueClass(NullWritable.class);
		
		//Run
		JobClient.runJob(conf);
		
		long end = System.currentTimeMillis();
		System.out.println("Time: "+(end-start)/1000+" secs");
	}
}


