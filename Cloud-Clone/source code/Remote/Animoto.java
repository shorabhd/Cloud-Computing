import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class Animoto 
{
	static AmazonS3 s3;
	static String bucketName = "shorabh";
	
	public static void main(String[] args) throws IOException 
	{
		AWSCredentials credentials = null;
        try 
        {
            credentials = new ProfileCredentialsProvider("default").getCredentials();
        } 
        catch (Exception e) 
        {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                    "Please make sure that your credentials file is at the correct " +
                    "location (C:\\Users\\Shorabh\\.aws\\credentials), and is in valid format.",
                    e);
        }
		AmazonSQS sqs = new AmazonSQSClient(credentials);
        Region usEast1 = Region.getRegion(Regions.US_EAST_1);
        sqs.setRegion(usEast1);
        
        s3 = new AmazonS3Client(credentials);
        sqs.setRegion(usEast1);
        
        //Create a queue
        CreateQueueRequest createQueueRequest = new CreateQueueRequest("ANI");
        String myQueueUrl = sqs.createQueue(createQueueRequest).getQueueUrl();
		
        BufferedReader br = null;
		try
		{
			br = new BufferedReader(new FileReader("Animoto.txt"));
			String s = br.readLine();
			Runtime runtime = Runtime.getRuntime();
			while(s!=null)
			{
				Process p = runtime.exec("wget "+ s);
        		p.waitFor();
        		s = br.readLine();
			}
			Process rename = runtime.exec("./rename.sh");
			rename.waitFor();
			
			runtime.exec("ffmpeg -f image2 -i img%03d.jpg movie.mpg");
			
			File movie = new File("movie.mpg");	
	        
			System.out.println("Creating bucket " + bucketName + "\n");
        	s3.createBucket(bucketName);
			
			URL url = put(movie.getName(), movie);
			
			sqs.sendMessage(new SendMessageRequest(myQueueUrl, url.toString()));
		}
		catch(InterruptedException e)
		{}
		catch(AmazonServiceException ase) 
		{
			sqs.sendMessage(new SendMessageRequest(myQueueUrl, "1"));
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } 
		catch (AmazonClientException ace) 
		{
			sqs.sendMessage(new SendMessageRequest(myQueueUrl, "1"));
            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with S3, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
		br.close();
	}
	
	public static URL put(String key, File movie)
	{
		try
		{
			System.out.println("Upload to S3 from a file\n");
            s3.putObject(new PutObjectRequest(bucketName, key, movie));
            
            System.out.println("Generating pre-signed URL.");
			java.util.Date expire = new java.util.Date();
			long milliSeconds = expire.getTime();
			milliSeconds += 1000 * 60 * 60 * 24; // Add 1 day.
			expire.setTime(milliSeconds);

			GeneratePresignedUrlRequest generatePresignedUrlRequest = 
				    new GeneratePresignedUrlRequest(bucketName, key);
			generatePresignedUrlRequest.setMethod(HttpMethod.GET); 
			generatePresignedUrlRequest.setExpiration(expire);

			return s3.generatePresignedUrl(generatePresignedUrlRequest);        
		}
		catch(AmazonServiceException ase) 
		{
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } 
		catch (AmazonClientException ace) 
		{
            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with S3, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
		return null;
	}
}
