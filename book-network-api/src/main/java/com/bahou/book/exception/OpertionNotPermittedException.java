package com.bahou.book.exception;

public class OpertionNotPermittedException extends RuntimeException {
    public OpertionNotPermittedException(String msg) {
        super(msg);
    }
}
