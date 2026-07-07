package com.example.programmers.exception;

public class InvalidProposalStateException extends RuntimeException {

    public InvalidProposalStateException(String message) {
        super(message);
    }
}