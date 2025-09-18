package com.cafe.mobile.shcafe.common.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AppResponse<T> {
    // response 코드
    private String code;

    // resonse 메세지
    private String message;

    // response 데이터
    private T data;
}