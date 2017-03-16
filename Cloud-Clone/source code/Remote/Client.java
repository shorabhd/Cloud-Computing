import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.PurgeQueueRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class Client 
{
	static AmazonDynamoDBClient dynamoDB;
    public static void main(String[] args) throws Exception 
    {
    	String queueName = null;
		String wname=null;
		Options options = new Options();
		CommandLineParser parser = new DefaultParser();
		options.addOption( "s", true, "Queue Name");
		options.addOption( "w", true, "Workload File");
		
		try 
		{   
			CommandLine line = parser.parse(options,args);
		    if(line.hasOption("s") && line.hasOption("w")) 
		    {
		    	queueName = line.getOptionValue("s");
		    	wname = line.getOptionValue("w");
		    }
		    else
		    {
		    	System.out.println("Please Enter the Arguments");
		    }
		}
		catch(ParseException exp) 
		{
		    System.out.println( "Unexpected exception:" + exp.getMessage() );
		}
		
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
        
        System.out.println("===========================================");
        System.out.println("CLIENT IS RUNNING");
        System.out.println("===========================================\n");
        
        File filename = new File(wname);
		BufferedReader br = new BufferedReader(new FileReader(filename));
		
		/*SQS and DynamoDB Creation*/
        
        AmazonSQS sqs = new AmazonSQSClient(credentials);
        Region usEast1 = Region.getRegion(Regions.US_EAST_1);
        sqs.setRegion(usEast1);
        
        try 
        {   
            //Create a queue
            CreateQueueRequest createQueueRequest = new CreateQueueRequest(queueName);
            String queueUrlReq = sqs.createQueue(createQueueRequest).getQueueUrl();
            System.out.println("Created a new SQS queue called " + queueName + " .\n");
            long start = System.currentTimeMillis();
            //Put messages
            String s = br.readLine();
    		int lines=0;
    		while(s!=null)
    		{
    			sqs.sendMessage(new SendMessageRequest(queueUrlReq, s+" "+lines));
    			lines++;
    			s = br.readLine();
    		}        
    		System.out.println("Uploaded Messages into " + queueName + " .\n");
    		long middle = System.currentTimeMillis();
    		System.out.println((middle-start)/1000);
    		
    		//Create Response Queue
    		CreateQueueRequest createQueueResponse = new CreateQueueRequest(queueName+"_Response");
            String queueUrlResp = sqs.createQueue(createQueueResponse).getQueueUrl();
            ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrlResp);
            List<Message> messages = null;
            int count=0;
            BufferedWriter bw = new BufferedWriter(new FileWriter("Response.txt"));
            while(true)
            {
            	messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
            	if(!messages.isEmpty())
            	{
            		bw.write(messages.get(0).getBody()+"\n");
            		sqs.deleteMessage(new DeleteMessageRequest(queueUrlResp,messages.get(0).getReceiptHandle()));
            		count++;
            		if(count==lines)
            			break;
            	}
            }
            long end = System.currentTimeMillis();
    		long time = (end-start)/1000;
    		System.out.println("Time Taken By Client: "+time+" secs.");
    		bw.close();
    		sqs.purgeQueue(new PurgeQueueRequest(queueUrlReq));
    		sqs.purgeQueue(new PurgeQueueRequest(queueUrlResp));
        } 
        catch (AmazonServiceException ase) 
        {
            System.out.println("Caught an AmazonServiceException, which means your request made it " +
                    "to Amazon SQS, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } 
        catch (AmazonClientException ace) 
        {
            System.out.println("Caught an AmazonClientException, which means the client encountered " +
                    "a serious internal problem while trying to communicate with SQS, such as not " +
                    "being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
        br.close();
        
    }
}
