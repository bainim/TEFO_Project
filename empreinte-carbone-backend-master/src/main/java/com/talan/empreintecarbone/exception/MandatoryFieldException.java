package com.talan.empreintecarbone.exception;

public class MandatoryFieldException extends RuntimeException {
    public MandatoryFieldException(String s) {
        super(s);
    }
}
