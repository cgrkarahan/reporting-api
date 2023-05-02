package com.rpdpymnt.reportingapi.util;

import java.util.HashMap;

public final class JwtCache {
    private static HashMap<String, String> cache = new HashMap<>();

    private JwtCache() {
        // non-public constructor to prevent instantiation
    }

    public static void addToken(String key, String token) {
        cache.put(key, token);
    }

    public static String getToken(String key) {
        return cache.get(key);
    }

    public static void removeToken(String key) {
        cache.remove(key);
    }
}