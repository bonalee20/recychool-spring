package com.app.recychool.util;

import jakarta.annotation.PostConstruct;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.*;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SmsUtil {

    @Value("${coolsms.api.key}")
    private String smsApiKey;

    @Value("${coolsms.api.secret}")
    private String smsApiSecret;

    @Value("${spring.mail.username}")
    private String emailUsername;

    @Value("${spring.mail.password}")
    private String emailPassword;

    private DefaultMessageService messageService;
    private final JavaMailSenderImpl mailSender;

    @PostConstruct
    private void init() {
        this.messageService = NurigoApp.INSTANCE.initialize(smsApiKey, smsApiSecret, "https://api.coolsms.co.kr");
    }

    // 인증코드 8자리 전송 및 Session에 저장
    public String saveAuthentificationCode(HttpSession session){
        String authentificationCode = RandomStringUtils.randomAlphanumeric(8);
        session.setAttribute("authentificationCode", authentificationCode);
        return authentificationCode;
    }

    // 전달받은 인증코드를 문자로 전송
    public SingleMessageSentResponse sendMessage(String to, String verificationCode){
        Message message = new Message();
        String toPhoneNumber = to.replace("\"", "");

        message.setFrom("01033139339"); // 보내는 사람
        message.setTo(toPhoneNumber); // 받는 사람
        message.setText("[ReCychool]\n 아래의 인증코드를 입력해주세요\n" + verificationCode);
        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
        return response;
    }

    // 전달받은 인증코드를 이메일로 전송
    public void sendEmail(String toEmail, String verificationCode){
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject("[ReCychool] 인증코드 발송 이메일입니다.");
            helper.setText("[ReCychool]\n 아래의 인증코드를 입력해주세요\n" + verificationCode, true); // HTML 가능
            helper.setFrom("recychool@gmail.com", "이승찬");

            mailSender.send(mimeMessage);

        } catch (Exception e) {
            throw new RuntimeException("메일 전송 실패: " + e.getMessage());
        }
    }


}
