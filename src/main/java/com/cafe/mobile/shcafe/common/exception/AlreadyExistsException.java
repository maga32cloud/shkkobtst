package com.cafe.mobile.shcafe.common.exception;

import com.cafe.mobile.shcafe.common.type.ResponseType;
import org.springframework.dao.DuplicateKeyException;

public class AlreadyExistsException extends DuplicateKeyException {

    public AlreadyExistsException() {
        super(ResponseType.DUP_KEY.message());
    }

    public AlreadyExistsException(String field) {
        super(String.format("이미 사용 중인 %s입니다", field));
    }
}