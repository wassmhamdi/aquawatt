package com.siga.camndaapp.Controller;

import com.siga.camndaapp.domain.Utilisateur;
import com.siga.camndaapp.dto.request.AuthenticationRequest;
import com.siga.camndaapp.dto.request.RegisterRequest;
import com.siga.camndaapp.dto.request.ResetPassWord;
import com.siga.camndaapp.dto.response.AuthenticationResponse;
import com.siga.camndaapp.service.AuthenticationService;
import com.siga.camndaapp.service.MailService;
import com.siga.camndaapp.service.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * @author MHAMDI Wassim 27/02/2025
 * SIGA'S Product
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor

public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final MailService mailService;



    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest registerRequest) throws MessagingException {

        if (registerRequest!=null) {
            Utilisateur res = userService.register(registerRequest);
            mailService.sendActivationEmail(res);
            return "Registration successful! Please check your email to activate your account.";
        }
        return "Registration refused! Please check your information";

    }

    @GetMapping("/activate")
    public String activateAccount(@RequestParam("token") String token) {
        if (token != null && StringUtils.isNotEmpty(token)) {
             userService.activateAccount(token);
            return "Account activated successfully!";
        }
        return "Invalid or expired activation link.";
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody AuthenticationRequest authenticationRequest) {
        try {
            AuthenticationResponse res = authenticationService.authenticate(authenticationRequest);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            System.out.println(e.toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        }

    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refresh(@RequestParam("token") String refreshToken) {
        try {
            AuthenticationResponse res = authenticationService.refreshToken(refreshToken);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @GetMapping("/validateToken")
    public ResponseEntity<Boolean> validateToken(@RequestParam("token") String token) {
        try {
            Boolean res = authenticationService.validateToken(token);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PostMapping(path = "/begin-reset-password")
    public void requestPasswordReset(@RequestBody RegisterRequest registerRequest) throws MessagingException {
        Optional<Utilisateur> user = userService.requestPasswordReset(registerRequest.getEmail());
        if (user.isPresent()) {
            mailService.sendPasswordResetMail(user.get());
        } else {
            // Pretend the request has been successful to prevent checking which emails really exist
            // but log that an invalid attempt has been made
            log.warn("Password reset requested for non existing mail");
        }
    }

    @PostMapping(path = "/end-reset-password")
    public void finishPasswordReset(@RequestBody ResetPassWord resetPassWord) {


            Optional<Utilisateur> user = userService.completePasswordReset(resetPassWord);

        log.info("***************************----------------- {}", user);

    }



}