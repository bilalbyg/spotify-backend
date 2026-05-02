package com.spotifyclone.catalog.security.annotation;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
// ADMIN yetkisini tek noktadan yönetmek için custom annotation tanımı.
@PreAuthorize("hasAuthority(T(com.spotifyclone.catalog.security.constant.Constants).ROLE_ADMIN)")
public @interface AdminOnly {
}
