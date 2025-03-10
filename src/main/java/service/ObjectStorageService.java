package service;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import config.NaverConfig;

@Service
public class ObjectStorageService {
	private String bucketName;
	private AmazonS3 s3Client;

	public ObjectStorageService(NaverConfig naverConfig) {
		this.bucketName = naverConfig.getBucketName();
		s3Client = AmazonS3ClientBuilder.standard()
				.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(naverConfig.getEndPoint(),
						naverConfig.getRegionName()))
				.withCredentials(new AWSStaticCredentialsProvider(
						new BasicAWSCredentials(naverConfig.getAccessKey(), naverConfig.getSecretKey())))
				.build();
	}

	public String uploadFile(String bucketName, String directoryPath, MultipartFile file) {
		System.out.println("uploadFile=" + file.getOriginalFilename());

		if (file.isEmpty()) {
			return null;
		}

		try (InputStream fileIn = file.getInputStream()) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_");
			String filename = sdf.format(new Date()) + UUID.randomUUID().toString() + "."
					+ file.getOriginalFilename().split("\\.")[1];

			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentType(file.getContentType());
			metadata.setContentLength(file.getSize());

			PutObjectRequest objectRequest = new PutObjectRequest(bucketName, directoryPath + "/" + filename, fileIn,
					metadata).withCannedAcl(CannedAccessControlList.PublicRead);

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
			System.out.println(path + ":삭제완료");
		}
	}

	public String getBucketName() {
		return bucketName;
	}
}
