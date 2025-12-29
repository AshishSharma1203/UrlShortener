package com.example.url.util;

import com.example.url.config.UrlConstants;

import java.security.SecureRandom;

public final class AliasGenerator {



    private static final SecureRandom RANDOM = new SecureRandom();

    private AliasGenerator() {}

    public static String generate(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(UrlConstants.BASE62.charAt(RANDOM.nextInt(UrlConstants.BASE62.length())));
        }
        return sb.toString();
    }
}
