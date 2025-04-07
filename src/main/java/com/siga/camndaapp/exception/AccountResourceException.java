package com.siga.camndaapp.exception;

/**
 * @author MHAMDI Wassim 06/03/2025
 * SIGA'S Product
 */


public class AccountResourceException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public AccountResourceException() {
        super("Email is already in use!");
    }
}
