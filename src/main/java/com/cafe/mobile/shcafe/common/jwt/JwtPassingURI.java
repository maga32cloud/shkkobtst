package com.cafe.mobile.shcafe.common.jwt;

public class JwtPassingURI {
    public static final String[] PUBLIC_URIS = {
            "/api/*/member",
            "/api/*/member/login",
            "/api/*/member/cancelWithdraw/*"
    };
}
