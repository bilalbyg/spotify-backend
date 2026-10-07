package com.spotifyclone.catalog.config;

// JWT doğrulaması yapan özel filtre sınıfımız.
import com.spotifyclone.catalog.security.JwtAuthenticationFilter;
// Rol sabitlerini tek noktadan yönetmek için kullanılan sınıf.
import com.spotifyclone.catalog.security.constant.Constants;
// Lombok ile constructor üretmek için kullanılan anotasyon.
import lombok.RequiredArgsConstructor;
// Spring bean tanımı yapmak için kullanılan anotasyon.
import org.springframework.context.annotation.Bean;
// Bu sınıfın bir konfigürasyon sınıfı olduğunu belirtir.
import org.springframework.context.annotation.Configuration;
// CORS için varsayılan ayarları uygulamakta kullanılan yardımcı tip.
import org.springframework.security.config.Customizer;
// @PreAuthorize gibi metot seviyesinde güvenlik anotasyonlarını aktif eder.
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
// HttpSecurity nesnesi ile güvenlik zinciri kurmak için kullanılan tip.
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// Spring Security web güvenlik desteğini aktif eder.
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// Oturum yönetim politikasını seçmek için kullanılan enum.
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.http.HttpMethod;
// Security filter chain bean tipini temsil eder.
import org.springframework.security.web.SecurityFilterChain;
// Özel JWT filtresini bu filtreden önce çalıştırmak için referans alınan sınıf.
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// Sınıfı Spring konfigürasyonu olarak işaretler.
@Configuration
// Web güvenlik konfigürasyonunu aktif eder.
@EnableWebSecurity
// Metot seviyesinde role/authority kontrollerini aktif eder.
@EnableMethodSecurity
// Final alanlar için constructor'ı otomatik üretir.
@RequiredArgsConstructor
// Catalog service güvenlik ayarlarının tanımlandığı sınıf.
public class SecurityConfig {

    // JWT doğrulaması için kullanılacak özel filtre bağımlılığı.
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // Spring Security zincirini bean olarak üretir.
    @Bean
    // HTTP güvenlik kurallarını burada tanımlıyoruz.
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Uygulamayı stateless JWT yapısına geçirip tüm endpointleri kimlik doğrulamaya zorluyoruz.
        http
                // CSRF korumasını API/JWT senaryosu için devre dışı bırakıyoruz.
                .csrf(csrf -> csrf.disable())
                // CORS ayarlarında varsayılan olarak mevcut CorsConfigurationSource bean'ini kullanıyoruz.
                .cors(Customizer.withDefaults())
                // Sunucu tarafında session tutmadan her isteği token ile doğruluyoruz.
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Tüm endpointlerin yalnızca ADMIN rolü ile erişilebilir olmasını sağlıyoruz.
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/songs/**",
                                "/api/v1/albums/**",
                                "/api/v1/artists/**",
                                "/api/v1/playlists/**",
                                "/api/v1/podcasts/**",
                                "/api/v1/episodes/**",
                                "/api/v1/genres/**",
                                "/api/v1/lyrics/**").authenticated()
                        // Tanımlı tüm istekler ADMIN yetkisi gerektirir.
                        .anyRequest().hasAuthority(Constants.ROLE_ADMIN)
                )
                // JWT filtresini kullanıcı adı/şifre filtresinden önce zincire ekliyoruz.
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // Tanımlanan kurallardan SecurityFilterChain nesnesi oluşturup döndürüyoruz.
        return http.build();
    }
}
