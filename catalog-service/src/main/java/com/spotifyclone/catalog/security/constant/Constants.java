package com.spotifyclone.catalog.security.constant;

// Güvenlik katmanında tekrar eden rol sabitlerini tek yerde topluyoruz.
public final class Constants {

    // Utility sınıfın örneklenmesini engelliyoruz.
    private Constants() {
    }

    // Spring Security authority prefix'i.
    public static final String ROLE_PREFIX = "ROLE_";
    // İş kuralı rol adları.
    public static final String ADMIN = "ADMIN";
    public static final String USER = "USER";
    // Spring Security tarafında kullanılan tam authority değerleri.
    public static final String ROLE_ADMIN = ROLE_PREFIX + ADMIN;
    public static final String ROLE_USER = ROLE_PREFIX + USER;
}
