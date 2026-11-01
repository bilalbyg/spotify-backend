package com.spotifyclone.catalog.jam.api;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * HTTP-seam security test for the jam endpoints. Jam is a user-facing feature,
 * so a regular {@code USER} role must reach {@code /api/v1/jam/**} and the
 * {@code /ws/jam} handshake; only authentication (a valid JWT) is required.
 * The admin catch-all rule must keep guarding the back-office catalog API.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class JamEndpointSecurityTest {

    // Same non-secret fixture as application-test.yml.
    private static final String TEST_SECRET =
            "dGVzdC1qd3Qtc2VjcmV0LWtleS1mb3ItbG9jYWwtc3ByaW5nLWJvb3QtYXBwbGljYXRpb25z";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createSession_userRole_isAllowed() throws Exception {
        mockMvc.perform(post("/api/v1/jam/sessions")
                        .header("Authorization", bearerToken("USER")))
                .andExpect(status().isOk());
    }

    @Test
    void createSession_withoutToken_returns401() throws Exception {
        mockMvc.perform(post("/api/v1/jam/sessions"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void webSocketHandshake_userRole_isNotForbidden() throws Exception {
        MvcResult result = mockMvc.perform(get("/ws/jam")
                        .header("Authorization", bearerToken("USER")))
                .andReturn();

        // The handshake itself fails without an Upgrade header — that is fine;
        // the security chain must not be what stops a regular user.
        assertThat(result.getResponse().getStatus()).isNotEqualTo(403);
    }

    @Test
    void webSocketHandshake_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/ws/jam"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminOnlyCatalogEndpoints_stillRejectUserRole() throws Exception {
        mockMvc.perform(post("/api/v1/songs")
                        .header("Authorization", bearerToken("USER")))
                .andExpect(status().isForbidden());
    }

    private String bearerToken(String role) {
        String token = Jwts.builder()
                .setSubject("jam-security-test@example.com")
                .claim("uid", UUID.randomUUID().toString())
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3_600_000))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(TEST_SECRET)))
                .compact();
        return "Bearer " + token;
    }
}
