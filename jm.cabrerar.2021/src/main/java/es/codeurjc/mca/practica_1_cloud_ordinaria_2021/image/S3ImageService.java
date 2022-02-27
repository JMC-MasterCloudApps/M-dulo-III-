package es.codeurjc.mca.practica_1_cloud_ordinaria_2021.image;

import static java.lang.System.getProperty;
import static software.amazon.awssdk.core.sync.RequestBody.fromFile;

import java.io.File;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@Service("storageService")
@Profile("production")
public class S3ImageService implements ImageService {

    private static final String S3_PUBLIC = "https://s3.amazonaws.com/";

    @Value("${amazon.s3.bucket-name}")
    private String bucketName;

    private S3Client s3;

    @Autowired
    S3ImageService(@Value("${amazon.s3.region}") String region,
                   @Value("${amazon.s3.bucket-name}") String bucketName) {

        log.info("Initializing S3 service for region '{}' and bucket '{}'", region, bucketName);
        s3 = S3Client.builder()
            .region(Region.of(region))
            .build();

        createBucket(bucketName);
    }

    @Override
    public String createImage(MultipartFile multiPartFile) {

        try {
            log.info("Uploading image to S3");

            var file = new File(getProperty("java.io.tmpdir") + "/" + multiPartFile.getOriginalFilename());
            multiPartFile.transferTo(file);

            final var fileName = "image_" + UUID.randomUUID() + "_" + multiPartFile.getOriginalFilename();

            var putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

            var response = s3.putObject(putRequest, fromFile(file));

            log.info("SDK HTTP response: {}", response.sdkHttpResponse().statusCode());

            return S3_PUBLIC + bucketName + "/" + fileName;

        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't upload image.", ex);
        }
    }

    @Override
    public void deleteImage(String image) {
        // Not implemented yet
    }

    private void createBucket(String bucketName) {
        try {
            log.info("S3 - Creating bucket '{}'", bucketName);
            var headBucketRequest = HeadBucketRequest.builder().bucket(bucketName).build();
            var response = s3.headBucket(headBucketRequest);

            log.info("SDK HTTP response: {}", response.sdkHttpResponse().statusCode());

        } catch (NoSuchBucketException e) {
            var bucketRequest = CreateBucketRequest.builder().bucket(bucketName).build();
            s3.createBucket(bucketRequest);
        }
    }

}
