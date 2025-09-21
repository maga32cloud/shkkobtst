package com.cafe.mobile.shcafe.common.jwt;

public class JwtPassingURI {
    public static final String[] PUBLIC_URIS = {
            "/api/*/member",
            "/api/*/member/login",
            "/api/*/member/cancelWithdraw/*",
            // Swagger 관련 경로
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/webjars/**"
    };
}
