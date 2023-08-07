package com.lifeManager.opalyouth.utils;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.lifeManager.opalyouth.common.exception.BaseException;
import com.lifeManager.opalyouth.common.response.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class ImageUtils {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public List<String> uploadImage(List<MultipartFile> multipartFiles, String folderName) throws BaseException  {

        List<String> savedFileUrls = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty()) {
                String contentType = multipartFile.getContentType();

                String savedFileName = folderName + "/" + System.nanoTime();

                ObjectMetadata objectMetadata = new ObjectMetadata();
                objectMetadata.setContentType(contentType);
                objectMetadata.setContentLength(multipartFile.getSize());

                try {
                    amazonS3Client.putObject(new PutObjectRequest(bucket, savedFileName, multipartFile.getInputStream(), objectMetadata).withCannedAcl(CannedAccessControlList.PublicRead));
                    savedFileUrls.add(amazonS3Client.getUrl(bucket, savedFileName).toString());
                } catch (IOException e) {
                    throw new BaseException(BaseResponseStatus.IMAGE_INSERT_ERROR);
                }
            }
        }
        return savedFileUrls;
    }

    public String uploadImage(MultipartFile multipartFile, String folderName) throws BaseException  {

        String fileUrl = null;

        if (!multipartFile.isEmpty()) {
            String contentType = multipartFile.getContentType();

            String savedFileName = folderName + "/" + System.nanoTime();

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(contentType);
            objectMetadata.setContentLength(multipartFile.getSize());

            try {
                amazonS3Client.putObject(new PutObjectRequest(bucket, savedFileName, multipartFile.getInputStream(), objectMetadata).withCannedAcl(CannedAccessControlList.PublicRead));
                fileUrl = amazonS3Client.getUrl(bucket, savedFileName).toString();
            } catch (IOException e) {
                throw new BaseException(BaseResponseStatus.IMAGE_INSERT_ERROR);
            }
        }
        return fileUrl;
    }
}
