package com.spotifyclone.apigateway.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private static final String SECRET =
            "dGVzdC1qd3Qtc2VjcmV0LWtleS1mb3ItbG9jYWwtc3ByaW5nLWJvb3QtYXBwbGljYXRpb25z";

    private JwtService serviceWithSecret() {
        JwtService service = new JwtService();
        ReflectionTestUtils.setField(service, "secretKey", SECRET);
        return service;
    }

    @Test
    void isValid_acceptsTokenSignedWithTheSecret() {
        JwtService service = serviceWithSecret();
        String token = Jwts.builder()
                .setSubject("user@example.com")
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET)), SignatureAlgorithm.HS256)
                .compact();

        assertThat(service.isValid(token)).isTrue();
    }

    @Test
    void isValid_rejectsGarbageToken() {
        assertThat(serviceWithSecret().isValid("garbage")).isFalse();
    }

    @Test
    void isValid_rejectsTokenSignedWithADifferentKey() {
        JwtService service = serviceWithSecret();
        String otherKey = "b3RoZXItc2VjcmV0LWtleS1mb3ItdGVzdGluZy1wdXJwb3Nlcy1vbmx5LTIwMjY";
        String token = Jwts.builder()
                .setSubject("user@example.com")
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(otherKey)), SignatureAlgorithm.HS256)
                .compact();

        assertThat(service.isValid(token)).isFalse();
    }
}
