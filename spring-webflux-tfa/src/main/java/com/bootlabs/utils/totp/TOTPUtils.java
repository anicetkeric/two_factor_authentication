package com.bootlabs.utils.totp;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class TOTPUtils {

    private TOTPUtils() {
    }

    public static String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        Base32 base32 = new Base32();
        return base32.encodeToString(bytes);
    }

    public static String getTOTPCode(String secretKey) {
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(secretKey);
        String hexKey = Hex.encodeHexString(bytes);
        return TOTP.getOTP(hexKey);
    }

    public static String getGoogleAuthenticatorBarCode(String secretKey, String account, String issuer) {
        try {
            final var encoding = "UTF-8";
            final var replacement = "%20";
            return "otpauth://totp/"
                   + URLEncoder.encode(issuer + ":" + account, encoding).replace("+", replacement)
                   + "?secret=" + URLEncoder.encode(secretKey, encoding).replace("+", replacement)
                   + "&issuer=" + URLEncoder.encode(issuer, encoding).replace("+", replacement);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    public static List<String> getRecoveryCodes(int count) {
        Set<String> recoveryCodes = new HashSet<>();

        for (int i = 0; i < count; i++) {
          String code = RandomStringUtils.randomAlphabetic(10);
          recoveryCodes.add(code);
        }
        return recoveryCodes.stream().toList();
    }

}
