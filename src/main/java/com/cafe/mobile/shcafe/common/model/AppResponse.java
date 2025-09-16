package com.cafe.mobile.shcafe.common.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AppResponse<T> {
    // response code
    private String code;

    // resonse message
    private String message;

    // response data
    private T data;
}