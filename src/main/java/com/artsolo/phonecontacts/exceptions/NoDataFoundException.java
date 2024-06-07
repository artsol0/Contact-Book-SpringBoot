package com.artsolo.phonecontacts.exceptions;

public class NoDataFoundException extends RuntimeException {
    public NoDataFoundException(String data, Long id) {
        super(String.format("%s with id '%d' not found", data, id));
    }
}
