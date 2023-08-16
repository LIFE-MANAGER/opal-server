package com.lifeManager.opalyouth.dto.messageCertify;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageCertifyResponse {
    private String certifyCode;
    private String requestId;
    private String requestTime;
    private String statusCode;
    private String statusName;

    public MessageCertifyResponse(String certifyCode, String requestId, String requestTime, String statusCode, String statusName) {
        this.certifyCode = certifyCode;
        this.requestId = requestId;
        this.requestTime = requestTime;
        this.statusCode = statusCode;
        this.statusName = statusName;
    }

    public static MessageCertifyResponse naverResponseToCertifyResponse(MessageNaverResponse messageNaverResponse, String certifyCode) {
        return new MessageCertifyResponse(
                certifyCode,
                messageNaverResponse.getRequestId(),
                messageNaverResponse.getRequestTime(),
                messageNaverResponse.getStatusCode(),
                messageNaverResponse.getStatusName()
        );
    }
}
