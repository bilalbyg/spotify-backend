package com.spotifyclone.catalog.controller;

import com.spotifyclone.catalog.service.AlbumService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AlbumControllerTest {

    private AlbumService albumService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        albumService = mock(AlbumService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new AlbumController(albumService)).build();
    }

    @Test
    void bulkCreateBindsArtistIdFromMultipartFormField() throws Exception {
        UUID artistId = UUID.randomUUID();
        MockMultipartFile albumsPart = new MockMultipartFile(
                "albums",
                "blob",
                MediaType.APPLICATION_JSON_VALUE,
                "[{\"title\":\"Test Album\",\"releaseYear\":2026,\"imageIndex\":null}]"
                        .getBytes(StandardCharsets.UTF_8)
        );
        when(albumService.createManyAlbums(eq(artistId), anyList(), anyList())).thenReturn(List.of());

        // Spring 6.1: .file() must be called before .param() so the
        // request-builder chain stays typed as
        // MockMultipartHttpServletRequestBuilder for the .file() call.
        mockMvc.perform(multipart("/api/v1/albums/bulk")
                        .file(albumsPart)
                        .param("artistId", artistId.toString()))
                .andExpect(status().isCreated());

        verify(albumService).createManyAlbums(eq(artistId), anyList(), anyList());
    }
}
