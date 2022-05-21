package com.amazonaws.lambda;

import java.net.URL;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.lambda.thirdparty.org.json.JSONObject;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;

public class GetCompanyLogo implements RequestHandler<APIGatewayProxyRequest, APIGatewayProxyResponse> {
	private String BucketName = "dynamiclogo";
	String accessKey = "";
	String secretKey = "";
	Regions region = Regions.AP_SOUTH_1;
	String objectKey = "Larsen";

	APIGatewayProxyResponse response = new APIGatewayProxyResponse();
	JSONObject output = new JSONObject();

	@Override
	public APIGatewayProxyResponse handleRequest(APIGatewayProxyRequest input, Context context) {
		context.getLogger().log("Input: " + input);

		BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
		final AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(awsCreds)).withRegion(region).build();

		java.util.Date expiration = new java.util.Date();
		long expTimeMillis = expiration.getTime();
		expTimeMillis += 1000 * 60 ;
		expiration.setTime(expTimeMillis);

		GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(BucketName, objectKey)
				.withMethod(HttpMethod.GET).withExpiration(expiration);

		URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);

		output.put("status", true);
		output.put("url :-  ", url.toString());

		response.setBody(output.toString());
		response.setStatusCode(200);
		return response;

	}

}
