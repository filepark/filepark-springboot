package service;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.BucketCrossOriginConfiguration;
import com.amazonaws.services.s3.model.CORSRule;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import config.NaverConfig;

@Service
public class ObjectStorageService {
	private String bucketName;
	private String endPoint;
	private String optimizerEndPoint;
	private AmazonS3 s3Client;

	public ObjectStorageService(NaverConfig naverConfig) {
		this.bucketName = naverConfig.getBucketName();
		this.endPoint = naverConfig.getEndPoint();
		this.optimizerEndPoint = naverConfig.getOptimizerEndPoint();
		s3Client = AmazonS3ClientBuilder.standard()
				.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(naverConfig.getEndPoint(), naverConfig.getRegionName()))
				.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(naverConfig.getAccessKey(), naverConfig.getSecretKey())))
				.build();
		
		List<CORSRule.AllowedMethods> methodRule = new ArrayList<CORSRule.AllowedMethods>();
		methodRule.add(CORSRule.AllowedMethods.PUT);
		methodRule.add(CORSRule.AllowedMethods.GET);
		methodRule.add(CORSRule.AllowedMethods.POST);
		CORSRule rule = new CORSRule().withId("CORSRule")
				.withAllowedMethods(methodRule)
				.withAllowedHeaders(Arrays.asList(new String[] { "*" }))
				.withAllowedOrigins(Arrays.asList(new String[] { "*" }))
				.withMaxAgeSeconds(3600);
		
		List<CORSRule> rules = new ArrayList<CORSRule>();
		rules.add(rule);

		// Add rules to new CORS configuration.
		BucketCrossOriginConfiguration configuration = new BucketCrossOriginConfiguration();
		configuration.setRules(rules);

		// Set the rules to CORS configuration.
		s3Client.setBucketCrossOriginConfiguration(bucketName, configuration);

		// Get the rules for CORS configuration.
		configuration = s3Client.getBucketCrossOriginConfiguration(bucketName);
		
		
//		if (configuration == null) {
//		    System.out.println("Configuration is null.");
//		} else {
//		    System.out.println("Configuration has " + configuration.getRules().size() + " rules\n");
//
//		    for (CORSRule getRule : configuration.getRules()) {
//		        System.out.println("Rule ID: " + getRule.getId());
//		        System.out.println("MaxAgeSeconds: " + getRule.getMaxAgeSeconds());
//		        System.out.println("AllowedMethod: " + getRule.getAllowedMethods());
//		        System.out.println("AllowedOrigins: " + getRule.getAllowedOrigins());
//		        System.out.println("AllowedHeaders: " + getRule.getAllowedHeaders());
//		        System.out.println("ExposeHeader: " + getRule.getExposedHeaders());
//		        System.out.println();
//		    }
//		}
	}

	public String uploadFile(String bucketName, String directoryPath, MultipartFile file) {
//		System.out.println("uploadFile=" + file.getOriginalFilename());

		if (file.isEmpty()) {
			return null;
		}

		try (InputStream fileIn = file.getInputStream()) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_");
			String filename = sdf.format(new Date()) + UUID.randomUUID().toString();
			String[] splitFileName = file.getOriginalFilename().split("\\.");
			filename += splitFileName.length > 1 ? "." + splitFileName[splitFileName.length - 1] : "";

			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentType(file.getContentType());
			metadata.setContentLength(file.getSize());

			PutObjectRequest objectRequest = new PutObjectRequest(bucketName, directoryPath + "/" + filename, fileIn, metadata)
					.withCannedAcl(CannedAccessControlList.PublicRead);

			s3Client.putObject(objectRequest);

			return filename;

		} catch (Exception e) {
			throw new RuntimeException("파일 업로드 오류", e);
		}
	}

	public void deleteFile(String bucketName, String directoryPath, String fileName) {
		String path = directoryPath + "/" + fileName;
		boolean isfind = s3Client.doesObjectExist(bucketName, path);
		if (isfind) {
			s3Client.deleteObject(bucketName, path);
//			System.out.println(path + ":삭제완료");
		}
	}

	public String getBucketName() {
		return bucketName;
	}

	public String getEndPoint() {
		return endPoint;
	}

	public String getOptimizerEndPoint() {
		return optimizerEndPoint;
	}
}
