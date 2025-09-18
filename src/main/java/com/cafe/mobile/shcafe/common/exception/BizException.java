package com.cafe.mobile.shcafe.common.exception;

import com.cafe.mobile.shcafe.common.type.ResponseType;
import lombok.Getter;

@Getter
public class BizException extends RuntimeException {
    private final String code;

    public BizException() {
        super(ResponseType.BIZ_ERROR.message());
        this.code = ResponseType.BIZ_ERROR.code();
    }

    public BizException(String message) {
        super(message);
        this.code = ResponseType.BIZ_ERROR.code();
    }

    public BizException(ResponseType error) {
        super(error.message());
        this.code = error.code();
    }

}
