package com.sirius.xmybatis.exception;

/**
 * SqlSession未找到异常
 */
public class SqlSessionNotFoundException extends RuntimeException {

    private static final String ERROR_MESSAGE = "SqlSession未找到";

    public SqlSessionNotFoundException() {
        super(ERROR_MESSAGE);
    }

    public SqlSessionNotFoundException(String message) {
        super(message);
    }

    public SqlSessionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
