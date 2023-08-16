package com.lifeManager.opalyouth.controller;

import com.lifeManager.opalyouth.common.exception.BaseException;
import com.lifeManager.opalyouth.common.response.BaseResponse;
import com.lifeManager.opalyouth.utils.ImageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class ImageController {
    private final ImageUtils imageUtils;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * file upload 를 테스트하는 컨트롤러, 아래와 같이 업로드 기능 사용 가능
     * @param files
     * @return
     */
    @PostMapping("/images/upload")
    public BaseResponse<List<String>> testUploadFile(@RequestParam("files") List<MultipartFile> files) {
        try {
            String folder = LocalDate.now().toString();
            List<String> list = imageUtils.uploadImage(files, folder);
            return new BaseResponse<>(list);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}
