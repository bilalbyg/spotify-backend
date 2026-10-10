package com.spotifyclone.catalog.controller;

import com.spotifyclone.catalog.service.SongService;
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

class SongControllerTest {

    private SongService songService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        songService = mock(SongService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new SongController(songService)).build();
    }

    @Test
    void bulkCreateBindsAlbumIdAndMultipleAudioFilesFromMultipartForm() throws Exception {
        UUID albumId = UUID.randomUUID();
        MockMultipartFile songsPart = new MockMultipartFile(
                "songs",
                "blob",
                MediaType.APPLICATION_JSON_VALUE,
                ("[{\"title\":\"Test Song\",\"duration\":180,\"audioIndex\":0},"
                        + "{\"title\":\"Second Song\",\"duration\":210,\"audioIndex\":1}]")
                        .getBytes(StandardCharsets.UTF_8)
        );
        MockMultipartFile audioFile = new MockMultipartFile(
                "audioFiles",
                "test.mp3",
                "audio/mpeg",
                new byte[]{1, 2, 3}
        );
        MockMultipartFile secondAudioFile = new MockMultipartFile(
                "audioFiles",
                "second.mp3",
                "audio/mpeg",
                new byte[]{4, 5, 6}
        );
        when(songService.createManySongs(eq(albumId), anyList(), anyList())).thenReturn(List.of());

        mockMvc.perform(multipart("/api/v1/songs/bulk")
                        .param("albumId", albumId.toString())
                        .file(songsPart)
                        .file(audioFile)
                        .file(secondAudioFile))
                .andExpect(status().isCreated());

        verify(songService).createManySongs(eq(albumId), anyList(), anyList());
    }
}
