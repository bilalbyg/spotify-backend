# TODO

## Security / Audit

- `@AdminOnly` anotasyonu için AOP tabanlı audit log ekle.
- `@AfterReturning` ile başarılı çağrılarda şu pattern ile log bas:
  `Kullanıcı adı ADMIN yetkisi ile metot ismi metodunu çalıştırdı @ timestamp`
- Log alanları:
  - kullanıcı bilgisi (`SecurityContextHolder -> Authentication -> principal`)
  - metot adı (`JoinPoint`)
  - zaman damgası (`OffsetDateTime`)
- Opsiyonel: `AccessDeniedException` için `@AfterThrowing` ekleyip yetkisiz denemeleri ayrı logla.
- `generateApiKey` işini ekle (güvenli key üretimi, saklama/maskeleme ve rotasyon stratejisi ile birlikte).
- Sistem ayağa kalkar kalkmaz default bir admin hesabı oluşturma işini ekle; en güvenli yöntemle (idempotent seed, güçlü şifre politikası, secret/env üzerinden başlangıç şifresi, ilk girişte şifre değişimi zorunluluğu) uygulanmalı.
