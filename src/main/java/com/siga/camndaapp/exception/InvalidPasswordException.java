package com.siga.camndaapp.exception;

/**
 * @author MHAMDI Wassim 06/03/2025
 * SIGA'S Product
 */


public class InvalidPasswordException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    public InvalidPasswordException() {
        super("Password is incorrect");
    }
}