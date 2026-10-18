package com.spotifyclone.catalog.security;

// Servlet filtre zincirini temsil eden tip.
import jakarta.servlet.FilterChain;
// Filtre içerisinde servlet hatalarını fırlatmak için kullanılan istisna tipi.
import jakarta.servlet.ServletException;
// Gelen HTTP isteğini temsil eden tip.
import jakarta.servlet.http.HttpServletRequest;
// Giden HTTP cevabını temsil eden tip.
import jakarta.servlet.http.HttpServletResponse;
// Lombok ile constructor üretmek için kullanılan anotasyon.
import lombok.RequiredArgsConstructor;
// Spring Security authentication nesnesi üretmek için kullanılan sınıf.
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// Geçerli isteğin security context'ine erişmek için kullanılan yardımcı sınıf.
import org.springframework.security.core.context.SecurityContextHolder;
// Rol bilgisini authority formatına çevirmek için kullanılan sınıf.
import org.springframework.security.core.authority.SimpleGrantedAuthority;
// İstek detaylarını authentication nesnesine bağlamak için kullanılan sınıf.
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
// Bu sınıfı Spring bean'i olarak işaretlemek için kullanılan anotasyon.
import org.springframework.stereotype.Component;
// Her istek için bir kez çalışan filtre taban sınıfı.
import org.springframework.web.filter.OncePerRequestFilter;

// Girdi/çıktı hataları için gerekli tip.
import java.io.IOException;
// Tek authority listesini kolayca oluşturmak için gerekli tip.
import java.util.List;

// Filtrenin Spring tarafından otomatik bulunup yönetilmesini sağlar.
@Component
// Final alanlar için gerekli constructor'ın Lombok tarafından üretilmesini sağlar.
@RequiredArgsConstructor
// JWT doğrulaması yapan filtre sınıfı.
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Token parse/doğrulama işlemlerini yapan servis bağımlılığı.
    private final JwtService jwtService;

    // Üst sınıftaki filtreleme metodunu override ediyoruz.
    @Override
    // Her HTTP isteğinde JWT kontrolünün yapılacağı ana filtre metodu.
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Authorization başlığı yoksa veya Bearer formatında değilse bir sonraki filtreye geçiyoruz.
        final String authHeader = request.getHeader("Authorization");
        // Header beklenen formatta değilse kimlik doğrulama yapmadan akışı devam ettiriyoruz.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // İsteği zincirdeki sonraki filtreye gönderiyoruz.
            filterChain.doFilter(request, response);
            // Bu filtre içindeki işlemleri burada sonlandırıyoruz.
            return;
        }

        // "Bearer " ön ekini atıp token'ın kendisini alıyoruz.
        final String jwt = authHeader.substring(7);

        // Geçersiz token için SecurityContext oluşturmadan akışı devam ettiriyoruz.
        if (!jwtService.isTokenValid(jwt)) {
            // sendError error-dispatch tetikleyip Spring Security tarafından 403'e çevrilebildiğinden
            // geçersiz token durumunu doğrudan 401 olarak döndürüyoruz.
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            // Bu filtre içindeki işlemleri burada sonlandırıyoruz.
            return;
        }

        // Daha önce authentication set edilmemişse token'dan kullanıcı ve rolü okuyup context'e yazıyoruz.
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            // Token subject'inden kullanıcı e-posta bilgisini çıkarıyoruz.
            final String email = jwtService.extractEmail(jwt);
            // Token claim alanından rol bilgisini çıkarıyoruz.
            final String role = jwtService.extractRole(jwt);
            // Token claim alanından kullanıcı UUID'sini çıkarıyoruz.
            final java.util.UUID userId = jwtService.extractUserId(jwt);

            // Role claim'i yoksa yetki üretmeyip isteği kimliksiz bırakıyoruz.
            if (role == null || role.isBlank()) {
                // Kimlik doğrulama claim'i eksik; isteği doğrudan 401 ile sonlandırıyoruz.
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                // Bu filtre içindeki işlemleri burada sonlandırıyoruz.
                return;
            }

            // Kimlik + e-posta + rol bilgisinden Spring Security authentication nesnesi oluşturuyoruz.
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    // Principal olarak kimlik bilgisini (userId + email + role) veriyoruz.
                    new AuthenticatedUser(userId, email, role),
                    // Credentials tarafını kullanmadığımız için null bırakıyoruz.
                    null,
                    // Rolü "ROLE_X" formatına çevirip authority listesine ekliyoruz.
                    List.of(new SimpleGrantedAuthority("ROLE_" + role))
            );
            // İsteğe ait detayları authentication nesnesine bağlıyoruz.
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            // Üretilen authentication nesnesini mevcut security context'e yazıyoruz.
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        // Bu filtre tamamlandıktan sonra isteği zincirdeki sonraki filtreye gönderiyoruz.
        filterChain.doFilter(request, response);
    }
}
