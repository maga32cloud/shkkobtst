package com.cafe.mobile.shcafe.common.type;

public enum ResponseType {
    SUCCESS("200", "성공"),
    BAD_REQUEST("400", "잘못된 요청입니다."),
    INVALID_ARGUMENT("400", "잘못된 파라미터입니다."),
    NEED_LOGIN("401", "로그인이 필요합니다."),
    INVALID_TOKEN("401", "유효하지 않은 토큰입니다."),
    NOT_ALLOWED("403", "권한이 없습니다."),
    DUP_KEY("409", "이미 등록된 데이터입니다."),

    // 비즈니스 예외
    BIZ_ERROR("B000", "실행 중 오류가 발생했습니다."),
    // 회원관련
    NOT_EXIST_MEMBER("B004", "회원정보가 존재하지 않습니다."),
    WITHDRAWING_PROGRESS("B088", "탈퇴 신청한 회원입니다. 탈퇴를 철회하시겠습니까?"),
    WITHDRAWN_MEMBER("B099", "탈퇴한 회원입니다."),
    // 상품관련
    INVALID_PRODUCT("B204", "판매하지 않는 상품입니다."),
    NOT_MATCHED_PRICE("B205", "현재 상품과 가격정보가 일치히지 않습니다."),

    ERROR("999", "오류가 발생했습니다.")
    ;

    private final String code;
    private final String message;

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
