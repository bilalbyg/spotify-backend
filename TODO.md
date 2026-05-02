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
