package com.app.recychool.util;

import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@Component
public class IdentityCrypto {

    private final String SECRET_KEY = "sadfio33h1l1151259jla";

//    hmacSha256 방식으로 토큰처럼 사용가능한 문자열 생성
    public String hmacSha256(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return Base64.getEncoder().encodeToString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("HMAC failed", e);
        }
    }

    public String normalizePhone(String phone) {
        return phone == null ? "" : phone.replaceAll("[^0-9]", "");
    }

    public String normalizeName(String name) {
        return name == null ? "" : name.trim().replaceAll("\\s+", "");
    }

    public String identityKey(String name, String phone, String birth) {
        String payload = normalizeName(name) + "|" + normalizePhone(phone) + "|" + (birth == null ? "" : birth.trim());
        return hmacSha256(payload);
    }

    public String tokenHash(String tokenPlain) {
        return hmacSha256(tokenPlain);
    }

    public String newTokenPlain() {
        return UUID.randomUUID().toString() + UUID.randomUUID(); // 길게
    }
}
