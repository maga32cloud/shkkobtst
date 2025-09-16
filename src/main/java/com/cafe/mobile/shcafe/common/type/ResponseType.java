package com.cafe.mobile.shcafe.common.type;

public enum ResponseType {
    SUCCESS("200", "성공"),
    BAD_REQUEST("400", "잘못된 요청입니다.")
    ;

    String code;
    String message;

    ResponseType(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String code() {
        return this.code;
    }

    public String message() {
        return this.message;
    }
}
