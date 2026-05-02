package com.spotifyclone.catalog.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;

@Service
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    // Token içindeki subject alanından kullanıcı e-posta bilgisini alıyoruz.
    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    // Token içine auth-service tarafından eklenen role claim'ini alıyoruz.
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    // Token imza ve format doğrulamasını yapıyoruz; hata varsa token geçersiz kabul edilir.
    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    // Bu metot token'ı parse ederken imza doğrulamasını da yapar; imza yanlışsa burada exception fırlatılır.
    // Doğrulama başarılı olursa token içindeki tüm claim alanlarını (subject, role, exp vb.) döner.
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // application.yml dosyasındaki secret key değeri metin olarak gelir; JJWT ise Key nesnesi bekler.
    // Bu yüzden önce Base64 decode yapıp sonra HMAC-SHA için uygun bir Key objesine çeviriyoruz.
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
