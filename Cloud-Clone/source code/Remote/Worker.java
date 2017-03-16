import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
//import com.amazonaws.services.dynamodbv2.model.DeleteTableRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class Worker 
{
	static AmazonDynamoDBClient dynamoDB;
	public static void main(String[] args) throws Exception 
	{
		String queueName = null;
		Options options = new Options();
		CommandLineParser parser = new DefaultParser();
		options.addOption( "s", true, "Queue Name");
		options.addOption( "t", true, "Workload File");

		try 
		{   
			CommandLine line = parser.parse(options,args);
			if(line.hasOption("s") && line.hasOption("t")) 
			{
				queueName = line.getOptionValue("s");
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
		System.out.println("WORKER IS RUNNING");
		System.out.println("===========================================\n");

		dynamoDB = new AmazonDynamoDBClient(credentials);
		Region usEast2 = Region.getRegion(Regions.US_EAST_1);
		dynamoDB.setRegion(usEast2);

		AmazonSQS sqs = new AmazonSQSClient(credentials);

		CreateQueueRequest createQueueResponse = new CreateQueueRequest(queueName+"_Response");
		String queueUrl2 = sqs.createQueue(createQueueResponse).getQueueUrl();

		try
		{
			String tableName = queueName;
			// Create table if it does not exist yet
			// Create a table with a primary hash key named 'name', which holds a string
			CreateTableRequest createTableRequest = new CreateTableRequest().withTableName(tableName)
					.withKeySchema(new KeySchemaElement().withAttributeName("name").withKeyType(KeyType.HASH))
					.withAttributeDefinitions(new AttributeDefinition().withAttributeName("name").withAttributeType(ScalarAttributeType.S))
					.withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(10L).withWriteCapacityUnits(10L));
			TableUtils.createTableIfNotExists(dynamoDB, createTableRequest);
			TableUtils.waitUntilActive(dynamoDB, tableName);
			
			CreateQueueRequest createQueueRequest = new CreateQueueRequest(queueName);
			String queueUrl = sqs.createQueue(createQueueRequest).getQueueUrl();
			ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl);
			List<Message> messages = null;
			String str[]=null;
			Map<String,AttributeValue> search = new HashMap<String,AttributeValue>();
			
			long start = System.currentTimeMillis();
			
			while(true)
			{
				messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
				if(!messages.isEmpty())
				{
					str = messages.get(0).getBody().split(" ");
					search.clear();
					search.put("name", new AttributeValue(str[2]));
					GetItemResult getItemResult = dynamoDB.getItem(tableName,search);
					try
					{
						getItemResult.getItem().containsKey(str[2]);
					}
					catch(Exception e)
					{
						dynamoDB.putItem(new PutItemRequest(tableName, newItem(str[2],str[0])));
						Thread.sleep(Long.parseLong(str[1]));
						sqs.sendMessage(new SendMessageRequest(queueUrl2, "0"));
					}
					try
					{	
						// Delete a message
						sqs.deleteMessage(new DeleteMessageRequest(queueUrl, messages.get(0).getReceiptHandle()));
					}
					catch(Exception e)
					{
						break;
					}
				}
				else
				{}
			}
			long end = System.currentTimeMillis();
			long time = (end-start)/1000;
			System.out.println("Time Taken By Worker: "+time+" secs.");
			//dynamoDB.deleteTable(new DeleteTableRequest(tableName));
		}
		catch (AmazonServiceException ase) 
		{
			sqs.sendMessage(new SendMessageRequest(queueUrl2, "1"));
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
			sqs.sendMessage(new SendMessageRequest(queueUrl2, "1"));
			System.out.println("Caught an AmazonClientException, which means the client encountered " +
					"a serious internal problem while trying to communicate with SQS, such as not " +
					"being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}
	}

	private static Map<String, AttributeValue> newItem(String name,String value) 
	{
		Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
		item.put("name", new AttributeValue(name));
		item.put("value", new AttributeValue(value));
		return item;
	}
}
