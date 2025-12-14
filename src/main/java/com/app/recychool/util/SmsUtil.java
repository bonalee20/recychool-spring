package com.app.recychool.util;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SmsUtil {
    @Value("${coolsms.api.key}")
    private String smsApiKey;

    @Value("${coolsms.api.secret}")
    private String smsApiSecret;

    private DefaultMessageService messageService;

    @PostConstruct
    private void init() {
        this.messageService = NurigoApp.INSTANCE.initialize(smsApiKey, smsApiSecret, "https://api.coolsms.co.kr");
    }

    public String saveAuthentificationCode(HttpSession session){
        String authentificationCode = RandomStringUtils.randomAlphanumeric(8);
        session.setAttribute("authentificationCode", authentificationCode);
        return authentificationCode;
    }

    public SingleMessageSentResponse sendMessage(String to, String verificationCode){
        Message message = new Message();
        String toPhoneNumber = to.replace("\"", "");

        message.setFrom("01033139339"); // 보내는 사람
        message.setTo(toPhoneNumber); // 받는 사람
        message.setText("[webnest]\n 아래의 인증코드를 입력해주세요\n" + verificationCode);
        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
        return response;
    }
}
