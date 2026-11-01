package com.spotifyclone.catalog.jam.api;

import com.jayway.jsonpath.JsonPath;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Date;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * HTTP-seam contract for jam domain errors: clients must be able to tell
 * "unknown session" (404) from "not allowed" (403) from "state conflict"
 * (409). A generic 500 carries no actionable meaning.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class JamApiExceptionMappingTest {

    private static final String TEST_SECRET =
            "dGVzdC1qd3Qtc2VjcmV0LWtleS1mb3ItbG9jYWwtc3ByaW5nLWJvb3QtYXBwbGljYXRpb25z";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void joinWithUnknownInviteLink_returns404() throws Exception {
        mockMvc.perform(post("/api/v1/jam/sessions/join")
                        .header("Authorization", bearerToken(UUID.randomUUID()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"inviteLink\":\"" + UUID.randomUUID() + "\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void endSession_asNonHost_returns403() throws Exception {
        UUID hostId = UUID.randomUUID();
        String sessionId = createSessionAs(hostId);

        mockMvc.perform(post("/api/v1/jam/sessions/{id}/end", sessionId)
                        .header("Authorization", bearerToken(UUID.randomUUID())))
                .andExpect(status().isForbidden());
    }

    @Test
    void endSession_twice_returns409() throws Exception {
        UUID hostId = UUID.randomUUID();
        String sessionId = createSessionAs(hostId);

        mockMvc.perform(post("/api/v1/jam/sessions/{id}/end", sessionId)
                        .header("Authorization", bearerToken(hostId)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/jam/sessions/{id}/end", sessionId)
                        .header("Authorization", bearerToken(hostId)))
                .andExpect(status().isConflict());
    }

    @Test
    void endSession_unknownSession_returns404() throws Exception {
        mockMvc.perform(post("/api/v1/jam/sessions/{id}/end", UUID.randomUUID())
                        .header("Authorization", bearerToken(UUID.randomUUID())))
                .andExpect(status().isNotFound());
    }

    private String createSessionAs(UUID hostId) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/jam/sessions")
                        .header("Authorization", bearerToken(hostId)))
                .andExpect(status().isOk())
                .andReturn();
        return JsonPath.read(result.getResponse().getContentAsString(), "$.sessionId");
    }

    private String bearerToken(UUID userId) {
        String token = Jwts.builder()
                .setSubject("jam-error-test@example.com")
                .claim("uid", userId.toString())
                .claim("role", "USER")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3_600_000))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(TEST_SECRET)))
                .compact();
        return "Bearer " + token;
    }
}
