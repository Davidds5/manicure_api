package br.com.davidds5.manicure_api.exceptions;

public class TimeConflictException extends BusinessException {
    public TimeConflictException(String message) {
        super(message);
    }
}
