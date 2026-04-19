package com.spotifyclone.catalog.service;

import com.spotifyclone.catalog.dto.ArtistResponse;
import com.spotifyclone.catalog.dto.CreateArtistRequest;
import com.spotifyclone.catalog.model.Artist;
import com.spotifyclone.catalog.repository.ArtistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ArtistServiceTest {

    @Mock
    private ArtistRepository artistRepository;

    @InjectMocks
    private ArtistService artistService;

    private CreateArtistRequest request;
    private Artist savedArtist;

    @BeforeEach
    void setup() {
        request = new CreateArtistRequest("Nova Echoes", "Electronic vibes", null);

        savedArtist = Artist.builder()
                .id(UUID.randomUUID())
                .name(request.name())
                .bio(request.bio())
                .imageUrl("http://image.url")
                .popularity(0)
                .build();
    }

    // ---------------------------------------------------------
    // TEST 1: BAŞARILI KAYIT SENARYOSU (Happy Path)
    // ---------------------------------------------------------
    @Test
    void createArtist_WhenArtistDoesNotExist_ShouldReturnArtistResponse() {
        // 1. GIVEN (Şartları Belirle):
        // Taklitçi repository'ye diyoruz ki: "Sana 'Nova Echoes' ismi var mı diye sorulursa 'Hayır (false)' de."
        when(artistRepository.existsByName(request.name())).thenReturn(false);
        // "Eğer sana bir Artist kaydetmeni söylerlerse, benim hazırladığım 'savedArtist' nesnesini geri dön."
        when(artistRepository.save(any(Artist.class))).thenReturn(savedArtist);

        // 2. WHEN (Eylemi Gerçekleştir):
        // Asıl servisimizi çağırıyoruz ve sonucu bir değişkene alıyoruz.
        ArtistResponse response = artistService.createArtist(request);

        // 3. THEN (Sonuçları Doğrula):
        // A) Gelen cevap boş olmamalı
        assertNotNull(response);
        // B) Dönen cevabın ID'si ve ismi, bizim beklediğimiz verilerle birebir aynı olmalı
        assertEquals(savedArtist.getId(), response.id());
        assertEquals(request.name(), response.name());
        assertEquals(request.bio(), response.bio());

        // C) En önemlisi: Taklitçi repository'nin metodları gerçekten çağrıldı mı diye "Teyit (Verify)" ediyoruz.
        // existsByName metodu tam olarak 1 kere çağrılmış olmalı.
        verify(artistRepository, times(1)).existsByName(request.name());
        // save metodu tam olarak 1 kere çağrılmış olmalı.
        verify(artistRepository, times(1)).save(any(Artist.class));
    }

    // ---------------------------------------------------------
    // TEST 2: HATA FIRLATMA SENARYOSU (Negative Path)
    // ---------------------------------------------------------
    @Test
    void createArtist_WhenArtistAlreadyExists_ShouldThrowException() {
        // 1. GIVEN:
        // Bu sefer taklitçiye diyoruz ki: "Sana bu isim sorulursa 'Evet, var (true)' de."
        when(artistRepository.existsByName(request.name())).thenReturn(true);

        // 2. WHEN & 3. THEN (Hata fırlatılacağı için eylem ve doğrulama iç içe yazılır):
        // Servisi çağırdığımızda bir RuntimeException fırlatılacağını "İddia (Assert)" ediyoruz.
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            artistService.createArtist(request);
        });

        // Fırlatılan hatanın mesajı bizim yazdığımız mesajla tam eşleşiyor mu?
        assertEquals("Bu sanatçı zaten mevcut: Nova Echoes", exception.getMessage());

        // ÇOK KRİTİK DOĞRULAMA: Hata fırlatıldığı için sistem ASLA veritabanına kaydetme (save) işlemi YAPMAMALI!
        verify(artistRepository, never()).save(any(Artist.class));
    }

    // ---------------------------------------------------------
    // TEST 3: LİSTELEME SENARYOSU
    // ---------------------------------------------------------
    @Test
    void getAllArtists_ShouldReturnListOfArtistResponses() {
        // 1. GIVEN:
        // Veritabanından tüm sanatçılar istendiğinde, içinde 1 tane sanatçı olan bir liste dön.
        when(artistRepository.findAll()).thenReturn(List.of(savedArtist));

        // 2. WHEN:
        List<ArtistResponse> responseList = artistService.getAllArtists();

        // 3. THEN:
        assertNotNull(responseList);
        assertEquals(1, responseList.size()); // Listede 1 eleman olmalı
        assertEquals(savedArtist.getName(), responseList.get(0).name()); // İlk elemanın adı doğru olmalı

        // findAll metodu tam olarak 1 kere çağrılmış olmalı.
        verify(artistRepository, times(1)).findAll();
    }
}
