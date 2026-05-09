package com.spotifyclone.auth.config;

import com.spotifyclone.auth.model.BootstrapState;
import com.spotifyclone.auth.model.Gender;
import com.spotifyclone.auth.model.Role;
import com.spotifyclone.auth.model.User;
import com.spotifyclone.auth.repository.BootstrapStateRepository;
import com.spotifyclone.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Base64;

// Uygulama acildiginda tek seferlik admin bootstrap islemini yurutur.
@Slf4j
// Spring tarafinda otomatik olarak bean olarak olusturulur.
@Component
// Gerekli tum bagimliliklar icin constructor uretir.
@RequiredArgsConstructor
public class AdminBootstrapApplicationStartedListener implements ApplicationListener<ApplicationStartedEvent> {

    // Bootstrap isleminin calisip calismadigini belirleyen sabit marker key.
    private static final String ADMIN_BOOTSTRAP_KEY = "admin_initialized";

    // Admin olusturma icin gerekli e-posta bilgisini env/property'den okuyoruz.
    @Value("${application.bootstrap.admin.email:}")
    private String adminEmail;

    // Admin olusturma icin gerekli kullanici adini env/property'den okuyoruz.
    @Value("${application.bootstrap.admin.username:}")
    private String adminUsername;

    // Admin olusturma icin gerekli base64 formatli sifreyi env/property'den okuyoruz.
    @Value("${application.bootstrap.admin.password-b64:}")
    private String adminPasswordB64;

    // User verilerine erismek icin repository bagimliligi.
    private final UserRepository userRepository;

    // Bootstrap marker kaydini okumak/yazmak icin repository bagimliligi.
    private final BootstrapStateRepository bootstrapStateRepository;

    // Sifreyi guvenli sekilde hashlemek icin encoder bagimliligi.
    private final PasswordEncoder passwordEncoder;

    // Uygulama baslangic event'i geldiginde bu metot tetiklenir.
    @Override
    // Bootstrap islemini tek transaction icinde yuruterek tutarlilik sagliyoruz.
    @Transactional
    public void onApplicationEvent(ApplicationStartedEvent event) {
        // Marker daha once yazildiysa bootstrap tamamlanmis kabul edip cikiyoruz.
        if (bootstrapStateRepository.existsById(ADMIN_BOOTSTRAP_KEY)) {
            // Tekrarlanan startup durumunu logluyoruz.
            log.info("Admin bootstrap skipped because marker already exists.");
            // Metottan cikiyoruz.
            return;
        }

        // Zorunlu admin ayarlari eksikse sistemi erken fail ederek yanlis kurulumla devam etmiyoruz.
        validateBootstrapInputs();

        try {
            // Marker kaydini once insert ederek coklu instance race durumunda tek kazanan belirliyoruz.
            bootstrapStateRepository.saveAndFlush(
                    // Marker kaydinin kendisini olusturuyoruz.
                    BootstrapState.builder()
                            // Marker key degerini sabit anahtardan veriyoruz.
                            .key(ADMIN_BOOTSTRAP_KEY)
                            // Marker olusma zamanini su an olarak set ediyoruz.
                            .createdAt(OffsetDateTime.now())
                            // Builder sonucu entity nesnesine ceviriyoruz.
                            .build()
            );
        } catch (DataIntegrityViolationException ex) {
            // Baska bir instance marker'i yazdiysa bu instance'in bootstrap yapmasina gerek yok.
            log.info("Admin bootstrap skipped because marker was created by another instance.");
            // Metottan cikiyoruz.
            return;
        }

        // Admin e-postasi zaten varsa ikinci bir admin kaydi olusturmuyoruz.
        if (userRepository.existsByEmail(adminEmail)) {
            // Admin zaten mevcut oldugu icin sadece bilgi logu basiyoruz.
            log.info("Admin bootstrap skipped because user already exists: {}", adminEmail);
            // Metottan cikiyoruz.
            return;
        }

        // Base64 sifreyi decode ederek duz metin sifreye ulasiyoruz.
        String rawPassword = decodeBase64Password(adminPasswordB64);

        // Admin kullanicisini zorunlu alanlarla birlikte olusturuyoruz.
        User adminUser = User.builder()
                // Admin username alanini env/property degerinden set ediyoruz.
                .username(adminUsername)
                // Admin email alanini env/property degerinden set ediyoruz.
                .email(adminEmail)
                // Duz sifreyi veritabanina asla yazmayip BCrypt hash'ini sakliyoruz.
                .password(passwordEncoder.encode(rawPassword))
                // Yetkiyi admin olarak atiyoruz.
                .role(Role.ADMIN)
                // Zorunlu alan oldugu icin sabit bir dogum tarihi veriyoruz.
                .dateOfBirth(LocalDate.of(1970, 1, 1))
                // Zorunlu alan oldugu icin en az varsayim iceren gender degerini veriyoruz.
                .gender(Gender.PREFER_NOT_TO_SAY)
                // Builder sonucu entity nesnesine ceviriyoruz.
                .build();

        // Olusturulan admin kullanicisini kalici olarak kaydediyoruz.
        userRepository.save(adminUser);
        // Basarili bootstrap sonucunu logluyoruz.
        log.info("Default admin user created successfully for email: {}", adminEmail);
    }

    // Bootstrap input degerlerini dogrulayan yardimci metot.
    private void validateBootstrapInputs() {
        // Admin email bos ise kurulum hatasi firlatiyoruz.
        if (adminEmail == null || adminEmail.isBlank()) {
            // Hata mesaji ile eksik konfigurasyonu acikca belirtiyoruz.
            throw new IllegalStateException("Missing required bootstrap property: application.bootstrap.admin.email");
        }
        // Admin username bos ise kurulum hatasi firlatiyoruz.
        if (adminUsername == null || adminUsername.isBlank()) {
            // Hata mesaji ile eksik konfigurasyonu acikca belirtiyoruz.
            throw new IllegalStateException("Missing required bootstrap property: application.bootstrap.admin.username");
        }
        // Admin password-b64 bos ise kurulum hatasi firlatiyoruz.
        if (adminPasswordB64 == null || adminPasswordB64.isBlank()) {
            // Hata mesaji ile eksik konfigurasyonu acikca belirtiyoruz.
            throw new IllegalStateException("Missing required bootstrap property: application.bootstrap.admin.password-b64");
        }
    }

    // Base64 formatli sifreyi guvenli sekilde decode eden yardimci metot.
    private String decodeBase64Password(String base64Password) {
        try {
            // Base64 decode islemi ile byte dizisini elde ediyoruz.
            byte[] decodedBytes = Base64.getDecoder().decode(base64Password);
            // Byte dizisini UTF-8 string'e ceviriyoruz.
            String decodedPassword = new String(decodedBytes, StandardCharsets.UTF_8);
            // Decode sonucu bos gelirse bunu gecersiz kabul ediyoruz.
            if (decodedPassword.isBlank()) {
                // Bos sifreyi engellemek icin acik hata firlatiyoruz.
                throw new IllegalStateException("Decoded admin password is blank.");
            }
            // Gecerli duz metin sifreyi geri donuyoruz.
            return decodedPassword;
        } catch (IllegalArgumentException ex) {
            // Base64 format hatasini daha acik bir mesajla yeniden firlatiyoruz.
            throw new IllegalStateException("Invalid base64 admin password for application.bootstrap.admin.password-b64", ex);
        }
    }
}
