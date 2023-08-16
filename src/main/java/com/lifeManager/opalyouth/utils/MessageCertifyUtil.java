package com.lifeManager.opalyouth.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lifeManager.opalyouth.dto.messageCertify.MessageCertifyRequest;
import com.lifeManager.opalyouth.dto.messageCertify.MessageCertifyResponse;
import com.lifeManager.opalyouth.dto.messageCertify.MessageNaverRequest;
import com.lifeManager.opalyouth.dto.messageCertify.MessageNaverResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Component
public class MessageCertifyUtil {

    @Value("${naver-cloud-sms.access-key}")
    private String accessKey;

    @Value("${naver-cloud-sms.secret-key}")
    private String secretKey;

    @Value("${naver-cloud-sms.service-id}")
    private String serviceId;

    @Value("${naver-cloud-sms.sender-phone}")
    private String senderPhoneNum;


    public MessageCertifyResponse sendSms(MessageCertifyRequest messageCertifyRequest) throws JsonProcessingException, InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        String time = Long.toString(System.currentTimeMillis());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-ncp-apigw-timestamp", time);
        headers.set("x-ncp-iam-access-key", accessKey);
        headers.set("x-ncp-apigw-signature-v2", makeSignature(time)); // API Gateway Signature

        String code = createSmsCode();

        List<MessageNaverRequest.MessagesDto> messages = new ArrayList<>();
        messages.add(new MessageNaverRequest.MessagesDto(messageCertifyRequest.getPhoneNumber().replaceAll("-", "")));

        MessageNaverRequest request = MessageNaverRequest.builder()
                .type("SMS")
                .contentType("COMM")
                .from(senderPhoneNum)
                .countryCode("82")
                .content("[오팔청춘]" + "\n" +"인증번호 [" + code + "]를 입력해주세요.")
                .messages(messages)
                .build();

        log.info("request type : {}", request.getType());
        log.info("request contentType : {}", request.getContentType());
        log.info("request from : {}", request.getFrom());
        log.info("request countryCode : {}", request.getCountryCode());
        log.info("request content : {}", request.getContent());
        log.info("request messages : {}", request.getMessages());

        // MessageNaverRequest Object를 Json으로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        String body = objectMapper.writeValueAsString(request);

        // jsonBody와 헤더 조립
        HttpEntity<String> http = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();

        URI uri = UriComponentsBuilder.fromHttpUrl("https://sens.apigw.ntruss.com/sms/v2/services/"+ serviceId +"/messages")
                .build().toUri();

        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        MessageNaverResponse naverResponse = restTemplate.postForObject(uri, http, MessageNaverResponse.class);

        MessageCertifyResponse messageCertifyResponse = MessageCertifyResponse.naverResponseToCertifyResponse(naverResponse, code);
        return messageCertifyResponse;
    }

    /**
     * 인증 코드 생성
     * @return
     */
    public String createSmsCode() {
        StringBuffer code = new StringBuffer();
        Random random = new Random();

        for (int i = 0; i < 5; i++) { // 인증코드 5자리
            code.append((random.nextInt(10)));
        }
        return code.toString();
    }

    /**
     * https://api.ncloud-docs.com/docs/common-ncpapi
     * 위 API 명세서에 따라 작성한 makeSignature 메서드
     * @param time
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     * @throws InvalidKeyException
     */
    public String makeSignature(String time) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        String space = " ";
        String newLine = "\n";
        String method = "POST";
        String url = "/sms/v2/services/"+ this.serviceId+"/messages";
        String accessKey = this.accessKey;
        String secretKey = this.secretKey;

        String message = new StringBuilder()
                .append(method)
                .append(space)
                .append(url)
                .append(newLine)
                .append(time)
                .append(newLine)
                .append(accessKey)
                .toString();

        SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);

        byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
        String encodeBase64String = Base64.encodeBase64String(rawHmac);

        return encodeBase64String;
    }
}
