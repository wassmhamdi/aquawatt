package com.siga.camndaapp.exception;

/**
 * @author MHAMDI Wassim 28/02/2025
 * SIGA'S Product
 */


public class UsernameAlreadyUsedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UsernameAlreadyUsedException() {
        super("Login name already used!");
    }
}
