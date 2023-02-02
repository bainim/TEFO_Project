package com.talan.empreintecarbone.exception;

public class ItemAlreadyUsedException extends RuntimeException {
    public ItemAlreadyUsedException(String s) {
        super(s);
    }
}
