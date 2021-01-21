package crs.fcl.integration.aws4fcl;

import java.io.File;
import java.io.IOException;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class AWSS3App {
	public static void main(String[] args) throws IOException {
        Regions clientRegion = Regions.US_EAST_1;
        String objectHomeDir = "C:\\Temp\\FCL_OBJS_FROM_AWS";

        if (args.length < 2) {
            System.out.println("Please specify a bucket name and key name");
            System.exit(1);
        }

		//String bucketName = args[0];
		String bucketName = "fcl-export";
		//String startAfter = args[1];
		//String startAfter = "staging/FCL-export-2020-03-03.xml";
		String prefix = "production";
		String startAfter = "production/";
      
		BasicAWSCredentials awsCreds = new BasicAWSCredentials("XXXXXXXX",
				"XXXXXXXX");
		AmazonS3 s3 = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(awsCreds))
				.withRegion(clientRegion).build();

		ListObjectsV2Result result = s3
				.listObjectsV2(new ListObjectsV2Request().withBucketName(bucketName).withStartAfter(startAfter).withPrefix(prefix));
		//ListObjectsV2Result result = s3
		//		.listObjectsV2(new ListObjectsV2Request().withBucketName(bucketName).withStartAfter(startAfter));
		for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
			System.out.println(" - " + objectSummary.getKey() + "  " + "(size = " + objectSummary.getSize() + ")");
            System.out.println("Downloading object:" + objectSummary.getKey());
            //File localFile = new File(objectHomeDir + File.separator + objectSummary.getKey());
            //s3.getObject(new GetObjectRequest(bucketName, objectSummary.getKey()), localFile);

		}
		System.out.println("Done!");
	}
	
}
